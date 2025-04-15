package com.nguyenhan.maddemo1.mapper;

import com.nguyenhan.maddemo1.dto.AlarmDto;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.model.Alarm;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlarmMapper {

    private final UserRepository userRepository; // Không cần static

    @Autowired // Tiêm UserRepository qua constructor
    public AlarmMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Alarm mapToAlarm(AlarmDto alarmDto, Alarm alarm) {
        User user = userRepository.findById(alarmDto.getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "ID", alarmDto.getUserId().toString())
        );

        alarm.setDateAlarm(alarmDto.getDateAlarm());
        alarm.setUser(user);
        alarm.setMode(alarmDto.getMode());
        alarm.setCategory(alarmDto.getCategory());
        alarm.setMusic(alarmDto.getMusic());
        alarm.setName(alarmDto.getName());
        alarm.setState(alarmDto.getState());
        alarm.setTimeAlarm(alarmDto.getTimeAlarm());
        alarm.setRepeatDays(alarmDto.getRepeatDays());
        return alarm;
    }

    public AlarmDto mapToAlarmDto(Alarm alarm, AlarmDto alarmDto) {
        User user = userRepository.findById(alarmDto.getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "ID", alarmDto.getUserId().toString())
        );
        alarmDto.setUserId(user.getId());
        alarmDto.setDateAlarm(alarm.getDateAlarm());
        alarmDto.setMode(alarm.getMode());
        alarmDto.setCategory(alarm.getCategory());
        alarmDto.setMusic(alarm.getMusic());
        alarmDto.setName(alarm.getName());
        alarmDto.setState(alarm.getState());
        alarmDto.setTimeAlarm(alarm.getTimeAlarm());
        alarmDto.setRepeatDays(alarm.getRepeatDays());
        return alarmDto;
    }
}
