package com.nguyenhan.maddemo1.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseListOutputDto {
    private Long id;
    private String name;
    private String note;
    private String teacher;
    private String credits;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate timeStart;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate timeEnd;
    private String numberOfLesion;
    private String numberOfAssignment;
    private String addressLearning;
    private String state;// notyet,ongoing,end
}
