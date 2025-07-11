package com.nguyenhan.maddemo1.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nguyenhan.maddemo1.constants.repeatCycle;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class PersonalWorkDto {

    private Long id;

    @NotNull(message = "Tên công việc không được bỏ trống")
    private String name;

    private String description;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime create_at = LocalDateTime.now();

    @Future(message = "Thời gian bắt đầu không được trước thời điểm hiện tại")
    private LocalDateTime timeStart;

    @Future(message = "Thời gian kết thúc không được trước thời điểm hiện tại")
    private LocalDateTime timeEnd;

    private String workAddress;

    @Enumerated(EnumType.STRING)
    private repeatCycle repeatCycle;

    private Long userId;

    @AssertTrue(message = "Thời gian kết thúc không được trươớc thời gian bắt đầu")
    public boolean isValidTimeRange(){
        return timeEnd == null||timeStart==null||!timeEnd.isBefore(timeStart);
    }
}
