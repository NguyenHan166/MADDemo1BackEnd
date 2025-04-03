package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.dto.ScheduleLearningDto;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.mapper.ScheduleLearningMapper;
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
    private final ScheduleLearningMapper scheduleLearningMapper;

    public ScheduleLearningService(ScheduleLearningRepository scheduleLearningRepository, UserRepository userRepository, CourseRepository courseRepository, ScheduleLearningMapper scheduleLearningMapper) {
        this.scheduleLearningRepository = scheduleLearningRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.scheduleLearningMapper = scheduleLearningMapper;
    }

    public List<ScheduleLearning> findAll() {
        return scheduleLearningRepository.findAll();
    }

    public List<ScheduleLearning> findAllByUserID(Long userID) {
        return scheduleLearningRepository.findAllByUserId(userID);
    }

    public ScheduleLearning fetchScheduleLearning(Long scheduleLearningID) {
        return scheduleLearningRepository.findById(scheduleLearningID).orElseThrow(
                () -> new ResourceNotFoundException("ScheduleLearning", "scheduleLearningID" , scheduleLearningID.toString())
        );
    }

    public ScheduleLearning createScheduleLearning(ScheduleLearningDto scheduleLearningDto) {
        ScheduleLearning scheduleLearning = scheduleLearningMapper.mapToScheduleLearning(scheduleLearningDto,new ScheduleLearning());
        User user = userRepository.findById(scheduleLearningDto.getUserID()).orElseThrow(
                () -> new ResourceNotFoundException("User", "userID" , scheduleLearningDto.getUserID().toString())
        );
        scheduleLearning.setUser(user);
        Course course = new Course();
        if (scheduleLearningDto.getCourseID() != null) {
            course = courseRepository.findById(scheduleLearningDto.getCourseID()).orElseThrow(
                    ()-> new ResourceNotFoundException("Course", "courseID" , scheduleLearningDto.getCourseID().toString())
            );
            scheduleLearning.setCourse(course);
        }

        return scheduleLearningRepository.save(scheduleLearning);
    }

    public boolean updateScheduleLearning(Long scheduleLearningID,ScheduleLearningDto scheduleLearningDto) {
        boolean isUpdated = false;
        ScheduleLearning scheduleLearning = scheduleLearningRepository.findById(scheduleLearningID).orElseThrow(
                () -> new ResourceNotFoundException("ScheduleLearning", "scheduleLearningID" , scheduleLearningID.toString())
        );
        User user = userRepository.findById(scheduleLearningDto.getUserID()).orElseThrow(
                () -> new ResourceNotFoundException("User", "userID" , scheduleLearningDto.getUserID().toString())
        );
        scheduleLearning = scheduleLearningMapper.mapToScheduleLearning(scheduleLearningDto,scheduleLearning);
        scheduleLearningRepository.save(scheduleLearning);
        isUpdated = true;
        return isUpdated;
    }

    public boolean deleteScheduleLearning(Long scheduleLearningID) {
        boolean isDeleted = false;
        ScheduleLearning scheduleLearning = fetchScheduleLearning(scheduleLearningID);
        scheduleLearningRepository.deleteById(scheduleLearningID);
        isDeleted = true;
        return isDeleted;
    }

}
