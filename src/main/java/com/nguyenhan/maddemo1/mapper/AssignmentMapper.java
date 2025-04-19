package com.nguyenhan.maddemo1.mapper;

import com.nguyenhan.maddemo1.dto.AssignmentDto;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.model.Assignment;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.CourseRepository;
import com.nguyenhan.maddemo1.repository.UserRepository;
import com.nguyenhan.maddemo1.service.CourseService;
import com.nguyenhan.maddemo1.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AssignmentMapper {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public AssignmentMapper(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public Assignment mapToAssignment(AssignmentDto assignmentDto, Assignment assignment){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        assignment.setName(assignmentDto.getName());
        assignment.setTimeEnd(assignmentDto.getTimeEnd());
        assignment.setState(assignmentDto.getState());
        assignment.setNote(assignmentDto.getNote());
        assignment.setUser(user);

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
