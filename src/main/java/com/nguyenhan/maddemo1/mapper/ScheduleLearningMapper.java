package com.nguyenhan.maddemo1.mapper;

import com.nguyenhan.maddemo1.dto.ScheduleLearningDto;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.ScheduleLearning;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.CourseRepository;
import com.nguyenhan.maddemo1.repository.UserRepository;
import com.nguyenhan.maddemo1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ScheduleLearningMapper {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public ScheduleLearningMapper(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public ScheduleLearning mapToScheduleLearning(ScheduleLearningDto scheduleLearningDto, ScheduleLearning scheduleLearning) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        scheduleLearning.setUser(user);
        scheduleLearning.setName(scheduleLearningDto.getName());
        scheduleLearning.setLearningAddresses(scheduleLearningDto.getLearningAddresses());
        scheduleLearning.setNote(scheduleLearningDto.getNote());
        scheduleLearning.setTeacher(scheduleLearningDto.getTeacher());
        scheduleLearning.setState(scheduleLearningDto.getState());
        scheduleLearning.setTimeEnd(scheduleLearningDto.getTimeEnd());
        scheduleLearning.setTimeStart(scheduleLearningDto.getTimeStart());

        if (scheduleLearningDto.getCourseID() != null) {
            Course course = courseRepository.findById(scheduleLearningDto.getCourseID()).orElseThrow(
                    () -> new ResourceNotFoundException("Course", "courseId", scheduleLearningDto.getCourseID().toString())
            );
            scheduleLearning.setCourse(course);
        }

        return scheduleLearning;
    }

    public ScheduleLearningDto mapToScheduleLearningDto(ScheduleLearning scheduleLearning, ScheduleLearningDto scheduleLearningDto) {
        scheduleLearningDto.setId(scheduleLearning.getId());
        scheduleLearningDto.setName(scheduleLearning.getName());
        scheduleLearningDto.setLearningAddresses(scheduleLearning.getLearningAddresses());
        scheduleLearningDto.setNote(scheduleLearning.getNote());
        scheduleLearningDto.setTeacher(scheduleLearning.getTeacher());
        scheduleLearningDto.setState(scheduleLearning.getState());
        scheduleLearningDto.setTimeEnd(scheduleLearning.getTimeEnd());
        scheduleLearningDto.setTimeStart(scheduleLearning.getTimeStart());
        if (scheduleLearning.getCourse() != null) scheduleLearningDto.setCourseID(scheduleLearning.getCourse().getId());
        return scheduleLearningDto;
    }

}
