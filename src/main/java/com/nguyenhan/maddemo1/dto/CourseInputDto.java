package com.nguyenhan.maddemo1.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseInputDto {
    private String name;
    private String note;
    private String teacher;
    private String credits;
    private String addressLearning;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate timeStart;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate timeEnd;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startLessonTime;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endLessonTime;
    private String repeatType;
    private String state;// notyet,ongoing,end
    private String listDay;
}
