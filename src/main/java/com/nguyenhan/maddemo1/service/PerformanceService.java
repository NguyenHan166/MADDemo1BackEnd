package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.config.PersonalWorkSecurity;
import com.nguyenhan.maddemo1.constants.Review;
import com.nguyenhan.maddemo1.constants.StateAssignment;
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


    public PerformanceDto getAllPerformance(PerformanceDto request){

        User user = getCurrentUser();
        PerformanceDto response = new PerformanceDto();

        Long userId = user.getId();
        LocalDateTime timeStart = request.getTimeStart();
        LocalDateTime timeEnd = request.getTimeEnd();

        List<ScheduleLearning> scheduleLearningList = scheduleLearningRepository.findByUserAndTimeStartGreaterThanEqualAndTimeEndLessThanEqual(user, timeStart, timeEnd);

        int presentCount = 0;
        int absentCount = 0;

        for (ScheduleLearning s : scheduleLearningList) {
            if (s.getState() == StateLesson.PRESENT) {
                presentCount++;
            } else if (s.getState() == StateLesson.ABSENT) {
                absentCount++;
            }
        }

//        log.info("CheduleS: {}", scheduleLearningList.size());
//        log.info("Present: {}", presentCount);
//        log.info("Absent: {}", absentCount);

        response.setPresent(presentCount);
        response.setAbsent(absentCount);

        response.setTimeStart(timeStart);
        response.setTimeEnd(timeEnd);

        List<Course> courseList = courseRepository.findByUserId(userId);
        List<CourseResponseDto> courseResponseDtoList = new ArrayList<>();
        for(Course c : courseList){
            CourseResponseDto courseResponseDto = courseMapper.mapToCourseResponseDto(c);
            courseResponseDto.setReview_sche(calculateReview_sche(c));
            courseResponseDto.setTotalScheduleLearning(c.getScheduleLearnings().size());
            courseResponseDto.setTotalScheduleLearningCurrent(calculateTotalScheduleLearningCurrent(c));
            courseResponseDto.setScheduleLearning_present(calculateScheduleLearningPresent(c));
            courseResponseDto.setScheduleLearning_absent(calculateTotalScheduleLearningCurrent(c)-calculateScheduleLearningPresent(c));
            courseResponseDto.setTotalAssignment(c.getAssignments().size());
            courseResponseDto.setTotalAssignmentCurrent(calculateTotalAssignmentCurrent(c));
            courseResponseDto.setAssignment_overdue(calculateAssignment_overdue(c));
            courseResponseDto.setReview_ass(calculateReview_ass(courseResponseDto.getAssignment_overdue(), courseResponseDto.getTotalAssignmentCurrent()));
            courseResponseDto.setPercent_review_all(calculatePercent_review_all(courseResponseDto.getScheduleLearning_present(), courseResponseDto.getTotalScheduleLearningCurrent(), courseResponseDto.getAssignment_overdue(), courseResponseDto.getTotalAssignmentCurrent()));

            courseResponseDtoList.add(courseResponseDto);
        }
        response.setCourseResponseDtoList(courseResponseDtoList);
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

    public int calculateAssignment_overdue(Course course){
        int count = 0;
        List<Assignment> assignmentList = course.getAssignments();
        for (Assignment a : assignmentList){
            if(a.getState() == StateAssignment.OVERDUE)
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
            if(s.getState() == StateLesson.PRESENT)
                count++;
        }
        return count;
    }

    public Review calculateReview_sche(Course course) {
        List<ScheduleLearning> scheduleLearningList = course.getScheduleLearnings();

        int totalLessons = 0;
        int attendedLessons = 0;

        for (ScheduleLearning s : scheduleLearningList) {
            if (s.getTimeStart().isBefore(LocalDateTime.now())) {
                totalLessons++;
                if (s.getState() == StateLesson.PRESENT) {
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

    public Review calculateReview_ass(int overdue, int current) {

        if (current == 0 || overdue == 0) return Review.GOOD;

        double ratio = (double) (current - overdue) / current;

        if (ratio >= 0.8) return Review.GOOD;
        if (ratio >= 0.6) return Review.WARNING;
        return Review.FAIL;
    }

    public int calculatePercent_review_all(int present, int total_sche, int overdue, int ass_current) {
        // Nếu chưa có lịch học và chưa có bài tập => hiệu suất 100%
        if (total_sche == 0 && ass_current == 0) {
            return 100;
        }

        double attendanceEfficiency = 0;
        double assignmentEfficiency = 0;

        if (total_sche > 0) {
            attendanceEfficiency = (double) present / total_sche;
        } else {
            // Nếu chưa có buổi học thì hiệu suất tham gia mặc định là 100%
            attendanceEfficiency = 1.0;
        }

        if (ass_current > 0) {
            assignmentEfficiency = (double) (ass_current - overdue) / ass_current;
        } else {
            // Nếu chưa có bài tập thì hiệu suất bài tập mặc định là 100%
            assignmentEfficiency = 1.0;
        }

        double totalEfficiency = (attendanceEfficiency + assignmentEfficiency) / 2;

        return (int) Math.round(totalEfficiency * 100); // Trả về phần trăm hiệu suất
    }



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
