package com.nguyenhan.maddemo1.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nguyenhan.maddemo1.constants.NotificationCategory;
import com.nguyenhan.maddemo1.constants.StateNotification;
import com.nguyenhan.maddemo1.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor@NoArgsConstructor
public class NotificationDto {

    private Long id;
    private String name;
    private String content;
    private LocalDateTime timeNoti;
    private LocalDateTime eventTime;
    private Long entityId;

    @Enumerated(EnumType.STRING)
    private NotificationCategory category;

    @Enumerated(EnumType.STRING)
    private StateNotification state;
    private LocalDateTime readTime;
}
