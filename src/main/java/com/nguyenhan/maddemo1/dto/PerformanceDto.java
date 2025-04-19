package com.nguyenhan.maddemo1.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nguyenhan.maddemo1.constants.StateLesson;
import com.nguyenhan.maddemo1.model.Assignment;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.ScheduleLearning;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class PerformanceDto {
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime create_at = LocalDateTime.now();

    @NotNull
    private LocalDateTime timeStart;

    @NotNull
    private LocalDateTime timeEnd;

    private List<CourseResponseDto> courseResponseDtoList;
//    private List<ScheduleLearning> scheduleLearningList;
//    private List<Assignment> assignmentList;

    private int present;
    private int absent;
}
