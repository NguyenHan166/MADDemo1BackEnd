package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.dto.AlarmDto;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.mapper.AlarmMapper;
import com.nguyenhan.maddemo1.model.Alarm;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.AlarmRepository;
import com.nguyenhan.maddemo1.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final AlarmMapper alarmMapper;

    public AlarmService(AlarmRepository alarmRepository, UserRepository userRepository, AlarmMapper alarmMapper) {
        this.alarmRepository = alarmRepository;
        this.userRepository = userRepository;
        this.alarmMapper = alarmMapper;
    }

    public List<Alarm> findAllByUserId(Long userId) {
        return alarmRepository.findByUserId(userId);
    }

    public Alarm findById(Long id) {
        return alarmRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Alarm", "Id", id.toString())
        );
    }

    public Alarm createAlarm(AlarmDto alarmDto) {
        User user = userRepository.findById(alarmDto.getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "Id", alarmDto.getUserId().toString())
        );

        Alarm alarm = new Alarm();
        alarmMapper.mapToAlarm(alarmDto, alarm);
        return alarmRepository.save(alarm);
    }

    public boolean updateAlarm(Long alarmId , AlarmDto alarmDto) {
        boolean isUpdated = false;
        Alarm alarm = alarmRepository.findById(alarmId).orElseThrow(
                () -> new ResourceNotFoundException("Alarm", "Id", alarmId.toString())
        );

        User user = userRepository.findById(alarmDto.getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "Id", alarmDto.getUserId().toString())
        );

        alarmMapper.mapToAlarm(alarmDto, alarm);
        alarmRepository.save(alarm);
        isUpdated = true;
        return isUpdated;
    }

    public boolean deleteAlarm(Long alarmId) {
        boolean isDeleted = false;
        Alarm alarm = alarmRepository.findById(alarmId).orElseThrow(
                () -> new ResourceNotFoundException("Alarm", "Id", alarmId.toString())
        );
        alarmRepository.deleteById(alarmId);
        isDeleted = true;
        return isDeleted;
    }
}
