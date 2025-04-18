package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.config.PersonalWorkSecurity;
import com.nguyenhan.maddemo1.constants.Review;
import com.nguyenhan.maddemo1.constants.StateLesson;
import com.nguyenhan.maddemo1.dto.CourseResponseDto;
import com.nguyenhan.maddemo1.dto.PerformanceDto;
import com.nguyenhan.maddemo1.mapper.CourseMapper;
import com.nguyenhan.maddemo1.mapper.PersonalWorkMapper;
import com.nguyenhan.maddemo1.model.Assignment;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.ScheduleLearning;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class PerformanceService {
    private final AssignmentRepository assignmentRepository;
    private final PersonalWorkRepository personalWorkRepository;
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final UserRepository userRepository;
    private final ScheduleLearningRepository scheduleLearningRepository;

    public PerformanceService(PersonalWorkRepository personalWorkRepository, CourseRepository courseRepository, UserRepository userRepository, CourseMapper courseMapper, PersonalWorkMapper personalWorkMapper, PersonalWorkSecurity personalWorkSecurity, ScheduleLearningRepository scheduleLearningRepository,
                              AssignmentRepository assignmentRepository, CourseMapper courseMapper1) {
        this.personalWorkRepository = personalWorkRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.scheduleLearningRepository = scheduleLearningRepository;
        this.assignmentRepository = assignmentRepository;
        this.courseMapper = courseMapper1;
    }

//    @Transactional
//    @PreAuthorize("@personalWorkSecurity.isOwner(#id)")
//    public PersonalWork updatePersonalWork(PersonalWorkUpdateDto request, Long id){
//        log.info("Id personalwork update: {}", id);
//        PersonalWork personalWork = personalWorkRepository.findById(id).orElseThrow(
//                () -> new ResourceNotFoundException("PersonalWork", "id", id.toString())
//        );
//
//        log.info("Ten per:{}", personalWork.getName());
//
//        personalWorkMapper.updatePersonalWorkMapper(personalWork, request);
//        log.info("Ten per:{}", personalWork.getName());
//        return personalWorkRepository.save(personalWork);
//    }
//
//    @PreAuthorize("@personalWorkSecurity.isOwner(#id)")
//    public void deletePersonalWork(Long id){
//        PersonalWork personalWork = personalWorkRepository.findById(id).orElseThrow(
//                () -> new ResourceNotFoundException("PersonalWork", "id", id.toString())
//        );
//
//        personalWorkRepository.deleteById(id);
//        log.info("PersonalWork with ID {} deleted successfully.", id);
//    }

    public PerformanceDto getAllPerformance(PerformanceDto request){
        User user = getCurrentUser();
        PerformanceDto response = new PerformanceDto();

        Long userId = user.getId();
        LocalDateTime timeStart = request.getTimeStart();
        LocalDateTime timeEnd = request.getTimeEnd();

        List<ScheduleLearning> scheduleLearningList = scheduleLearningRepository.findByUserIdAndTimeStartBetween(userId, timeStart, timeEnd);

        int presentCount = 0;
        int absentCount = 0;

        for (ScheduleLearning s : scheduleLearningList) {
            if (s.getStateLesson() == StateLesson.PRESENT) {
                presentCount++;
            } else if (s.getStateLesson() == StateLesson.ABSENT) {
                absentCount++;
            }
        }

        response.setPresent(presentCount);
        response.setAbsent(absentCount);

        List<Course> courseList = courseRepository.findByUserId(userId);
        List<CourseResponseDto> courseResponseDtoList = new ArrayList<>();
        for(Course c : courseList){
            CourseResponseDto courseResponseDto = courseMapper.mapToCourseResponseDto(c);
            courseResponseDto.setReview(calculateReview(c));
            courseResponseDto.setTotalScheduleLearning(c.getScheduleLearnings().size());
            courseResponseDto.setTotalScheduleLearningCurrent(calculateTotalScheduleLearningCurrent(c));
            courseResponseDto.setScheduleLearning_present(calculateScheduleLearningPresent(c));
            courseResponseDto.setScheduleLearning_absent(calculateTotalScheduleLearningCurrent(c)-calculateScheduleLearningPresent(c));
            courseResponseDto.setTotalAssignment(c.getAssignments().size());
            courseResponseDto.setTotalAssignmentCurrent(calculateTotalAssignmentCurrent(c));
        }
        return response;
    }

    public int calculateTotalAssignmentCurrent( Course course){
        int count = 0;
        List<Assignment> assignmentList = course.getAssignments();
        for (Assignment a : assignmentList){
            if(a.getTimeEnd().isBefore(LocalDateTime.now()))
                count++;
        }
        return count;
    }

    public int calculateTotalScheduleLearningCurrent(Course course){
        int count = 0;
        List<ScheduleLearning> scheduleLearningList = course.getScheduleLearnings();
        for(ScheduleLearning s : scheduleLearningList){
            if(s.getTimeEnd().isBefore(LocalDateTime.now()))
                count++;
        }
        return count;
    }

    public int calculateScheduleLearningPresent(Course course){
        int count = 0;
        List<ScheduleLearning> scheduleLearningList = course.getScheduleLearnings();
        for(ScheduleLearning s : scheduleLearningList){
            if(s.getStateLesson().compareTo(StateLesson.PRESENT) > 0)
                count++;
        }
        return count;
    }

    public Review calculateReview(Course course) {
        List<ScheduleLearning> scheduleLearningList = course.getScheduleLearnings();

        int totalLessons = 0;
        int attendedLessons = 0;

        for (ScheduleLearning s : scheduleLearningList) {
            if (s.getTimeStart().isBefore(LocalDateTime.now())) {
                totalLessons++;
                if (s.getStateLesson() == StateLesson.PRESENT) {
                    attendedLessons++;
                }
            }
        }

        if (totalLessons == 0) return null;

        double ratio = (double) attendedLessons / totalLessons;

        if (ratio >= 0.8) return Review.GOOD;
        if (ratio >= 0.6) return Review.WARNING;
        return Review.FAIL;
    }


//    public List<PersonalWork> getPersonalWorksByCourseId(Long courseId){
//        return personalWorkRepository.findByCourseId(courseId);
//    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("PersonalWork Service: principal: {}", principal);

        if (principal instanceof User) {
            return (User) principal;
        } else if (principal instanceof String) {
            // Trường hợp khi principal là email (chuỗi)
            String username = (String) principal;
            return userRepository.findByEmail(username).orElseThrow(() ->
                    new UsernameNotFoundException("User not found with email: " + username));
        }

        return null; // hoặc throw exception nếu không tìm thấy
    }
}
