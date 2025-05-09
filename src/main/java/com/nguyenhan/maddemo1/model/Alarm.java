package com.nguyenhan.maddemo1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nguyenhan.maddemo1.constants.AlarmCategory;
import com.nguyenhan.maddemo1.constants.AlarmMode;
import com.nguyenhan.maddemo1.constants.AlarmState;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "alarms")
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class Alarm extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    @Column(nullable = true)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    private AlarmCategory category;
    private LocalDateTime timeAlarm;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "alarm_repeat_days", joinColumns = @JoinColumn(name = "alarm_id"))
    @Column(name = "repeat_day")
    private Set<DayOfWeek> repeatDays;  // Lưu chuỗi các ngày lặp lại

    @Enumerated(EnumType.STRING)
    private AlarmMode mode;
    private String music;

    @Enumerated(EnumType.STRING)
    private AlarmState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID")
    @JsonBackReference
    private User user;


}
