package com.nguyenhan.maddemo1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "alarms")
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class Alarm extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private Long entityId;
    private String category;
    private String dateAlarm;
    private LocalDateTime timeAlarm;
    private String repeatDays;  // Lưu chuỗi các ngày lặp lại
    private String mode;
    private String music;
    private String state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID")
    @JsonBackReference
    private User user;


}
