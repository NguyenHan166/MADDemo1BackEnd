package com.nguyenhan.maddemo1.model;

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

    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    @Column(name = "entity_id", nullable = false)
    private Integer entityId;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String state = "pending"; // Mặc định là "pending"

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "read_time")
    private LocalDateTime readTime;
}