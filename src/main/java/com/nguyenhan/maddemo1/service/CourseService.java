package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.constants.NotificationCategory;
import com.nguyenhan.maddemo1.constants.StateCourse;
import com.nguyenhan.maddemo1.constants.StateNotification;
import com.nguyenhan.maddemo1.dto.CourseInputDto;
import com.nguyenhan.maddemo1.exception.ResourceAlreadyExistsException;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.mapper.CourseMapper;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.Notification;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.*;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;
    private final UserService userService;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final ScheduleLearningRepository scheduleLearningRepository;
    private final AssignmentRepository assignmentRepository;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository, CourseMapper courseMapper,
                         UserService userService, NotificationRepository notificationRepository, EmailService emailService,
                         ScheduleLearningRepository scheduleLearningRepository, AssignmentRepository assignmentRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.courseMapper = courseMapper;
        this.userService = userService;
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
        this.assignmentRepository = assignmentRepository;
        this.scheduleLearningRepository = scheduleLearningRepository;
    }

    public List<Course> listCourses() {
        return courseRepository.findAll();
    }

    public List<Course> listCoursesOfUser(Long userID) {
        if (userRepository.findById(userID).isEmpty()) {
            throw new ResourceNotFoundException("User", "userID", userID.toString());
        }
        return courseRepository.findByUserId(userID);
    }

    public Course findById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", "id", id.toString()));
    }

    public Course createCourse(CourseInputDto courseDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        for (Course course : user.getCourses()) {
            if (course.getName().equals(courseDto.getName())) {
                throw new ResourceAlreadyExistsException("Course", "name", courseDto.getName());
            }
            if (course.getTimeStart().equals(courseDto.getTimeStart()) && course.getTimeEnd().equals(courseDto.getTimeEnd())) {
                throw new ResourceAlreadyExistsException("Course", "Course Time", course.getTimeStart().toString());
            }
        }

        Course course = new Course();
        courseMapper.mapToCourse(courseDto, course);
        log.atInfo().log("Creating new course " + course.getName());
        return courseRepository.save(course);
    }

    public boolean updateCourse(Long id, CourseInputDto courseDto) {

        boolean isUpdate = false;
        Course course = courseRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Course", "id", id.toString())
        );

        User user = userService.getAuthenticatedUser();

        boolean flag = false;
        for (Course c : user.getCourses()) {
            if (c.getId().equals(course.getId())) {
                flag = true;
                break;
            }
        }
        if (!flag) throw new ResourceNotFoundException("Course", "id", course.getId().toString());

        courseMapper.mapToCourse(courseDto, course);
        courseRepository.save(course);
        isUpdate = true;
        return isUpdate;
    }

    // Lấy các khóa học của người dùng sắp xếp theo thời gian bắt đầu
    public List<Course> getCoursesByUser(User user) {
        return courseRepository.findByUserOrderByTimeStartAsc(user);
    }

    // Lấy các khóa học có trạng thái
    public List<Course> getCoursesByState(User user, StateCourse state) {
        return courseRepository.findByUserAndState(user, state);
    }

    // Lấy các khóa học trong khoảng thời gian cho trước
    public List<Course> getCoursesBetweenTimes(User user, LocalDate startTime, LocalDate endTime) {
        return courseRepository.findByUserAndTimeStartGreaterThanEqualAndTimeEndLessThanEqual(user, startTime, endTime);
    }

    @Transactional
    public boolean deleteCourse(Long id) {
        boolean isDelete = false;
        Course course = courseRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Course", "id", id.toString())
        );
        log.atInfo().log("Delete course start");

        // Xóa noti liên quan
        notificationRepository.deleteByEntityIdAndCategory(id, NotificationCategory.COURSE);
        course.getScheduleLearnings().forEach(
                lesson -> {
                    notificationRepository.deleteByEntityIdAndCategory(lesson.getId(), NotificationCategory.LESSON);
                }
        );

        scheduleLearningRepository.deleteScheduleLearningByCourse(course.getId());
        assignmentRepository.deleteAssignmentByCourse(course.getId());

        courseRepository.deleteCourseById(id);
        boolean isCourseDelete = courseRepository.existsById(id);
        log.atInfo().log("Is delete: " + !isCourseDelete);
        log.atInfo().log("Delete Course End");

        isDelete = true;
        return isDelete;
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Scheduled(fixedRate = 300000)
    public void updateStateCourseScheduleTask() {
        log.info("Update Status Course Start");
        List<Course> courses = courseRepository.findByStateOrState(StateCourse.NOT_YET, StateCourse.ONGOING);
        for (Course course : courses) {
            if (LocalDate.now().isAfter(course.getTimeEnd())) {
                course.setState(StateCourse.END);
                Notification notification = new Notification();
                notification.setEventTime(course.getTimeStart().atStartOfDay());
                notification.setName(String.format("Khóa học %s đã kết thúc", course.getName()));
                notification.setState(StateNotification.UNREAD);
                notification.setCategory(NotificationCategory.COURSE);
                notification.setContent(String.format("Khóa học %s đã kết thúc lúc %s", course.getName(), course.getTimeEnd().toString()));
                notification.setTimeNoti(LocalDateTime.now().plusMinutes(1)); // Để tạm
                notification.setEntityId(course.getId());
                notification.setUser(course.getUser());
                notificationRepository.save(notification);

                try {
                    emailService.sendNotificationEmail(course.getUser().getEmail(), notification, course.getUser());
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            } else if (LocalDate.now().isAfter(course.getTimeStart()) && LocalDate.now().isBefore(course.getTimeEnd())) {
                course.setState(StateCourse.ONGOING);
            }
        }
        courseRepository.saveAll(courses);
        log.info("Update Status Course End");
    }

}

