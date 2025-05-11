package com.nguyenhan.maddemo1.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nguyenhan.maddemo1.constants.StateLesson;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;
@Data
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class ScheduleLearningDto {
    private Long id;
    private String name;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private String teacher;
    private String learningAddresses;
    @Enumerated(EnumType.STRING)
    private StateLesson state;// notyet, vang,comat
    private String note;
    private Long courseID;
}
