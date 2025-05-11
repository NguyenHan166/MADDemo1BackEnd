package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.constants.NotificationCategory;
import com.nguyenhan.maddemo1.constants.StateAssignment;
import com.nguyenhan.maddemo1.constants.StateNotification;
import com.nguyenhan.maddemo1.dto.AssignmentDto;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.mapper.AssignmentMapper;
import com.nguyenhan.maddemo1.model.*;
import com.nguyenhan.maddemo1.repository.*;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final UserService userService;
    private final AssignmentMapper assignmentMapper;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public AssignmentService(AssignmentRepository assignmentRepository, CourseRepository courseRepository, UserService userService,
                             AssignmentMapper assignmentMapper, UserRepository userRepository, NotificationRepository notificationRepository, EmailService emailService) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.userService = userService;
        this.assignmentMapper = assignmentMapper;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    public List<Assignment> findAllByUser(User user) {
        return assignmentRepository.findByUserOrderByTimeEndAsc(user);
    }

    public List<Assignment> findAllByCourse(Course course) {
        return assignmentRepository.findByCourseOrderByTimeEndAsc(course);
    }

    public Assignment findById(Long id) {
        return assignmentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Assignment", "Id", id.toString())
        );
    }

    public Assignment create(AssignmentDto assignmentDto) {
        Course course = courseRepository.findById(assignmentDto.getCourseId()).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Id", assignmentDto.getCourseId().toString())
        );

        User user = userService.getAuthenticatedUser();
        Assignment assignment = assignmentMapper.mapToAssignment(assignmentDto, new Assignment());
        assignment.setUser(user);
        assignment.setCourse(course);
        return assignmentRepository.save(assignment);
    }

    public boolean update(Long id, AssignmentDto assignmentDto) {
        boolean isUpdated = false;
        Assignment assignment = assignmentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Assignment", "Id", id.toString())
        );

        Course course = courseRepository.findById(assignmentDto.getCourseId()).orElseThrow(
                () -> new ResourceNotFoundException("Course", "Id", id.toString())
        );

        if (!course.getAssignments().contains(assignment)) {
            throw new ResourceNotFoundException("Assignment in course", "Id", id.toString());
        }

        assignmentMapper.mapToAssignment(assignmentDto, assignment);

        assignmentRepository.save(assignment);
        isUpdated = true;
        return isUpdated;
    }

    @Transactional
    public boolean delete(Long id) {
        boolean isDeleted = false;
        log.info("Nhận yêu cầu xoá assignment ID: {}", id);
        Assignment assignment = assignmentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Assignment", "Id", id.toString())
        );

        log.info("Xoá assignment ID: {}", id);
        assignmentRepository.deleteAssignmentById(id);

        log.info("Xoá notification liên quan đến assignment ID: {}", id);
        notificationRepository.deleteByEntityIdAndCategory(id, NotificationCategory.ASSIGNMENT);

        log.info("Xoá assignment thành công ID: {}", id);
        isDeleted = true;
        return isDeleted;
    }


    @Scheduled(fixedRate = 300000) // 5p
    public void updateStateAssignmentScheduleTask() {
        log.info("Update Status Assignment Start");
        List<Assignment> assignments = assignmentRepository.findByState(StateAssignment.INCOMPLETE);
        for (Assignment assignment : assignments) {
            if (LocalDateTime.now().isAfter(assignment.getTimeEnd())) {
                assignment.setState(StateAssignment.OVERDUE);
                Notification notification = new Notification();
                notification.setEventTime(assignment.getTimeEnd());
                notification.setName(String.format("Bài tập %s đã hết hạn", assignment.getName()));
                notification.setState(StateNotification.UNREAD);
                notification.setCategory(NotificationCategory.ASSIGNMENT);
                notification.setContent(String.format("Bài tập %s đã hết hạn lúc %s", assignment.getName(), assignment.getTimeEnd().toString()));
                notification.setTimeNoti(LocalDateTime.now().plusMinutes(1)); // Để tạm
                notification.setEntityId(assignment.getId());
                notification.setUser(assignment.getUser());
                notificationRepository.save(notification);

                try {
                    emailService.sendNotificationEmail(assignment.getUser().getEmail(), notification, assignment.getUser());
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        assignmentRepository.saveAll(assignments);
        log.info("Update Status Assignment End");

    }
}
