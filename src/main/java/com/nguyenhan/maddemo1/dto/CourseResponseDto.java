package com.nguyenhan.maddemo1.dto;

import com.nguyenhan.maddemo1.constants.Review;
import com.nguyenhan.maddemo1.constants.StateCourse;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class CourseResponseDto {
    private Long id;
    private String name;
    private String note;
    private String teacher;
    private LocalDate timeStart;
    private LocalDate timeEnd;
    private int numberOfLesion;
    private String addressLearning;
    private String repeatTime;
    @Enumerated(EnumType.STRING)
    private StateCourse state;
    @Enumerated(EnumType.STRING)
    private Review review_sche;
    @Enumerated(EnumType.STRING)
    private Review review_ass;

    private int percent_review_all;
    private String text_review;
    private int totalScheduleLearning;
    private int totalScheduleLearningCurrent;
    private int scheduleLearning_present;
    private int scheduleLearning_absent;

    private int totalAssignment;
    private int totalAssignmentCurrent;
    private int assignment_overdue;
//    private Long noteId;
    private List<ScheduleLearningDto> scheduleLearningList;
//    private List<AssignmentDto> assignmentDtoList;
}
