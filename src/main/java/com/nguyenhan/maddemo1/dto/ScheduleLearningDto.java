package com.nguyenhan.maddemo1.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;
@Data
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class ScheduleLearningDto {
    private String description;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private String teacher;
    private String learningAddresses;
    private String state;
    private Long userID;
    private Long courseID;
}
