package com.nguyenhan.maddemo1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter@Setter@NoArgsConstructor@AllArgsConstructor@ToString
public class Course extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String teacher;
    private LocalDate timeStart;
    private LocalDate timeEnd;
    private String numberOfLessons;
    private String numberOfAssignments;
    private String credits;
    private String addressLearning;
    private String state="ongoing"; // ongoing, pending, finish
    private String listDay;
    private String repeatType;
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ScheduleLearning> scheduleLearnings = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Assignment> assignments = new ArrayList<>();
}
