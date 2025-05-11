package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.constants.NotificationCategory;
import com.nguyenhan.maddemo1.constants.StateAssignment;
import com.nguyenhan.maddemo1.constants.StateNotification;
import com.nguyenhan.maddemo1.dto.PersonalWorkDto;
import com.nguyenhan.maddemo1.dto.PersonalWorkUpdateDto;
import com.nguyenhan.maddemo1.exception.ResourceAlreadyExistsException;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.mapper.CourseMapper;
import com.nguyenhan.maddemo1.mapper.PersonalWorkMapper;
import com.nguyenhan.maddemo1.model.*;
import com.nguyenhan.maddemo1.repository.CourseRepository;
import com.nguyenhan.maddemo1.repository.NotificationRepository;
import com.nguyenhan.maddemo1.repository.PersonalWorkRepository;
import com.nguyenhan.maddemo1.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.nguyenhan.maddemo1.config.PersonalWorkSecurity;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
public class PersonalWorkService {

    private final PersonalWorkRepository personalWorkRepository;
    private final UserRepository userRepository;
    private final PersonalWorkMapper personalWorkMapper;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public PersonalWorkService(PersonalWorkRepository personalWorkRepository, UserRepository userRepository,
                               PersonalWorkMapper personalWorkMapper, NotificationRepository notificationRepository, EmailService emailService) {
        this.personalWorkRepository = personalWorkRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.personalWorkMapper = personalWorkMapper;
        this.emailService = emailService;
    }


    public PersonalWork createPersonalWork(PersonalWorkDto request) {
//        if (personalWorkRepository.findByName(request.getName()).isPresent()) {
//            throw new ResourceAlreadyExistsException("Event", "name", request.getName());
//        }

        PersonalWork personalWork = personalWorkMapper.toPersonalWork(request);
        return personalWorkRepository.save(personalWork);
    }

    @Transactional
    @PreAuthorize("@personalWorkSecurity.isOwner(#id)")
    public PersonalWork updatePersonalWork(PersonalWorkUpdateDto request, Long id) {
        log.info("Id personalwork update: {}", id);
        PersonalWork personalWork = personalWorkRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("PersonalWork", "id", id.toString())
        );

        log.info("Ten per:{}", personalWork.getName());

        personalWorkMapper.updatePersonalWorkMapper(personalWork, request);
        log.info("Ten per:{}", personalWork.getName());
        return personalWorkRepository.save(personalWork);
    }

    @Transactional
    @PreAuthorize("@personalWorkSecurity.isOwner(#id)")
    public boolean deletePersonalWork(Long id) {
        boolean isDeleted = false;
        PersonalWork personalWork = personalWorkRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("PersonalWork", "id", id.toString())
        );
        log.info(personalWork.toString());
//        personalWorkRepository.delete(personalWork);
        personalWorkRepository.deletePersonalWorkById(id);

        notificationRepository.deleteByEntityIdAndCategory(id, NotificationCategory.PERSONAL_WORK);
        log.info("PersonalWork with ID {} deleted successfully.", id);
        isDeleted = true;
        return isDeleted;
    }

    public List<PersonalWork> getPersonalWorksByUserId() {
        User user = getCurrentUser();

        return personalWorkRepository.findByUserId(user.getId());
    }

//    public List<PersonalWork> getPersonalWorksByCourseId(Long courseId){
//        return personalWorkRepository.findByCourseId(courseId);
//    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("PersonalWork Service: principal: {}", principal);

        if (principal instanceof User) {
            return (User) principal;
        } else if (principal instanceof String) {
            // Trường hợp khi principal là email (chuỗi)
            String username = (String) principal;
            return userRepository.findByEmail(username).orElseThrow(() ->
                    new UsernameNotFoundException("User not found with email: " + username));
        }

        return null; // hoặc throw exception nếu không tìm thấy
    }

    @Scheduled(fixedRate = 300000) // 30p
    public void updateStatePersonalWorkScheduleTask() {
        log.info("Update Status PersonalWork Start");
        List<PersonalWork> personalWorks = personalWorkRepository.findByState(StateAssignment.INCOMPLETE);
        for (PersonalWork personalWork : personalWorks) {
            if (LocalDateTime.now().isAfter(personalWork.getTimeEnd())) {
                personalWork.setState(StateAssignment.OVERDUE);
                Notification notification = new Notification();
                notification.setEventTime(personalWork.getTimeEnd());
                notification.setName(String.format("Công việc %s đã hết hạn", personalWork.getName()));
                notification.setState(StateNotification.UNREAD);
                notification.setCategory(NotificationCategory.PERSONAL_WORK);
                notification.setContent(String.format("Công việc %s đã hết hạn lúc %s", personalWork.getName(), personalWork.getTimeEnd().toString()));
                notification.setTimeNoti(LocalDateTime.now().plusMinutes(1)); // Để tạm
                notification.setEntityId(personalWork.getId());
                notification.setUser(personalWork.getUser());
                notificationRepository.save(notification);

                try {
                    emailService.sendNotificationEmail(personalWork.getUser().getEmail(), notification, personalWork.getUser());
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        personalWorkRepository.saveAll(personalWorks);
        log.info("Update Status Personal Work End");
    }

    public PersonalWork findById(Long eventId) {
        return personalWorkRepository.findById(eventId).orElseThrow(
                () -> new ResourceNotFoundException("PersonalWork", "id", eventId.toString())
        );
    }
}