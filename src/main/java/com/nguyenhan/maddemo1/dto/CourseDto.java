package com.nguyenhan.maddemo1.dto;

import com.nguyenhan.maddemo1.model.ScheduleLearning;
import com.nguyenhan.maddemo1.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class CourseDto {
    private Long id;
    @NotEmpty(message = "Tên khóa học không thể bỏ trống")
    private String name;
    private String description;
    private String teacher;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private int numberOfLesion;
    private String addressLearning;
    private String repeatTime;
    private String state;
//    private Long noteId;
    private List<ScheduleLearningDto> scheduleLearningList;
}
