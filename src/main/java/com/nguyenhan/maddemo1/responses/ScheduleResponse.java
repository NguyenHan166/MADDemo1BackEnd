package com.nguyenhan.maddemo1.responses;

import com.nguyenhan.maddemo1.dto.CourseDetailsDto;
import com.nguyenhan.maddemo1.dto.CourseListOutputDto;
import com.nguyenhan.maddemo1.dto.ScheduleLearningDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponse {
    List<ScheduleLearningDto> scheduleLearningList;
    ScheduleLearningDto scheduleLearning;
}
