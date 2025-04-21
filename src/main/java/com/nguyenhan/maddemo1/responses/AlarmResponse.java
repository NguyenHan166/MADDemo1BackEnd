package com.nguyenhan.maddemo1.responses;

import com.nguyenhan.maddemo1.dto.AlarmDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor@NoArgsConstructor
public class AlarmResponse {
    private AlarmDto alarm;
    private List<AlarmDto> alarms;
}
