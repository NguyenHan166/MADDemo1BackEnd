package com.nguyenhan.maddemo1.dto;

import com.nguyenhan.maddemo1.constants.RepeatTypeCourse;
import com.nguyenhan.maddemo1.constants.StateCourse;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class CourseDetailsDto {
    private Long id;
    @NotEmpty(message = "Tên khóa học không thể bỏ trống")
    private String name;
    private String teacher;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate timeStart;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate timeEnd;
    private String numberOfLessons;
    private String numberOfAssignments;
    private String credits;
    private String addressLearning;
    @Enumerated(EnumType.STRING)
    private RepeatTypeCourse repeatType;
    private String listDay;
    @Enumerated(EnumType.STRING)
    private StateCourse state;
    private String note;
    private List<ScheduleLearningDto> scheduleLearningList;
    private List<AssignmentDto> assignmentList;
}
