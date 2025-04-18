package com.nguyenhan.maddemo1.model;

import com.nguyenhan.maddemo1.constants.StateAssignment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class Assignment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private LocalDateTime timeEnd;

    @Enumerated(EnumType.STRING)
    private StateAssignment stateAssignment;

    @ManyToOne
    @JoinColumn(name = "courseID")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;
}
