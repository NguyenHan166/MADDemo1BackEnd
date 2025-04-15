package com.nguyenhan.maddemo1.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class AssignmentDto {
    private Long id;
    private String name;
    private LocalDateTime timeEnd;
    private String state;
    private Long courseId;
}
