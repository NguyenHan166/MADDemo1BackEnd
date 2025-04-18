package com.nguyenhan.maddemo1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "personalworks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalWork extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private LocalDateTime create_at;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private String workAddress;
    private String loopValue;
    private String state;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID")
    @JsonBackReference
    private User user;

}