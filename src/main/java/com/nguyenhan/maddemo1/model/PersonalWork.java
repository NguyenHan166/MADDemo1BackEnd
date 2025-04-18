package com.nguyenhan.maddemo1.model;

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


    @ManyToOne()
    @JoinColumn(name = "userID")
    @JsonIgnore
    private User user;

//    @OneToOne(mappedBy = "personalWork", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JsonBackReference
//    private Note note;

//    @ManyToOne(optional = true)
//    @JoinColumn(name = "course", nullable = true)
//    private Course course;
}
