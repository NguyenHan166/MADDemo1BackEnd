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
import com.nguyenhan.maddemo1.repository.CourseRepository;
import com.nguyenhan.maddemo1.repository.NotificationRepository;
import com.nguyenhan.maddemo1.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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

    public CourseService(CourseRepository courseRepository, UserRepository userRepository, CourseMapper courseMapper, UserService userService, NotificationRepository notificationRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.courseMapper = courseMapper;
        this.userService = userService;
        this.notificationRepository = notificationRepository;
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

    public boolean deleteCourse(Long id) {
        boolean isDelete = false;
        Course course = courseRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Course", "id", id.toString())
        );
        courseRepository.deleteById(id);
        isDelete = true;
        return isDelete;
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Scheduled(fixedRate = 1800000)
    public void updateStateCourseScheduleTask() {
        log.info("Update Status Course Start");
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
            List<Course> courses = courseRepository.findByUserAndStateOrState(user, StateCourse.NOT_YET, StateCourse.ONGOING);
            for (Course course : courses) {
                if (LocalDate.now().isAfter(course.getTimeEnd())) {
                    course.setState(StateCourse.END);
                    Notification notification = new Notification();
                    notification.setEventTime(course.getTimeStart().atStartOfDay());
                    notification.setName(String.format("Course %s is overdue", course.getName()));
                    notification.setState(StateNotification.UNREAD);
                    notification.setCategory(NotificationCategory.COURSE);
                    notification.setContent(String.format("Course %s is overdue at %s", course.getName(), course.getTimeEnd().toString()));
                    notification.setTimeNoti(LocalDateTime.now().plusMinutes(1)); // Để tạm
                    notification.setEntityId(course.getId());
                    notification.setUser(user);
                    notificationRepository.save(notification);
                } else if (LocalDate.now().isAfter(course.getTimeStart()) && LocalDate.now().isBefore(course.getTimeEnd())) {
                    course.setState(StateCourse.ONGOING);
                }
            }
            courseRepository.saveAll(courses);
            log.info("Update Status Course End");
        } else {
            log.info("Please login to update course schedule task");
        }
    }
}
