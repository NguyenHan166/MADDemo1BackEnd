package com.nguyenhan.maddemo1.controller;

import com.nguyenhan.maddemo1.constants.ResponseConstants;
import com.nguyenhan.maddemo1.constants.StateAssignment;
import com.nguyenhan.maddemo1.dto.AssignmentDto;
import com.nguyenhan.maddemo1.mapper.AssignmentMapper;
import com.nguyenhan.maddemo1.model.Assignment;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.responses.ErrorResponseDto;
import com.nguyenhan.maddemo1.responses.ResponseDto;
import com.nguyenhan.maddemo1.service.AssignmentService;
import com.nguyenhan.maddemo1.service.CourseService;
import com.nguyenhan.maddemo1.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final UserService userService;
    private final CourseService courseService;
    private final AssignmentMapper assignmentMapper;

    public AssignmentController(AssignmentService assignmentService, UserService userService, AssignmentMapper assignmentMapper, CourseService courseService) {
        this.assignmentService = assignmentService;
        this.userService = userService;
        this.assignmentMapper = assignmentMapper;
        this.courseService = courseService;
    }

    @GetMapping("/")
    public ResponseEntity<Object> getAssignmentsOfUser() {
        User user = userService.getAuthenticatedUser();
        List<Assignment> assignments = assignmentService.findAllByUser(user);
        List<AssignmentDto> assignmentDtos = new ArrayList<>();
        assignments.forEach(assignment -> {
            assignmentDtos.add(assignmentMapper.mapToAssignmentDto(assignment, new AssignmentDto()));
        });

        return ResponseEntity.status(ResponseConstants.STATUS_200).body(assignmentDtos);
    }

    @GetMapping("/course")
    public ResponseEntity<Object> getAssignmentsOfCourse(@RequestParam Long courseID) {
        User user = userService.getAuthenticatedUser();
        Course course = courseService.findById(courseID);
        List<Assignment> assignments = assignmentService.findAllByCourse(course);

        List<AssignmentDto> assignmentDtos = new ArrayList<>();
        assignments.forEach(assignment -> {
            assignmentDtos.add(assignmentMapper.mapToAssignmentDto(assignment, new AssignmentDto()));
        });
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(assignmentDtos);
    }

    @GetMapping("/fetch")
    public ResponseEntity<Object> fetchAssignmentDetails(@RequestParam Long assignmentID) {
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(assignmentService.findById(assignmentID));
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createAssignment(@RequestBody AssignmentDto assignmentDto) {
        if (LocalDateTime.now().isAfter(assignmentDto.getTimeEnd())){
            assignmentDto.setState(StateAssignment.QUAHAN.toString());
        }else if (LocalDateTime.now().isBefore(assignmentDto.getTimeEnd())){
            assignmentDto.setState(StateAssignment.CHUAHOANTHANH.toString());
        }
        Assignment assignment = assignmentService.create(assignmentDto);
        assignmentMapper.mapToAssignmentDto(assignment, assignmentDto);
        return ResponseEntity.status(ResponseConstants.STATUS_201).body(assignmentDto);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateAssignment(Long id , @RequestBody AssignmentDto assignmentDto) {
        boolean success = assignmentService.update(id, assignmentDto);
        if (success) {
            Assignment assignment = assignmentService.findById(id);
            assignmentMapper.mapToAssignmentDto(assignment, assignmentDto);
            return ResponseEntity.status(ResponseConstants.STATUS_200).body(assignmentDto);
        }else{
            return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ErrorResponseDto(
                    "/api/assignments/update",
                    ResponseConstants.STATUS_417,
                    ResponseConstants.MESSAGE_417_UPDATE,
                    LocalDateTime.now()
            ));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteAssignment(Long id) {
        boolean success = assignmentService.delete(id);
        if (success) {
            return ResponseEntity.status(ResponseConstants.STATUS_200).body(new ResponseDto(ResponseConstants.STATUS_200, ResponseConstants.MESSAGE_200));
        }else{
            return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ErrorResponseDto(
                    "/api/assignment/delete",
                    ResponseConstants.STATUS_417,
                    ResponseConstants.MESSAGE_417_UPDATE,
                    LocalDateTime.now()
            ));
        }
    }
}
