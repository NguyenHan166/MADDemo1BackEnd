package com.nguyenhan.maddemo1.mapper;

import com.nguyenhan.maddemo1.dto.AssignmentDto;
import com.nguyenhan.maddemo1.model.Assignment;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.service.CourseService;
import com.nguyenhan.maddemo1.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class AssignmentMapper {

    private final UserService userService;
    private final CourseService courseService;

    public AssignmentMapper(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    public Assignment mapToAssignment(AssignmentDto assignmentDto, Assignment assignment){
        assignment.setName(assignmentDto.getName());
        assignment.setTimeEnd(assignmentDto.getTimeEnd());
        assignment.setState(assignmentDto.getState());
        return assignment;
    }

    public AssignmentDto mapToAssignmentDto(Assignment assignment, AssignmentDto assignmentDto){
        assignmentDto.setId(assignment.getId());
        assignmentDto.setCourseId(assignment.getCourse().getId());
        assignmentDto.setName(assignment.getName());
        assignmentDto.setTimeEnd(assignment.getTimeEnd());
        assignmentDto.setState(assignment.getState());
        return assignmentDto;
    }
}
