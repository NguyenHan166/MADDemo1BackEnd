package com.nguyenhan.maddemo1.mapper;

import com.nguyenhan.maddemo1.dto.*;
import com.nguyenhan.maddemo1.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UsersMapper {

    private final AlarmMapper alarmMapper;
    private final ScheduleLearningMapper scheduleLearningMapper;
    private final CourseMapper courseMapper;
    private final AssignmentMapper assignmentMapper;

    public UsersMapper(AlarmMapper alarmMapper, ScheduleLearningMapper scheduleLearningMapper, CourseMapper courseMapper, AssignmentMapper assignmentMapper) {
        this.alarmMapper = alarmMapper;
        this.scheduleLearningMapper = scheduleLearningMapper;
        this.courseMapper = courseMapper;
        this.assignmentMapper = assignmentMapper;
    }

    public UserDto mapToUserDto(User user, UserDto userDto) {
//        userDto.setUsername(user.getUsername());

        List<AlarmDto> alarmDtoList = new ArrayList<>();
        List<AssignmentDto> assignmentDtoList = new ArrayList<>();
        List<ScheduleLearningDto> scheduleLearningDtoList = new ArrayList<>();
        List<CourseListOutputDto> courseListOutputDtoList = new ArrayList<>();

        user.getAlarms().forEach(alarm -> {
            alarmDtoList.add(alarmMapper.mapToAlarmDto(alarm, new AlarmDto()));
        });

        user.getAssignments().forEach(assignment -> {
            assignmentDtoList.add(assignmentMapper.mapToAssignmentDto(assignment, new AssignmentDto()));
        });

        user.getCourses().forEach(course -> {
            courseListOutputDtoList.add(courseMapper.mapToCourseListOutputDto(course, new CourseListOutputDto()));
        });

        user.getScheduleLearnings().forEach(scheduleLearning -> {
            scheduleLearningDtoList.add(scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, new ScheduleLearningDto()));
        });

        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setAge(user.getAge());
        userDto.setFullName(user.getFullName());
        userDto.setGender(user.getGender());
        userDto.setMobilePhone(user.getMobilePhone());
        userDto.setDateOfBirth(user.getDateOfBirth());
        userDto.setAlarmList(alarmDtoList);
        userDto.setAssignmentList(assignmentDtoList);
        userDto.setCourseList(courseListOutputDtoList);
        userDto.setScheduleLearningList(scheduleLearningDtoList);
        return userDto;
    }

    public  User mapToUser(UserDto userDto, User user) {
//        user.setUsername(userDto.getUsername());
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setAge(userDto.getAge());
        user.setFullName(userDto.getFullName());
        user.setGender(userDto.getGender());
        user.setMobilePhone(userDto.getMobilePhone());
        user.setDateOfBirth(userDto.getDateOfBirth());
        return user;
    }
}
