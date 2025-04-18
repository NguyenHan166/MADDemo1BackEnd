package com.nguyenhan.maddemo1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nguyenhan.maddemo1.constants.StateLesson;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedule_leanings")
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class ScheduleLearning extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private String teacher;
    private String learningAddresses;

    @Enumerated(EnumType.STRING)
    private StateLesson stateLesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID")
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseID")
    @JsonBackReference
    private Course course;

}
