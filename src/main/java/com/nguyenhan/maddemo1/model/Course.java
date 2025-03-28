package com.nguyenhan.maddemo1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class Course extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String teacher;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private int numberOfLesion;
    private String addressLearning;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "userID")
    private User user;
}
