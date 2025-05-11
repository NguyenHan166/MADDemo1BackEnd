package com.nguyenhan.maddemo1.controller;

import com.nguyenhan.maddemo1.constants.*;
import com.nguyenhan.maddemo1.dto.*;
import com.nguyenhan.maddemo1.mapper.*;
import com.nguyenhan.maddemo1.model.*;
import com.nguyenhan.maddemo1.responses.*;
import com.nguyenhan.maddemo1.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/api/users")
@RestController
@Slf4j
public class UserController {
    private final UserService userService;
    private final UsersMapper usersMapper;
    private final CourseService courseService;
    private final CourseMapper courseMapper;
    private final ScheduleLearningService scheduleLearningService;
    private final ScheduleLearningMapper scheduleLearningMapper;
    private final AssignmentService assignmentService;
    private final AssignmentMapper assignmentMapper;
    private final PersonalWorkService personalWorkService;
    private final PersonalWorkMapper personalWorkMapper;
    private final AlarmService alarmService;
    private final AlarmMapper alarmMapper;
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    public UserController(UserService userService, UsersMapper usersMapper, CourseService courseService, CourseMapper courseMapper, ScheduleLearningService scheduleLearningService, ScheduleLearningMapper scheduleLearningMapper, AssignmentService assignmentService, AssignmentMapper assignmentMapper, PersonalWorkService personalWorkService, PersonalWorkMapper personalWorkMapper, AlarmService alarmService, AlarmMapper alarmMapper, NotificationService notificationService, NotificationMapper notificationMapper) {
        this.userService = userService;
        this.usersMapper = usersMapper;
        this.courseService = courseService;
        this.courseMapper = courseMapper;
        this.scheduleLearningService = scheduleLearningService;
        this.scheduleLearningMapper = scheduleLearningMapper;
        this.assignmentService = assignmentService;
        this.assignmentMapper = assignmentMapper;
        this.personalWorkService = personalWorkService;
        this.personalWorkMapper = personalWorkMapper;
        this.alarmService = alarmService;
        this.alarmMapper = alarmMapper;
        this.notificationService = notificationService;
        this.notificationMapper = notificationMapper;
    }


    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentUserDto() {
        log.atInfo().log("getCurrentUserDto(): test user info start");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        UserDto userDto = usersMapper.mapToUserDto(currentUser, new UserDto());
        log.atInfo().log("current user: " + userDto);
        log.atInfo().log("getCurrentUserDto(): test user dto end");
        return ResponseEntity.status(UsersConstants.STATUS_200).body(userDto);
    }

    @GetMapping("/checkUser")
    public ResponseEntity<User> authenticatedUser() {
        User currentUser = userService.getAuthenticatedUser();
        return ResponseEntity.ok(currentUser);
    }


    @GetMapping("/")
    public ResponseEntity<List<User>> allUsers() {
        List<User> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }


    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateUser(@RequestBody UserDto userDto) {
        boolean isUpdated = userService.updateUser(userDto);
        if (isUpdated) {
            return ResponseEntity.ok(new ResponseDto(UsersConstants.STATUS_200, UsersConstants.MESSAGE_200));
        } else {
            return ResponseEntity.ok(new ResponseDto(UsersConstants.STATUS_417, UsersConstants.MESSAGE_417_UPDATE));
        }
    }


    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteUser(@RequestParam
                                                  @Email(message = "Email address should be a valid value")
                                                  @NotEmpty(message = "Email not be empty!")
                                                  String email) {
        boolean isDeleted = userService.deleteUser(email);
        if (isDeleted) {
            return ResponseEntity.ok(new ResponseDto(UsersConstants.STATUS_200, UsersConstants.MESSAGE_200));
        } else {
            return ResponseEntity.ok(new ResponseDto(UsersConstants.STATUS_417, UsersConstants.MESSAGE_417_DELETE));
        }
    }

