package com.nguyenhan.maddemo1.mapper;

import com.nguyenhan.maddemo1.dto.CourseDto;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.UserRepository;
import com.nguyenhan.maddemo1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component // Biến CourseMapper thành Spring Bean
public class CourseMapper {

    private final UserRepository userRepository; // Không cần static

    @Autowired // Tiêm UserRepository qua constructor
    public CourseMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Course mapToCourse(CourseDto courseDto, Course course) {
        User user = userRepository.findById(courseDto.getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "userId", courseDto.getUserId().toString())
        );
        course.setName(courseDto.getName());
        course.setDescription(courseDto.getDescription());
        course.setTeacher(courseDto.getTeacher());
        course.setTimeEnd(courseDto.getTimeEnd());
        course.setTimeStart(courseDto.getTimeStart());
        course.setAddressLearning(courseDto.getAddressLearning());
        course.setUser(user);

        return course;
    }

    public CourseDto mapToCourseDto(Course course, CourseDto courseDto) {
        courseDto.setName(course.getName());
        courseDto.setDescription(course.getDescription());
        courseDto.setTeacher(course.getTeacher());
        courseDto.setTimeEnd(course.getTimeEnd());
        courseDto.setTimeStart(course.getTimeStart());
        courseDto.setAddressLearning(course.getAddressLearning());
        courseDto.setUserId(course.getUser().getId());
        return courseDto;
    }
}