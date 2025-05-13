package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.constants.AlarmCategory;
import com.nguyenhan.maddemo1.dto.AlarmDto;
import com.nguyenhan.maddemo1.exception.ResourceAlreadyExistsException;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.exception.ServerErrorException;
import com.nguyenhan.maddemo1.mapper.AlarmMapper;
import com.nguyenhan.maddemo1.model.Alarm;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final AlarmMapper alarmMapper;
    private final ScheduleLearningRepository scheduleLearningRepository;
    private final PersonalWorkRepository personalWorkRepository;
    private final AssignmentRepository assignmentRepository;

    public AlarmService(AlarmRepository alarmRepository, UserRepository userRepository, AlarmMapper alarmMapper, ScheduleLearningRepository learningRepository, PersonalWorkRepository personalWorkRepository, AssignmentRepository assignmentRepository) {
        this.alarmRepository = alarmRepository;
        this.userRepository = userRepository;
        this.alarmMapper = alarmMapper;
        this.scheduleLearningRepository = learningRepository;
        this.personalWorkRepository = personalWorkRepository;
        this.assignmentRepository = assignmentRepository;
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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "Email", email)
        );

        Alarm alarm = alarmMapper.mapToAlarm(alarmDto, new Alarm());

        if (user.getAlarms().contains(alarm)) {
            throw new ResourceAlreadyExistsException("Alarms", "name", alarm.getName());
        }

        if (alarm.getEntityId() != null) {
            if (alarm.getCategory().equals("LESSON")) {
                scheduleLearningRepository.findById(alarm.getEntityId()).orElseThrow(
                        () -> new ResourceNotFoundException("ScheduleLearning", "Id", alarm.getEntityId().toString())
                );
            } else if (alarm.getCategory().equals("PERSONAL_WORK")) {
                personalWorkRepository.findById(alarm.getEntityId()).orElseThrow(
                        () -> new ResourceNotFoundException("PersonalWork", "Id", alarm.getEntityId().toString())
                );
            } else if (alarm.getCategory().equals("ASSIGNMENT")) {
                assignmentRepository.findById(alarm.getEntityId()).orElseThrow(
                        () -> new ResourceNotFoundException("Assignment", "Id", alarm.getEntityId().toString())
                );
            }
        }

        return alarmRepository.save(alarm);
    }

    @Transactional
    public boolean updateAlarm(Long alarmId, AlarmDto alarmDto) {
        boolean isUpdated = false;
        Alarm alarm = alarmRepository.findById(alarmId).orElseThrow(
                () -> new ResourceNotFoundException("Alarm", "Id", alarmId.toString())
        );

        log.atInfo().log("Update Start");

        alarmMapper.mapToAlarm(alarmDto, alarm);

        if (alarm.getEntityId() != null && !alarm.getCategory().equals(AlarmCategory.GENERAL)) {
            if (alarm.getCategory().equals(AlarmCategory.LESSON)) {
                scheduleLearningRepository.findById(alarm.getEntityId()).orElseThrow(
                        () -> new ResourceNotFoundException("ScheduleLearning", "Id", alarm.getEntityId().toString())
                );
            } else if (alarm.getCategory().equals(AlarmCategory.PERSONAL_WORK)) {
                personalWorkRepository.findById(alarm.getEntityId()).orElseThrow(
                        () -> new ResourceNotFoundException("PersonalWork", "Id", alarm.getEntityId().toString())
                );
            } else if (alarm.getCategory().equals(AlarmCategory.ASSIGNMENT)) {
                assignmentRepository.findById(alarm.getEntityId()).orElseThrow(
                        () -> new ResourceNotFoundException("Assignment", "Id", alarm.getEntityId().toString())
                );
            } else {
                throw new ServerErrorException("Something went wrong");
            }
        }
        alarmRepository.save(alarm);
        isUpdated = true;
        log.atInfo().log("Update Alarm End");
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
