package com.nguyenhan.maddemo1.controller;

import com.nguyenhan.maddemo1.constants.ResponseConstants;
import com.nguyenhan.maddemo1.dto.AlarmDto;
import com.nguyenhan.maddemo1.mapper.AlarmMapper;
import com.nguyenhan.maddemo1.model.Alarm;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.responses.AlarmResponse;
import com.nguyenhan.maddemo1.responses.ErrorResponseDto;
import com.nguyenhan.maddemo1.responses.ResponseDto;
import com.nguyenhan.maddemo1.service.AlarmService;
import com.nguyenhan.maddemo1.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/alarms")
public class AlarmController {

    private final AlarmService alarmService;
    private final UserService userService;
    private final AlarmMapper alarmMapper;

    public AlarmController(AlarmService alarmService, UserService userService, AlarmMapper alarmMapper) {
        this.alarmService = alarmService;
        this.userService = userService;
        this.alarmMapper = alarmMapper;
    }

    @GetMapping("/")
    public ResponseEntity<Object> getAllByUserId() {
        User user = userService.getAuthenticatedUser();

        List<AlarmDto> alarmDtos = new ArrayList<>();
        alarmService.findAllByUserId(user.getId()).forEach(
                alarm -> {
                    alarmDtos.add(alarmMapper.mapToAlarmDto(alarm, new AlarmDto()));
                }
        );
        AlarmResponse response = new AlarmResponse();
        response.setAlarms(alarmDtos);
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
    }

    @GetMapping("/fetch")
    public ResponseEntity<Object> fetchAlarms(@RequestParam Long alarmId) {
        Alarm alarm = alarmService.findById(alarmId);
        AlarmDto alarmDto = alarmMapper.mapToAlarmDto(alarm, new AlarmDto());
        AlarmResponse response = new AlarmResponse();
        response.setAlarm(alarmDto);
        return  ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> crateAlarm(@RequestBody AlarmDto alarmDto) {
        Alarm alarm = alarmService.createAlarm(alarmDto);
        alarmMapper.mapToAlarmDto(alarm, alarmDto);
        AlarmResponse response = new AlarmResponse();
        response.setAlarm(alarmDto);
        return ResponseEntity.status(ResponseConstants.STATUS_201).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateAlarm(@RequestParam Long alarmId ,@RequestBody AlarmDto alarmDto) {
        boolean isSuccess = alarmService.updateAlarm(alarmId, alarmDto);
        if (isSuccess) {
            Alarm alarm = alarmService.findById(alarmId);
            alarmMapper.mapToAlarmDto(alarm, alarmDto);
            AlarmResponse response = new AlarmResponse();
            response.setAlarm(alarmDto);
            return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
        }else{
            return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ResponseDto(ResponseConstants.STATUS_417, ResponseConstants.MESSAGE_417_UPDATE));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteAlarm(@RequestParam Long alarmId) {
        boolean isSuccess = alarmService.deleteAlarm(alarmId);
        if (isSuccess) {
            return ResponseEntity.status(ResponseConstants.STATUS_200).body(new ResponseDto(ResponseConstants.STATUS_200, ResponseConstants.MESSAGE_200));
        }else{
            return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ResponseDto(ResponseConstants.STATUS_417, ResponseConstants.MESSAGE_417_DELETE));
        }
    }
}
