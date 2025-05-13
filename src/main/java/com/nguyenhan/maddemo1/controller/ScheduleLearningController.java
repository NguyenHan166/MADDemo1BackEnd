package com.nguyenhan.maddemo1.controller;

import com.nguyenhan.maddemo1.constants.*;
import com.nguyenhan.maddemo1.dto.NotificationDto;
import com.nguyenhan.maddemo1.dto.ScheduleLearningDto;
import com.nguyenhan.maddemo1.mapper.ScheduleLearningMapper;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.ScheduleLearning;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.responses.ErrorResponseDto;
import com.nguyenhan.maddemo1.responses.ResponseDto;
import com.nguyenhan.maddemo1.responses.ScheduleResponse;
import com.nguyenhan.maddemo1.service.CourseService;
import com.nguyenhan.maddemo1.service.NotificationService;
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
    private final NotificationService notificationService;

    public ScheduleLearningController(ScheduleLearningService scheduleLearningService, ScheduleLearningMapper scheduleLearningMapper, UserService userService, CourseService courseService, NotificationService notificationService) {
        this.scheduleLearningService = scheduleLearningService;
        this.scheduleLearningMapper = scheduleLearningMapper;
        this.userService = userService;
        this.courseService = courseService;
        this.notificationService = notificationService;
    }

    @GetMapping("/fetch")
    public ResponseEntity<ScheduleResponse> fetchScheduleLearningById(@RequestParam Long id) {
        ScheduleLearning scheduleLearning = scheduleLearningService.fetchScheduleLearning(id);
        ScheduleLearningDto dto = scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, new ScheduleLearningDto());
        ScheduleResponse response = new ScheduleResponse();
        response.setScheduleLearning(dto);
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
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

        ScheduleResponse response = new ScheduleResponse();
        response.setScheduleLearningList(scheduleLearningDtoList);
        return  ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
    }

    @PostMapping("/create")
    public ResponseEntity<ScheduleResponse> createScheduleLearning(@RequestBody ScheduleLearningDto dto) {
        if (LocalDateTime.now().isBefore(dto.getTimeStart())){
            dto.setState(StateLesson.NOT_YET);
        }else if (LocalDateTime.now().isAfter(dto.getTimeEnd())){
            dto.setState(StateLesson.ABSENT);
        }else{
            dto.setState(StateLesson.PRESENT);
        }
        ScheduleLearning scheduleLearning = scheduleLearningService.createScheduleLearning(dto);

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setName(String.format("Buổi học %s chuẩn bị diễn ra", scheduleLearning.getName()));
        notificationDto.setTimeNoti(scheduleLearning.getTimeEnd().minusHours(30));
        notificationDto.setCategory(NotificationCategory.LESSON);
        notificationDto.setContent(String.format("Buổi học %s chuẩn bị diễ ra vào lúc %s tại %s", scheduleLearning.getName(), scheduleLearning.getTimeEnd(), scheduleLearning.getLearningAddresses()));
        notificationDto.setEntityId(scheduleLearning.getId());
        notificationDto.setEventTime(scheduleLearning.getTimeEnd());
        notificationDto.setState(StateNotification.UNREAD);
        notificationService.createNotification(notificationDto);


        scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, dto);
        ScheduleResponse response = new ScheduleResponse();
        response.setScheduleLearning(dto);
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateScheduleLearning(@RequestParam Long id, @RequestBody ScheduleLearningDto dto) {
        boolean isSuccess = scheduleLearningService.updateScheduleLearning(id, dto);
        if (isSuccess) {
            ScheduleResponse response = new ScheduleResponse();
            ScheduleLearning scheduleLearning = scheduleLearningService.fetchScheduleLearning(id);
            scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, dto);
            response.setScheduleLearning(dto);
            return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
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
        ScheduleResponse response  = new ScheduleResponse();
        response.setScheduleLearningList(scheduleLearningDtoList);
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
    }

    // API lấy danh sách các buổi học trong khoảng thời gian
    @GetMapping("/course/time")
    public ResponseEntity<Object> getSchedulesBetweenTimesAndCourse(
            @RequestParam("courseId") Long courseId,
            @RequestParam("startTime")  LocalDateTime startTime,
            @RequestParam("endTime")  LocalDateTime endTime) {
        Course course = courseService.findById(courseId);
        List<ScheduleLearningDto> scheduleLearningDtoList = new ArrayList<>();
        scheduleLearningService.getSchedulesBetweenTimes(course ,startTime, endTime).forEach(
                scheduleLearning -> {
                    scheduleLearningDtoList.add(scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, new ScheduleLearningDto()));
                }
        );
        ScheduleResponse response  = new ScheduleResponse();
        response.setScheduleLearningList(scheduleLearningDtoList);
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
    }

    @GetMapping("/user/time")
    public ResponseEntity<Object> getSchedulesBetweenTimesAndUser(
            @RequestParam("startTime")  LocalDateTime startTime,
            @RequestParam("endTime")  LocalDateTime endTime
    ){
        User user = userService.getAuthenticatedUser();
        List<ScheduleLearningDto> scheduleLearningDtoList = new ArrayList<>();
        scheduleLearningService.getSchedulesBetweenTimesAndUser(user ,startTime, endTime).forEach(
                scheduleLearning -> {
                    scheduleLearningDtoList.add(scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, new ScheduleLearningDto()));
                }
        );
        ScheduleResponse response  = new ScheduleResponse();
        response.setScheduleLearningList(scheduleLearningDtoList);
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
    }
}
