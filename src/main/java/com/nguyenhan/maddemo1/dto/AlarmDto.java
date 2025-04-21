package com.nguyenhan.maddemo1.dto;

import com.nguyenhan.maddemo1.constants.AlarmCategory;
import com.nguyenhan.maddemo1.constants.AlarmMode;
import com.nguyenhan.maddemo1.constants.AlarmState;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Data
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class AlarmDto {
    private Long id;
    private String name;
    private Long entityID;
    @Enumerated(EnumType.STRING)
    private AlarmCategory category;
    private LocalDateTime timeAlarm;
    private Set<DayOfWeek> repeatDays;  // Lưu chuỗi các ngày lặp lại
    @Enumerated(EnumType.STRING)
    private AlarmMode mode;
    private String music;
    @Enumerated(EnumType.STRING)
    private AlarmState state;
}
