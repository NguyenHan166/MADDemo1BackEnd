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
import org.springframework.stereotype.Component;

@Component
public class ScheduleLearningMapper {

    private final CourseRepository courseRepository;
    private final UserService userService;

    public ScheduleLearningMapper(UserService userService, CourseRepository courseRepository) {
        this.userService = userService;
        this.courseRepository = courseRepository;
    }

    public ScheduleLearning mapToScheduleLearning(ScheduleLearningDto scheduleLearningDto, ScheduleLearning scheduleLearning) {
        User user = userService.getAuthenticatedUser();
        scheduleLearning.setUser(user);
        scheduleLearning.setLearningAddresses(scheduleLearningDto.getLearningAddresses());
        scheduleLearning.setDescription(scheduleLearningDto.getDescription());
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
        scheduleLearningDto.setLearningAddresses(scheduleLearning.getLearningAddresses());
        scheduleLearningDto.setDescription(scheduleLearning.getDescription());
        scheduleLearningDto.setTeacher(scheduleLearning.getTeacher());
        scheduleLearningDto.setState(scheduleLearning.getState());
        scheduleLearningDto.setTimeEnd(scheduleLearning.getTimeEnd());
        scheduleLearningDto.setTimeStart(scheduleLearning.getTimeStart());
        if (scheduleLearning.getCourse() != null) scheduleLearningDto.setCourseID(scheduleLearning.getCourse().getId());
        return scheduleLearningDto;
    }

}
