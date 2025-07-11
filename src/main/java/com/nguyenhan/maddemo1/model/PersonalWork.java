package com.nguyenhan.maddemo1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nguyenhan.maddemo1.constants.repeatCycle;
import com.nguyenhan.maddemo1.constants.StateAssignment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "personalworks")
@Getter@Setter@NoArgsConstructor@AllArgsConstructor
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

    @Enumerated(EnumType.STRING)
    private repeatCycle repeatCycle;

    @Enumerated(EnumType.STRING)
    private StateAssignment state;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userID")
    @JsonBackReference
    private User user;

}
