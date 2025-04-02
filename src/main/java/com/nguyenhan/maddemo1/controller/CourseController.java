package com.nguyenhan.maddemo1.controller;

import com.nguyenhan.maddemo1.constants.ResponseConstants;
import com.nguyenhan.maddemo1.dto.CourseDto;
import com.nguyenhan.maddemo1.mapper.CourseMapper;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.responses.ResponseDto;
import com.nguyenhan.maddemo1.service.CourseService;
import com.nguyenhan.maddemo1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("api/courses/")
@RestController
public class CourseController {

    private final UserService userService;
    private final CourseService courseService;
    private final CourseMapper courseMapper;

    public CourseController(CourseService courseService, UserService userService, CourseMapper courseMapper) {
        this.courseService = courseService;
        this.userService = userService;
        this.courseMapper = courseMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseDto courseDto) {
        Course course = courseService.createCourse(courseDto);
        return ResponseEntity.status(ResponseConstants.STATUS_201).body(courseDto);
    }

    @GetMapping("/")
    public ResponseEntity<List<CourseDto>> getAllCourses(@RequestParam Long userId) {
        List<CourseDto> courseDtosList = new ArrayList<>();
        courseService.listCoursesOfUser(userId).forEach(
                course -> {
                    CourseDto courseDto = courseMapper.mapToCourseDto(course, new CourseDto());
                    courseDtosList.add(courseDto);
                }
        );
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(courseDtosList);
    }

    @GetMapping("/fetch")
    public ResponseEntity<CourseDto> getCourse(@RequestParam Long courseId) {
        Course course = courseService.findById(courseId);
        CourseDto courseDto = courseMapper.mapToCourseDto(course, new CourseDto());
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(courseDto);
    }

    @PutMapping("/update")
    public ResponseEntity<CourseDto> updateCourse(@RequestParam Long courseId ,@Valid @RequestBody CourseDto courseDto) {
        boolean isUpdate = courseService.updateCourse(courseId, courseDto);
        if (isUpdate) {
            return ResponseEntity.status(ResponseConstants.STATUS_417).body(courseDto);
        }else{
            return ResponseEntity.status(ResponseConstants.STATUS_417).body(courseDto);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteCourse(@RequestParam Long courseId) {
        boolean isDelete = courseService.deleteCourse(courseId);
        if (isDelete) {
            return ResponseEntity.status(ResponseConstants.STATUS_200).body(new ResponseDto(ResponseConstants.STATUS_200, ResponseConstants.MESSAGE_200));
        }else{
            return ResponseEntity.status(ResponseConstants.STATUS_400).body(new ResponseDto(ResponseConstants.STATUS_417, ResponseConstants.MESSAGE_417_DELETE));
        }
    }
}
