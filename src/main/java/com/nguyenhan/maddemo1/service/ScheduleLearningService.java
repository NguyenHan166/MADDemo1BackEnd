package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.constants.NotificationCategory;
import com.nguyenhan.maddemo1.constants.StateAssignment;
import com.nguyenhan.maddemo1.constants.StateLesson;
import com.nguyenhan.maddemo1.constants.StateNotification;
import com.nguyenhan.maddemo1.dto.ScheduleLearningDto;
import com.nguyenhan.maddemo1.exception.ResourceAlreadyExistsException;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.mapper.ScheduleLearningMapper;
import com.nguyenhan.maddemo1.model.*;
import com.nguyenhan.maddemo1.repository.CourseRepository;
import com.nguyenhan.maddemo1.repository.NotificationRepository;
import com.nguyenhan.maddemo1.repository.ScheduleLearningRepository;
import com.nguyenhan.maddemo1.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ScheduleLearningService {

    private final ScheduleLearningRepository scheduleLearningRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ScheduleLearningMapper scheduleLearningMapper;
    private final UserService userService;
    private final NotificationRepository notificationRepository;

    public ScheduleLearningService(ScheduleLearningRepository scheduleLearningRepository, UserRepository userRepository, CourseRepository courseRepository, ScheduleLearningMapper scheduleLearningMapper, UserService userService, NotificationRepository notificationRepository) {
        this.scheduleLearningRepository = scheduleLearningRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.scheduleLearningMapper = scheduleLearningMapper;
        this.userService = userService;
        this.notificationRepository = notificationRepository;
    }

    public List<ScheduleLearning> findAll() {
        return scheduleLearningRepository.findAll();
    }

    public List<ScheduleLearning> findAllByUser(User user) {
        return scheduleLearningRepository.findByUser(user);
    }

    public ScheduleLearning fetchScheduleLearning(Long scheduleLearningID) {
        return scheduleLearningRepository.findById(scheduleLearningID).orElseThrow(
                () -> new ResourceNotFoundException("ScheduleLearning", "scheduleLearningID" , scheduleLearningID.toString())
        );
    }

    public ScheduleLearning createScheduleLearning(ScheduleLearningDto scheduleLearningDto) {
        ScheduleLearning scheduleLearning = scheduleLearningMapper.mapToScheduleLearning(scheduleLearningDto,new ScheduleLearning());
        User user = userService.getAuthenticatedUser();
        scheduleLearning.setUser(user);
        Course course = new Course();
        if (scheduleLearningDto.getCourseID() != null) {
            course = courseRepository.findById(scheduleLearningDto.getCourseID()).orElseThrow(
                    ()-> new ResourceNotFoundException("Course", "courseID" , scheduleLearningDto.getCourseID().toString())
            );

            for (ScheduleLearning scheduleLearning1 : course.getScheduleLearnings()){
                if (scheduleLearning1.getTimeStart().equals(scheduleLearningDto.getTimeStart()) || scheduleLearning1.getTimeEnd().equals(scheduleLearningDto.getTimeEnd())){
                    throw new ResourceAlreadyExistsException("ScheduleLearning", "scheduleLearningTime" , scheduleLearningDto.getTimeStart().toString());
                }
            }

            scheduleLearning.setCourse(course);
        }

        return scheduleLearningRepository.save(scheduleLearning);
    }

    public List<ScheduleLearning> getSchedulesByCourse(Course course) {
        return scheduleLearningRepository.findByCourseOrderByTimeStartAsc(course);
    }

    public List<ScheduleLearning> getSchedulesBetweenTimes(Course course ,LocalDateTime startTime, LocalDateTime endTime) {
        return scheduleLearningRepository.findByCourseAndTimeStartGreaterThanEqualAndTimeEndLessThanEqual(course ,startTime, endTime);
    }

    public boolean updateScheduleLearning(Long scheduleLearningID,ScheduleLearningDto scheduleLearningDto) {
        boolean isUpdated = false;
        ScheduleLearning scheduleLearning = scheduleLearningRepository.findById(scheduleLearningID).orElseThrow(
                () -> new ResourceNotFoundException("ScheduleLearning", "scheduleLearningID" , scheduleLearningID.toString())
        );

        User user = userService.getAuthenticatedUser();

        boolean isContains = false;

        for (ScheduleLearning s : user.getScheduleLearnings()){
            if (s.getId().equals(scheduleLearningID)){
                isContains = true;
                break;
            }
        }

        if (!isContains) throw new ResourceNotFoundException("ScheduleLearning of User", "scheduleLearningID" , scheduleLearningID.toString());
        scheduleLearning = scheduleLearningMapper.mapToScheduleLearning(scheduleLearningDto,scheduleLearning);
        scheduleLearningRepository.save(scheduleLearning);
        isUpdated = true;
        return isUpdated;
    }

    public ScheduleLearning updateStateScheduleLearning(Long scheduleLearningID, StateLesson newState) {
        ScheduleLearning scheduleLearning = scheduleLearningRepository.findById(scheduleLearningID).orElseThrow(
                () -> new ResourceNotFoundException("ScheduleLearning", "scheduleLearningID" , scheduleLearningID.toString())
        );
        scheduleLearning.setState(newState);
        return scheduleLearningRepository.save(scheduleLearning);
    }

    public boolean deleteScheduleLearning(Long scheduleLearningID) {
        boolean isDeleted = false;
        ScheduleLearning scheduleLearning = fetchScheduleLearning(scheduleLearningID);
        scheduleLearningRepository.deleteById(scheduleLearningID);

        // xóa noti liên quan
        notificationRepository.deleteByEntityIdAndCategory(scheduleLearningID, NotificationCategory.LESSON);

        isDeleted = true;
        return isDeleted;
    }

    public List<ScheduleLearning> getSchedulesBetweenTimesAndUser(User user, LocalDateTime startTime, LocalDateTime endTime) {
        return scheduleLearningRepository.findByUserAndTimeStartGreaterThanEqualAndTimeEndLessThanEqual(user, startTime, endTime);
    }

    @Scheduled(fixedRate = 1800000) // 30p
    public void updateStateLessonScheduleTask() {
        log.info("Update Status Assignment Start");
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
            List<ScheduleLearning> scheduleLearnings = scheduleLearningRepository.findByUserAndState(user, StateLesson.NOT_YET);
            for (ScheduleLearning scheduleLearning : scheduleLearnings) {
                if (LocalDateTime.now().isAfter(scheduleLearning.getTimeEnd())) {
                    scheduleLearning.setState(StateLesson.ABSENT);
                    Notification notification = new Notification();
                    notification.setEventTime(scheduleLearning.getTimeStart());
                    notification.setName(String.format("Buổi học %s chưa điểm danh", scheduleLearning.getName()));
                    notification.setState(StateNotification.UNREAD);
                    notification.setCategory(NotificationCategory.LESSON);
                    notification.setContent(String.format("Buổi học %s đã kết thúc lúc %s và bạn chưa điểm danh", scheduleLearning.getName(), scheduleLearning.getTimeEnd().toString()));
                    notification.setTimeNoti(LocalDateTime.now().plusMinutes(1)); // Để tạm
                    notification.setEntityId(scheduleLearning.getId());
                    notification.setUser(user);
                    notificationRepository.save(notification);
                }
            }
            scheduleLearningRepository.saveAll(scheduleLearnings);
            log.info("Update Status ScheduleLearning End");
        } else {
            log.info("Please login to update Schedule Learning schedule task");
        }
    }
}