    @PutMapping("update/eventState")
    public ResponseEntity<Object> updateState(@RequestParam Long eventId, @RequestParam String type, @RequestParam String state) {
        if (type.equals(NotificationCategory.COURSE.toString())) {
            Course course = courseService.findById(eventId);
            course.setState(StateCourse.valueOf(state));
            CourseInputDto courseInputDto = courseMapper.mapToCourseInputDto(course, new CourseInputDto());

            boolean isUpdated = courseService.updateCourse(eventId, courseInputDto);

            if (isUpdated) {
                CourseResponse courseResponse = new CourseResponse();
                courseResponse.setCourse(courseMapper.mapToCourseDto(course, new CourseDetailsDto()));
                return ResponseEntity.status(ResponseConstants.STATUS_200).body(courseResponse);
            } else {
                return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ErrorResponseDto(
                        "api/course/update",
                        ResponseConstants.STATUS_417,
                        ResponseConstants.MESSAGE_417_UPDATE,
                        LocalDateTime.now()
                ));
            }

        } else if (type.equals(NotificationCategory.LESSON.toString())) {
            ScheduleLearning scheduleLearning = scheduleLearningService.findById(eventId);
            scheduleLearning.setState(StateLesson.valueOf(state));
            ScheduleLearningDto scheduleLearningDto = scheduleLearningMapper.mapToScheduleLearningDto(scheduleLearning, new ScheduleLearningDto());
            boolean isSuccess = scheduleLearningService.updateScheduleLearning(eventId, scheduleLearningDto);
            if (isSuccess) {
                ScheduleResponse response = new ScheduleResponse();
                response.setScheduleLearning(scheduleLearningDto);
                return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
            } else {
                return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ResponseDto(ResponseConstants.STATUS_417, ResponseConstants.MESSAGE_417_UPDATE));
            }
        } else if(type.equals(NotificationCategory.ASSIGNMENT.toString())) {
            Assignment assignment = assignmentService.findById(eventId);
            assignment.setState(StateAssignment.valueOf(state));
            AssignmentDto assignmentDto = assignmentMapper.mapToAssignmentDto(assignment, new AssignmentDto());

            boolean success = assignmentService.update(eventId, assignmentDto);
            if (success) {
                assignmentMapper.mapToAssignmentDto(assignment, assignmentDto);
                AssignmentResponse response = new AssignmentResponse();
                response.setAssignment(assignmentDto);
                return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
            }else{
                return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ErrorResponseDto(
                        "/api/users/update/assignment",
                        ResponseConstants.STATUS_417,
                        ResponseConstants.MESSAGE_417_UPDATE,
                        LocalDateTime.now()
                ));
            }
        } else if (type.equals(NotificationCategory.PERSONAL_WORK.toString())) {
            PersonalWork personalWork = personalWorkService.findById(eventId);
            personalWork.setState(StateAssignment.valueOf(state));
            PersonalWorkUpdateDto personalWorkUpdateDto = new PersonalWorkUpdateDto();
            personalWorkMapper.updatePersonalWorkMapper(personalWork, personalWorkUpdateDto);
            PersonalWork personalWorkUpdated = personalWorkService.updatePersonalWork(personalWorkUpdateDto, eventId);
            PersonalWorkDto personalWorkDto = personalWorkMapper.toPersonalWorkDto(personalWorkUpdated);
            PersonalWorkResponse response = new PersonalWorkResponse();
            response.setPersonalWork(personalWorkDto);
            return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
        } else if (type.equals(NotificationCategory.ALARM.toString())){
            Alarm alarm = alarmService.findById(eventId);
            alarm.setState(AlarmState.valueOf(state));
            AlarmDto alarmDto = alarmMapper.mapToAlarmDto(alarm, new AlarmDto());
            boolean isSuccess = alarmService.updateAlarm(eventId, alarmDto);
            if (isSuccess) {
                AlarmResponse response = new AlarmResponse();
                response.setAlarm(alarmDto);
                return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
            }else{
                return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ErrorResponseDto(
                        "api/users/update/alarm",
                        ResponseConstants.STATUS_417,
                        ResponseConstants.MESSAGE_417_UPDATE,
                        LocalDateTime.now()
                ));
            }
        } else if (type.equals("NOTIFICATION")) {
            Notification notification = notificationService.getNotificationById(eventId);
            notification.setState(StateNotification.valueOf(state));
            notification.setReadTime(LocalDateTime.now());
            NotificationDto notificationDto = notificationMapper.mapToNotificationDto(notification, new NotificationDto());
            boolean isSuccess = notificationService.updateNotification(eventId, notificationDto);
            if(isSuccess){
                return ResponseEntity.status(ResponseConstants.STATUS_200).body(notificationDto);
            }else{
                return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ErrorResponseDto(
                        "api/users/update/notification",
                        ResponseConstants.STATUS_417,
                        ResponseConstants.MESSAGE_417_UPDATE,
                        LocalDateTime.now()
                ));
            }
        } else {
            return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ErrorResponseDto(
                    "api/users/update",
                    ResponseConstants.STATUS_417,
                    ResponseConstants.MESSAGE_417_UPDATE,
                    LocalDateTime.now()
            ));
        }

    }

}