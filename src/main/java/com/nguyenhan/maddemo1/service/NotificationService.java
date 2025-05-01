package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.dto.NotificationDto;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.mapper.NotificationMapper;
import com.nguyenhan.maddemo1.model.Notification;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.NotificationRepository;
import com.nguyenhan.maddemo1.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository, UserService userService, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.notificationMapper = notificationMapper;
    }

    public List<Notification> getAllNotifications() {
        User user = userService.getAuthenticatedUser();
        return notificationRepository.findByUser(user);
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Notidication" , "Id" , id.toString())
        );
    }

    public Notification createNotification(NotificationDto notificationDto) {
        Notification notification = new Notification();
        notificationMapper.mapToNotification(notificationDto, notification);
        return notificationRepository.save(notification);
    }

    public List<Notification> createNotifications(List<NotificationDto> notificationDtos) {
        List<Notification> notifications = new ArrayList<>();
        notificationDtos.forEach(notificationDto -> {
            notifications.add(notificationMapper.mapToNotification(notificationDto, new Notification()));
        });
        return notificationRepository.saveAll(notifications);
    }

    public boolean updateNotification(Long id, NotificationDto notificationDto) {
        boolean isUpdated = false;
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Notidication" , "Id" , id.toString())
        );
        notificationMapper.mapToNotification(notificationDto, notification);
        notificationRepository.save(notification);
        isUpdated = true;
        return isUpdated;
    }

    public boolean deleteNotification(Long id) {
        boolean isDeleted = false;
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Notidication" , "Id" , id.toString())
        );
        notificationRepository.deleteById(notification.getId());
        isDeleted = true;
        return isDeleted;
    }
}
