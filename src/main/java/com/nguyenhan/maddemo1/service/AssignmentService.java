package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.dto.AssignmentDto;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.mapper.AssignmentMapper;
import com.nguyenhan.maddemo1.model.Assignment;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.ScheduleLearning;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.AssignmentRepository;
import com.nguyenhan.maddemo1.repository.CourseRepository;
import com.nguyenhan.maddemo1.repository.ScheduleLearningRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final UserService userService;
    private final AssignmentMapper assignmentMapper;
    private final ScheduleLearningRepository scheduleLearningRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, CourseRepository courseRepository, UserService userService, AssignmentMapper assignmentMapper, ScheduleLearningRepository scheduleLearningRepository) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.userService = userService;
        this.assignmentMapper = assignmentMapper;
        this.scheduleLearningRepository = scheduleLearningRepository;
    }

    public List<Assignment> findAllByUser(User user) {
        return assignmentRepository.findByUserOrderByTimeEndAsc(user);
    }

    public List<Assignment> findAllByCourse(Course course) {
        return assignmentRepository.findByCourseOrderByTimeEndAsc(course);
    }

    public Assignment findById(Long id) {
        return assignmentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Assignment" , "Id" , id.toString())
        );
    }

    public Assignment create(AssignmentDto assignmentDto) {
        Course course = courseRepository.findById(assignmentDto.getCourseId()).orElseThrow(
                () -> new ResourceNotFoundException("Course" , "Id" , assignmentDto.getCourseId().toString())
        );

        User user = userService.getAuthenticatedUser();
        Assignment assignment = assignmentMapper.mapToAssignment(assignmentDto, new Assignment());
        assignment.setUser(user);
        assignment.setCourse(course);
        return assignmentRepository.save(assignment);
    }

    public boolean update(Long id ,AssignmentDto assignmentDto) {
        boolean isUpdated = false;
        Assignment assignment = assignmentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Assignment" , "Id" , id.toString())
        );

        Course course = courseRepository.findById(assignmentDto.getCourseId()).orElseThrow(
                () -> new ResourceNotFoundException("Course" , "Id" , id.toString())
        );

        if (!course.getAssignments().contains(assignment)) {
            throw new ResourceNotFoundException("Assignment in course" , "Id" , id.toString());
        }

        assignmentMapper.mapToAssignment(assignmentDto, assignment);
        assignmentRepository.save(assignment);
        isUpdated = true;
        return isUpdated;
    }

    public boolean delete(Long id) {
        boolean isDeleted = false;
        Assignment assignment = assignmentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Assignment" , "Id" , id.toString())
        );
        scheduleLearningRepository.deleteById(id);
        isDeleted = true;
        return isDeleted;
    }
}
