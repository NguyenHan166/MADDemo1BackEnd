package com.nguyenhan.maddemo1.controller;

import com.nguyenhan.maddemo1.constants.ResponseConstants;
import com.nguyenhan.maddemo1.dto.CourseDto;
import com.nguyenhan.maddemo1.mapper.CourseMapper;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.responses.ErrorResponseDto;
import com.nguyenhan.maddemo1.responses.ResponseDto;
import com.nguyenhan.maddemo1.service.CourseService;
import com.nguyenhan.maddemo1.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("api/courses")
@RestController
@Validated
public class CourseController {

    private final UserService userService;
    private final CourseService courseService;
    private final CourseMapper courseMapper;

    public CourseController(CourseService courseService, UserService userService, CourseMapper courseMapper) {
        this.courseService = courseService;
        this.userService = userService;
        this.courseMapper = courseMapper;
    }

    @Operation(summary = "Tạo khóa học", description = "Tạo một khóa học mới và lưu vào cơ sở dữ liệu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Khóa học đã được tạo thành công", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Course.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @PostMapping("/create")
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CourseDto courseDto, @RequestParam String listDay) {
        Course course = courseService.createCourse(courseDto);
        return ResponseEntity.status(ResponseConstants.STATUS_201).body(course);
    }

    @Operation(summary = "Lấy tất cả khóa học của người dùng", description = "Lấy danh sách các khóa học của người dùng theo userId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách khóa học của người dùng", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CourseDto.class))),
            @ApiResponse(responseCode = "409", description = "User ID không hợp lệ")
    })
    @GetMapping("/")
    public ResponseEntity<Object> getAllCourses() {
        User findUser = userService.getAuthenticatedUser();

        List<CourseDto> courseDtosList = new ArrayList<>();
        courseService.getCoursesByUser(findUser).forEach(
                course -> {
                    CourseDto courseDto = courseMapper.mapToCourseDto(course, new CourseDto());
                    courseDtosList.add(courseDto);
                }
        );
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(courseDtosList);
    }

    @Operation(summary = "Lấy thông tin khóa học", description = "Lấy thông tin chi tiết về khóa học theo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thông tin khóa học", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CourseDto.class))),
            @ApiResponse(responseCode = "404", description = "Khóa học không tồn tại")
    })
    @GetMapping("/fetch")
    public ResponseEntity<CourseDto> getCourse(
            @Parameter(description = "Id của course cần xem", required = true)
            @RequestParam Long courseId) {
        Course course = courseService.findById(courseId);
        CourseDto courseDto = courseMapper.mapToCourseDto(course, new CourseDto());
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(courseDto);
    }

    @Operation(summary = "Cập nhật thông tin khóa học", description = "Cập nhật thông tin khóa học theo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "417", description = "Cập nhật không thành công", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CourseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @PutMapping("/update")
    public ResponseEntity<Object> updateCourse(
            @Parameter(description = "ID của course cần update", required = true)
            @RequestParam Long courseId ,@Valid @RequestBody CourseDto courseDto) {
        boolean isUpdate = courseService.updateCourse(courseId, courseDto);
        if (isUpdate) {
            courseDto.setId(courseId);
            return ResponseEntity.status(ResponseConstants.STATUS_200).body(courseDto);
        }else{
            return ResponseEntity.status(ResponseConstants.STATUS_417).body(courseDto);
        }
    }


    @Operation(summary = "Xóa khóa học", description = "Xóa một khóa học khỏi cơ sở dữ liệu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Khóa học đã được xóa thành công", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Khóa học không tồn tại hoặc không thể xóa")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteCourse(
            @Parameter(description = "ID của course cần xóa", required = true)
            @RequestParam Long courseId) {
        boolean isDelete = courseService.deleteCourse(courseId);
        if (isDelete) {
            return ResponseEntity.status(ResponseConstants.STATUS_200).body(new ResponseDto(ResponseConstants.STATUS_200, ResponseConstants.MESSAGE_200));
        }else{
            return ResponseEntity.status(ResponseConstants.STATUS_400).body(new ResponseDto(ResponseConstants.STATUS_417, ResponseConstants.MESSAGE_417_DELETE));
        }
    }


    // API lấy các khóa học có trạng thái "Đang diễn ra"
    @GetMapping("/state")
    public ResponseEntity<Object> getCoursesByState(
            @RequestParam Long userId,
            @RequestParam String state) {
        User user = userService.getAuthenticatedUser();
        List<CourseDto> courseDtosList = new ArrayList<>();
        courseService.getCoursesByState(user,state).forEach(
                course -> {
                    CourseDto courseDto = courseMapper.mapToCourseDto(course, new CourseDto());
                    courseDtosList.add(courseDto);
                }
        );
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(courseDtosList);
    }

    // API lấy các khóa học trong khoảng thời gian cho trước
    @GetMapping("/time")
    public ResponseEntity<Object> getCoursesBetweenTimes(
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<CourseDto> courseDtosList = new ArrayList<>();
        courseService.getCoursesBetweenTimes(startTime, endTime).forEach(
                course -> {
                    CourseDto courseDto = courseMapper.mapToCourseDto(course, new CourseDto());
                    courseDtosList.add(courseDto);
                }
        );
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(courseDtosList);
    }
}
