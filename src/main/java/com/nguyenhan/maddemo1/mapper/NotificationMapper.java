package com.nguyenhan.maddemo1.mapper;

import com.nguyenhan.maddemo1.dto.NotificationDto;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.model.Notification;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.NotificationRepository;
import com.nguyenhan.maddemo1.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    private final UserRepository userRepository;

    public NotificationMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Notification mapToNotification(NotificationDto notificationDto, Notification notification) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email)
        );

        notification.setUser(user);
        notification.setName(notificationDto.getName());
        notification.setContent(notificationDto.getContent());
        notification.setEntityId(notificationDto.getEntityId());
        notification.setCategory(notificationDto.getCategory());
        notification.setReadTime(notificationDto.getReadTime());
        notification.setState(notificationDto.getState());
        notification.setTimeNoti(notificationDto.getTimeNoti());
        notification.setEventTime(notificationDto.getEventTime());
        return notification;
    }

    public NotificationDto mapToNotificationDto(Notification notification, NotificationDto notificationDto) {
        notificationDto.setId(notification.getId());
        notificationDto.setName(notification.getName());
        notificationDto.setContent(notification.getContent());
        notificationDto.setEntityId(notification.getEntityId());
        notificationDto.setCategory(notification.getCategory());
        notificationDto.setReadTime(notification.getReadTime());
        notificationDto.setState(notification.getState());
        notificationDto.setTimeNoti(notification.getTimeNoti());
        notificationDto.setEventTime(notification.getEventTime());
        return notificationDto;
    }

}
