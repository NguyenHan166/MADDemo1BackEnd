package com.nguyenhan.maddemo1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduleLeanings")
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
    private String state;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "courseId")
    @JsonBackReference
    private Course course;

}
