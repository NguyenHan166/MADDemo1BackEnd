package com.nguyenhan.maddemo1.mapper;

import com.nguyenhan.maddemo1.dto.AssignmentDto;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.model.Assignment;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.CourseRepository;
import com.nguyenhan.maddemo1.service.CourseService;
import com.nguyenhan.maddemo1.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class AssignmentMapper {

    private final UserService userService;
    private final CourseRepository courseRepository;

    public AssignmentMapper(UserService userService, CourseRepository courseRepository) {
        this.userService = userService;
        this.courseRepository = courseRepository;
    }

    public Assignment mapToAssignment(AssignmentDto assignmentDto, Assignment assignment){
        assignment.setName(assignmentDto.getName());
        assignment.setTimeEnd(assignmentDto.getTimeEnd());
        assignment.setState(assignmentDto.getState());
        assignment.setNote(assignmentDto.getNote());
        assignment.setUser(userService.getAuthenticatedUser());

        if (assignmentDto.getCourseId() != null) {
            Course course = courseRepository.findById(assignmentDto.getCourseId()).orElseThrow(
                    () ->  new ResourceNotFoundException("Course" , "Id", assignmentDto.getCourseId().toString())
            );
            assignment.setCourse(course);
        }
        return assignment;
    }

    public AssignmentDto mapToAssignmentDto(Assignment assignment, AssignmentDto assignmentDto){
        assignmentDto.setId(assignment.getId());
        assignmentDto.setCourseId(assignment.getCourse().getId());
        assignmentDto.setName(assignment.getName());
        assignmentDto.setTimeEnd(assignment.getTimeEnd());
        assignmentDto.setState(assignment.getState());
        assignmentDto.setNote(assignment.getNote());
        return assignmentDto;
    }
}
