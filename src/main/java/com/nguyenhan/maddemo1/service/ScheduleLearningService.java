package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.dto.ScheduleLearningDto;
import com.nguyenhan.maddemo1.exception.ResourceAlreadyExistsException;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.mapper.ScheduleLearningMapper;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.ScheduleLearning;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.CourseRepository;
import com.nguyenhan.maddemo1.repository.ScheduleLearningRepository;
import com.nguyenhan.maddemo1.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ScheduleLearningService {

    private final ScheduleLearningRepository scheduleLearningRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ScheduleLearningMapper scheduleLearningMapper;
    private final UserService userService;

    public ScheduleLearningService(ScheduleLearningRepository scheduleLearningRepository, UserRepository userRepository, CourseRepository courseRepository, ScheduleLearningMapper scheduleLearningMapper, UserService userService) {
        this.scheduleLearningRepository = scheduleLearningRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.scheduleLearningMapper = scheduleLearningMapper;
        this.userService = userService;
    }

    public List<ScheduleLearning> findAll() {
        return scheduleLearningRepository.findAll();
    }

    public List<ScheduleLearning> findAllByUser(User user) {
        return scheduleLearningRepository.findByUser(user);
    }

    public ScheduleLearning fetchScheduleLearning(Long scheduleLearningID) {
        return scheduleLearningRepository.findById(scheduleLearningID).orElseThrow(
                () -> new ResourceNotFoundException("ScheduleLearning", "scheduleLearningID" , scheduleLearningID.toString())
        );
    }

    public ScheduleLearning createScheduleLearning(ScheduleLearningDto scheduleLearningDto) {
        ScheduleLearning scheduleLearning = scheduleLearningMapper.mapToScheduleLearning(scheduleLearningDto,new ScheduleLearning());
        User user = userService.getAuthenticatedUser();
        scheduleLearning.setUser(user);
        Course course = new Course();
        if (scheduleLearningDto.getCourseID() != null) {
            course = courseRepository.findById(scheduleLearningDto.getCourseID()).orElseThrow(
                    ()-> new ResourceNotFoundException("Course", "courseID" , scheduleLearningDto.getCourseID().toString())
            );

            for (ScheduleLearning scheduleLearning1 : course.getScheduleLearnings()){
                if (scheduleLearning1.getTimeStart().equals(scheduleLearningDto.getTimeStart()) || scheduleLearning1.getTimeEnd().equals(scheduleLearningDto.getTimeEnd())){
                    throw new ResourceAlreadyExistsException("ScheduleLearning", "scheduleLearningTime" , scheduleLearningDto.getTimeStart().toString());
                }
            }

            scheduleLearning.setCourse(course);
        }

        return scheduleLearningRepository.save(scheduleLearning);
    }

    public List<ScheduleLearning> getSchedulesByCourse(Course course) {
        return scheduleLearningRepository.findByCourseOrderByTimeStartAsc(course);
    }

    public List<ScheduleLearning> getSchedulesBetweenTimes(Course course ,LocalDateTime startTime, LocalDateTime endTime) {
        return scheduleLearningRepository.findByCourseAndTimeStartGreaterThanEqualAndTimeEndLessThanEqual(course ,startTime, endTime);
    }

    public boolean updateScheduleLearning(Long scheduleLearningID,ScheduleLearningDto scheduleLearningDto) {
        boolean isUpdated = false;
        ScheduleLearning scheduleLearning = scheduleLearningRepository.findById(scheduleLearningID).orElseThrow(
                () -> new ResourceNotFoundException("ScheduleLearning", "scheduleLearningID" , scheduleLearningID.toString())
        );

        scheduleLearning = scheduleLearningMapper.mapToScheduleLearning(scheduleLearningDto,scheduleLearning);
        scheduleLearningRepository.save(scheduleLearning);
        isUpdated = true;
        return isUpdated;
    }

    public ScheduleLearning updateStateScheduleLearning(Long scheduleLearningID, String newState) {
        ScheduleLearning scheduleLearning = scheduleLearningRepository.findById(scheduleLearningID).orElseThrow(
                () -> new ResourceNotFoundException("ScheduleLearning", "scheduleLearningID" , scheduleLearningID.toString())
        );
        scheduleLearning.setState(newState);
        return scheduleLearningRepository.save(scheduleLearning);
    }

    public boolean deleteScheduleLearning(Long scheduleLearningID) {
        boolean isDeleted = false;
        ScheduleLearning scheduleLearning = fetchScheduleLearning(scheduleLearningID);
        scheduleLearningRepository.deleteById(scheduleLearningID);
        isDeleted = true;
        return isDeleted;
    }

}
