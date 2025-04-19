package com.nguyenhan.maddemo1.responses;

import com.nguyenhan.maddemo1.dto.AssignmentDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor@NoArgsConstructor
public class AssignmentResponse {
    AssignmentDto assignment;
    List<AssignmentDto> assignments;
}
