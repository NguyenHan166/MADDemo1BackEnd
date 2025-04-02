package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.dto.ScheduleLearningDto;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.ScheduleLearning;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.CourseRepository;
import com.nguyenhan.maddemo1.repository.ScheduleLearningRepository;
import com.nguyenhan.maddemo1.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleLearningService {

    private final ScheduleLearningRepository scheduleLearningRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public ScheduleLearningService(ScheduleLearningRepository scheduleLearningRepository, UserRepository userRepository, CourseRepository courseRepository) {
        this.scheduleLearningRepository = scheduleLearningRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public List<ScheduleLearning> findAll() {
        return scheduleLearningRepository.findAll();
    }

    public ScheduleLearning createScheduleLearning(ScheduleLearningDto scheduleLearningDto) {
        User user = userRepository.findById(scheduleLearningDto.getUserID()).orElseThrow(
                () -> new ResourceNotFoundException("User", "userID" , scheduleLearningDto.getUserID().toString())
        );
        Course course = courseRepository.findById(scheduleLearningDto.getCourseID()).orElseThrow(
                ()-> new ResourceNotFoundException("Course", "courseID" , scheduleLearningDto.getCourseID().toString())
        );
        return null;
    }

}
