package com.nguyenhan.maddemo1.controller;

import com.nguyenhan.maddemo1.constants.ResponseConstants;
import com.nguyenhan.maddemo1.constants.StateScheduleLearning;
import com.nguyenhan.maddemo1.dto.ScheduleLearningDto;
import com.nguyenhan.maddemo1.mapper.ScheduleLearningMapper;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.ScheduleLearning;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.responses.ErrorResponseDto;
import com.nguyenhan.maddemo1.responses.ResponseDto;
import com.nguyenhan.maddemo1.service.CourseService;
import com.nguyenhan.maddemo1.service.ScheduleLearningService;
import com.nguyenhan.maddemo1.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/scheduleLearnings")
public class ScheduleLearningController {

    private final ScheduleLearningService scheduleLearningService;
    private final ScheduleLearningMapper scheduleLearningMapper;
    private final UserService userService;
    private final CourseService courseService;

    public ScheduleLearningController(ScheduleLearningService scheduleLearningService, ScheduleLearningMapper scheduleLearningMapper, UserService userService, CourseService courseService) {
        this.scheduleLearningService = scheduleLearningService;
        this.scheduleLearningMapper = scheduleLearningMapper;
        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping("/fetch")
    public ResponseEntity<ScheduleLearningDto> fetchScheduleLearningById(Long id) {
        ScheduleLearning scheduleLearning = scheduleLearningService.fetchScheduleLearning(id);
        ScheduleLearningDto dto = scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, new ScheduleLearningDto());
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(dto);
    }

    @GetMapping("/")
    public ResponseEntity<Object> fetchAllScheduleLearningOfUSer() {

        User findUser = userService.getAuthenticatedUser();
//        if (!findUser.getId().equals(userId)) {
//            return ResponseEntity.status(ResponseConstants.STATUS_409).body(new ErrorResponseDto(
//                    "/api/scheduleLearnings/{userId}",
//                    ResponseConstants.STATUS_409,
//                    ResponseConstants.MESSAGE_409,
//                    LocalDateTime.now()
//            ));
//        }

        List<ScheduleLearningDto> scheduleLearningDtoList = new ArrayList<>();
        scheduleLearningService.findAllByUser(findUser).forEach(
            scheduleLearning -> {
                scheduleLearningDtoList.add(scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, new ScheduleLearningDto()));
            }
        );
        return  ResponseEntity.status(ResponseConstants.STATUS_200).body(scheduleLearningDtoList);
    }

    @PostMapping("/create")
    public ResponseEntity<ScheduleLearningDto> createScheduleLearning(@RequestBody ScheduleLearningDto dto) {
        if (LocalDateTime.now().isBefore(dto.getTimeStart())){
            dto.setState(StateScheduleLearning.CHUADEN.toString());
        }else if (LocalDateTime.now().isAfter(dto.getTimeEnd())){
            dto.setState(StateScheduleLearning.VANG.toString());
        }else{
            dto.setState(StateScheduleLearning.COMAT.toString());
        }
        ScheduleLearning scheduleLearning = scheduleLearningService.createScheduleLearning(dto);
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, dto));
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateScheduleLearning(@RequestParam Long id, @RequestBody ScheduleLearningDto dto) {
        boolean isSuccess = scheduleLearningService.updateScheduleLearning(id, dto);
        if (isSuccess) {
            return ResponseEntity.status(ResponseConstants.STATUS_200).body(dto);
        }else {
            return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ResponseDto(ResponseConstants.STATUS_417, ResponseConstants.MESSAGE_417_UPDATE));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteScheduleLearning(@RequestParam Long id) {
        boolean isSuccess = scheduleLearningService.deleteScheduleLearning(id);
        if (isSuccess) {
            return ResponseEntity.status(ResponseConstants.STATUS_200).body(new ResponseDto(ResponseConstants.STATUS_200, ResponseConstants.MESSAGE_200));
        }else{
            return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ResponseDto(ResponseConstants.STATUS_417, ResponseConstants.MESSAGE_417_DELETE));
        }
    }

    @GetMapping("/course")
    public ResponseEntity<Object> getSchedulesByCourse(@RequestParam Long courseId) {
        Course course = courseService.findById(courseId); // Tìm khóa học theo ID
        List<ScheduleLearningDto> scheduleLearningDtoList = new ArrayList<>();
        scheduleLearningService.getSchedulesByCourse(course).forEach(
                scheduleLearning -> {
                    scheduleLearningDtoList.add(scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, new ScheduleLearningDto()));
                }
        );
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(scheduleLearningDtoList);
    }

    // API lấy danh sách các buổi học trong khoảng thời gian
    @GetMapping("/time")
    public ResponseEntity<Object> getSchedulesBetweenTimes(
            @RequestParam("courseId") Long courseId,
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Course course = courseService.findById(courseId);
        List<ScheduleLearningDto> scheduleLearningDtoList = new ArrayList<>();
        scheduleLearningService.getSchedulesBetweenTimes(course ,startTime, endTime).forEach(
                scheduleLearning -> {
                    scheduleLearningDtoList.add(scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, new ScheduleLearningDto()));
                }
        );
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(scheduleLearningDtoList);
    }
}
