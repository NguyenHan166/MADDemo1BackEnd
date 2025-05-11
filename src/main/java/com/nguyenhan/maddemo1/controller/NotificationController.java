package com.nguyenhan.maddemo1.controller;

import com.nguyenhan.maddemo1.constants.ResponseConstants;
import com.nguyenhan.maddemo1.dto.NotificationDto;
import com.nguyenhan.maddemo1.mapper.NotificationMapper;
import com.nguyenhan.maddemo1.model.Notification;
import com.nguyenhan.maddemo1.responses.ErrorResponseDto;
import com.nguyenhan.maddemo1.responses.NotificationResponse;
import com.nguyenhan.maddemo1.responses.ResponseDto;
import com.nguyenhan.maddemo1.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    public NotificationController(NotificationService notificationService, NotificationMapper notificationMapper) {
        this.notificationService = notificationService;
        this.notificationMapper = notificationMapper;
    }

    @GetMapping("/")
    public ResponseEntity<Object> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        List<NotificationDto> notificationDtos = new ArrayList<>();
        notifications.forEach(notification -> {
            notificationDtos.add(notificationMapper.mapToNotificationDto(notification, new NotificationDto()));
        });

        NotificationResponse response = new NotificationResponse();
        response.setNotifications(notificationDtos);
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
    }

    @GetMapping("/fetch")
    public ResponseEntity<Object> getNotification(@RequestParam("id") Long id) {
        Notification notification = notificationService.getNotificationById(id);
        NotificationDto notificationDto = notificationMapper.mapToNotificationDto(notification, new NotificationDto());
        NotificationResponse response = new NotificationResponse();
        response.setNotification(notificationDto);
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createNotification(@RequestBody NotificationDto notificationDto) {
         Notification notification = notificationService.createNotification(notificationDto);
         NotificationResponse response = new NotificationResponse();
         response.setNotification(notificationMapper.mapToNotificationDto(notification, notificationDto));
         return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateNotification(@RequestParam("id") Long id ,@RequestBody NotificationDto notificationDto) {
        boolean isSuccess = notificationService.updateNotification(id, notificationDto);
        if(isSuccess){
            return ResponseEntity.status(ResponseConstants.STATUS_200).body(new NotificationDto());
        }else{
            return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ErrorResponseDto(
                    "api/notifications/delete",
                    ResponseConstants.STATUS_417,
                    ResponseConstants.MESSAGE_417_UPDATE,
                    LocalDateTime.now()
            ));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteNotification(@RequestParam("id") Long id) {
        boolean isSuccess = notificationService.deleteNotification(id);
        if (isSuccess) {
            return ResponseEntity.status(ResponseConstants.STATUS_200).body(new ResponseDto(ResponseConstants.STATUS_200, ResponseConstants.MESSAGE_200));
        }else{
            return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ErrorResponseDto(
                    "api/notifications/delete",
                    ResponseConstants.STATUS_417,
                    ResponseConstants.MESSAGE_417_DELETE,
                    LocalDateTime.now()
            ));
        }
    }

}
