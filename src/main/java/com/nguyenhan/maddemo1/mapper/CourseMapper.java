package com.nguyenhan.maddemo1.mapper;

import com.nguyenhan.maddemo1.dto.*;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.UserRepository;
import com.nguyenhan.maddemo1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component // Biến CourseMapper thành Spring Bean
public class CourseMapper {

    private final UserRepository userRepository;
    public ScheduleLearningMapper scheduleLearningMapper;
    public AssignmentMapper assignmentMapper;

    @Autowired // Tiêm UserRepository qua constructor
    public CourseMapper(UserRepository userRepository, ScheduleLearningMapper scheduleLearningMapper, AssignmentMapper assignmentMapper) {
        this.userRepository = userRepository;
        this.scheduleLearningMapper = scheduleLearningMapper;
        this.assignmentMapper = assignmentMapper;
    }

    public Course mapToCourse(CourseInputDto courseDto, Course course) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        course.setName(courseDto.getName());
        course.setNote(courseDto.getNote());
        course.setCredits(courseDto.getCredits());
        course.setTeacher(courseDto.getTeacher());
        course.setTimeEnd(courseDto.getTimeEnd());
        course.setTimeStart(courseDto.getTimeStart());
        course.setAddressLearning(courseDto.getAddressLearning());
        course.setState(courseDto.getState());
        course.setRepeatType(courseDto.getRepeatType());
        course.setListDay(courseDto.getListDay());
        course.setUser(user);

        return course;
    }

    public CourseDetailsDto mapToCourseDto(Course course, CourseDetailsDto courseDto) {

        List<ScheduleLearningDto> scheduleLearningDtos = new ArrayList<>();
        List<AssignmentDto> assignmentDtos = new ArrayList<>();

        course.getScheduleLearnings().forEach(
                scheduleLearning -> {
                    scheduleLearningDtos.add(scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, new ScheduleLearningDto()));
                }
        );

        course.getAssignments().forEach(
                assignment -> {
                    assignmentDtos.add(assignmentMapper.mapToAssignmentDto(assignment, new AssignmentDto()));
                }
        );

        courseDto.setId(course.getId());
        courseDto.setName(course.getName());
        courseDto.setNote(course.getNote());
        courseDto.setCredits(course.getCredits());
        courseDto.setTeacher(course.getTeacher());
        courseDto.setTimeEnd(course.getTimeEnd());
        courseDto.setNumberOfAssignments(course.getNumberOfAssignments());
        courseDto.setNumberOfLessons(course.getNumberOfLessons());
        courseDto.setRepeatType(course.getRepeatType());
        courseDto.setListDay(course.getListDay());
        courseDto.setTimeStart(course.getTimeStart());
        courseDto.setAddressLearning(course.getAddressLearning());
        courseDto.setState(course.getState());
        courseDto.setScheduleLearningList(scheduleLearningDtos);
        courseDto.setAssignmentList(assignmentDtos);
        return courseDto;
    }

    public CourseListOutputDto mapToCourseListOutputDto(Course course, CourseListOutputDto courseListOutputDto){
        courseListOutputDto.setId(course.getId());
        courseListOutputDto.setName(course.getName());
        courseListOutputDto.setNote(course.getNote());
        courseListOutputDto.setCredits(course.getCredits());
        courseListOutputDto.setTeacher(course.getTeacher());
        courseListOutputDto.setCredits(course.getCredits());
        courseListOutputDto.setTimeStart(course.getTimeStart());
        courseListOutputDto.setTimeEnd(course.getTimeEnd());
        courseListOutputDto.setNumberOfAssignment(course.getNumberOfAssignments());
        courseListOutputDto.setNumberOfLesion(course.getNumberOfLessons());
        courseListOutputDto.setAddressLearning(course.getAddressLearning());
        courseListOutputDto.setState(course.getState());

        return courseListOutputDto;
    }

    public CourseResponseDto mapToCourseResponseDto(Course course){

        List<ScheduleLearningDto> scheduleLearningDtos = new ArrayList<>();

        course.getScheduleLearnings().forEach(
                scheduleLearning -> {
                    scheduleLearningDtos.add(scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, new ScheduleLearningDto()));
                }
        );

        CourseResponseDto courseResponseDto = new CourseResponseDto();
        courseResponseDto.setId(course.getId());
        courseResponseDto.setName(course.getName());
        courseResponseDto.setNote(course.getNote());
        courseResponseDto.setTeacher(course.getTeacher());
        courseResponseDto.setTimeEnd(course.getTimeEnd());
        courseResponseDto.setTimeStart(course.getTimeStart());
        courseResponseDto.setAddressLearning(course.getAddressLearning());
        courseResponseDto.setState(course.getState());
        courseResponseDto.setScheduleLearningList(scheduleLearningDtos);
//        courseResponseDto.setAssignmentDtoList(assignmentMapper.mapToAssignmentDtoList(course.getAssignments()));
        return courseResponseDto;
    }

}