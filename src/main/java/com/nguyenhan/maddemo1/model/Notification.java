package com.nguyenhan.maddemo1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nguyenhan.maddemo1.constants.NotificationCategory;
import com.nguyenhan.maddemo1.constants.StateNotification;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data // Lombok để tự động tạo getter, setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String content;

    @Column(name = "time_noti", nullable = false)
    private LocalDateTime timeNoti;

    @Column(name = "event_time", nullable = true)
    private LocalDateTime eventTime;

    @Column(name = "entity_id", nullable = true)
    private Long entityId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationCategory category;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StateNotification state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID")
    @JsonBackReference
    private User user;

    @Column(name = "read_time")
    private LocalDateTime readTime;
}