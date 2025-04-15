package com.nguyenhan.maddemo1.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class AlarmDto {
    private int id;
    private String name;
    private Long entityID;
    private String category;
    private String dateAlarm;
    private LocalDateTime timeAlarm;
    private String repeatDays;  // Lưu chuỗi các ngày lặp lại
    private String mode;
    private String music;
    private String state;
    private Long userId;
}
