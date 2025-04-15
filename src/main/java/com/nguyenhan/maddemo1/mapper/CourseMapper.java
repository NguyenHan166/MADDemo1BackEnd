package com.nguyenhan.maddemo1.mapper;

import com.nguyenhan.maddemo1.dto.CourseDto;
import com.nguyenhan.maddemo1.dto.ScheduleLearningDto;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.ScheduleLearning;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.UserRepository;
import com.nguyenhan.maddemo1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component // Biến CourseMapper thành Spring Bean
public class CourseMapper {

    private final UserRepository userRepository; // Không cần static
    private final UserService userService;
    public ScheduleLearningMapper scheduleLearningMapper;

    @Autowired // Tiêm UserRepository qua constructor
    public CourseMapper(UserRepository userRepository, UserService userService, ScheduleLearningMapper scheduleLearningMapper) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.scheduleLearningMapper = scheduleLearningMapper;
    }

    public Course mapToCourse(CourseDto courseDto, Course course) {
        User user = userService.getAuthenticatedUser();
        course.setName(courseDto.getName());
        course.setDescription(courseDto.getDescription());
        course.setTeacher(courseDto.getTeacher());
        course.setTimeEnd(courseDto.getTimeEnd());
        course.setTimeStart(courseDto.getTimeStart());
        course.setAddressLearning(courseDto.getAddressLearning());
        course.setState(courseDto.getState());
        course.setUser(user);

        return course;
    }

    public CourseDto mapToCourseDto(Course course, CourseDto courseDto) {

        List<ScheduleLearningDto> scheduleLearningDtos = new ArrayList<>();

        course.getScheduleLearnings().forEach(
                scheduleLearning -> {
                    scheduleLearningDtos.add(scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, new ScheduleLearningDto()));
                }
        );

        courseDto.setId(course.getId());
        courseDto.setName(course.getName());
        courseDto.setDescription(course.getDescription());
        courseDto.setTeacher(course.getTeacher());
        courseDto.setTimeEnd(course.getTimeEnd());
        courseDto.setTimeStart(course.getTimeStart());
        courseDto.setAddressLearning(course.getAddressLearning());
        courseDto.setState(course.getState());
        courseDto.setScheduleLearningList(scheduleLearningDtos);
        return courseDto;
    }
}