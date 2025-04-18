package com.nguyenhan.maddemo1.dto;

import com.nguyenhan.maddemo1.constants.Review;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class CourseResponseDto {
    private Long id;
    private String name;
    private String description;
    private String teacher;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private int numberOfLesion;
    private String addressLearning;
    private String repeatTime;
    private String state;
    @Enumerated(EnumType.STRING)
    private Review review;
    private int totalScheduleLearning;
    private int totalScheduleLearningCurrent;
    private int scheduleLearning_present;
    private int scheduleLearning_absent;

    private int totalAssignment;
    private int totalAssignmentCurrent;
//    private Long noteId;
    private List<ScheduleLearningDto> scheduleLearningList;
//    private List<AssignmentDto> assignmentDtoList;
}
