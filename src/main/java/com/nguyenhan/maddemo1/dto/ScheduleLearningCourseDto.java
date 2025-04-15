package com.nguyenhan.maddemo1.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class ScheduleLearningCourseDto {
    private String description;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
}
