package com.nguyenhan.maddemo1.dto;

import com.nguyenhan.maddemo1.constants.StateAssignment;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class AssignmentDto {
    private Long id;
    private String name;
    private LocalDateTime timeEnd;
    @Enumerated(EnumType.STRING)
    private StateAssignment stateAssignment;
    private Long courseId;
}
