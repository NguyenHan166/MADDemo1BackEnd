package com.nguyenhan.maddemo1.controller;

import com.nguyenhan.maddemo1.constants.*;
import com.nguyenhan.maddemo1.dto.*;
import com.nguyenhan.maddemo1.mapper.CourseMapper;
import com.nguyenhan.maddemo1.mapper.ScheduleLearningMapper;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.Notification;
import com.nguyenhan.maddemo1.model.ScheduleLearning;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.responses.CourseResponse;
import com.nguyenhan.maddemo1.responses.ErrorResponseDto;
import com.nguyenhan.maddemo1.responses.ResponseDto;
import com.nguyenhan.maddemo1.service.CourseService;
import com.nguyenhan.maddemo1.service.NotificationService;
import com.nguyenhan.maddemo1.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RequestMapping("api/courses")
@RestController
@Validated
@Slf4j
public class CourseController {

    private final UserService userService;
    private final CourseService courseService;
    private final CourseMapper courseMapper;
    private final ScheduleLearningMapper scheduleLearningMapper;
    private final NotificationService notificationService;

    public CourseController(CourseService courseService, UserService userService, CourseMapper courseMapper, ScheduleLearningMapper scheduleLearningMapper, NotificationService notificationService
    ) {
        this.courseService = courseService;
        this.userService = userService;
        this.courseMapper = courseMapper;
        this.scheduleLearningMapper = scheduleLearningMapper;
        this.notificationService = notificationService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createCourse(@Valid @RequestBody CourseInputDto courseDto) {
        Course course = new Course();
        if (RepeatTypeCourse.WEEKLY.equals(courseDto.getRepeatType())) {
            course = everyWeekSolutions(courseDto);
        } else if (RepeatTypeCourse.DAILY.equals(courseDto.getRepeatType())) {
            course = everyDaySolutions(courseDto);
        } else if (RepeatTypeCourse.MONTHLY.equals(courseDto.getRepeatType())) {
            course = everyMonthSolutions(courseDto);
        } else if (RepeatTypeCourse.NONE.equals(courseDto.getRepeatType())) {
            course = noneTypeSolutions(courseDto);
        } else {
            return ResponseEntity.status(ResponseConstants.STATUS_400).body(new ErrorResponseDto(
                    "api/courses/create",
                    ResponseConstants.STATUS_400,
                    ResponseConstants.MESSAGE_400,
                    LocalDateTime.now()
            ));
        }

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setName(String.format("Khóa học %s chuẩn bị kết thúc", course.getName()));
        notificationDto.setTimeNoti(course.getTimeEnd().atStartOfDay().minusDays(1));
        notificationDto.setCategory(NotificationCategory.COURSE);
        notificationDto.setContent(String.format("Khóa học %s chuẩn bị kết thúc vào lúc %s", course.getName(), course.getTimeEnd()));
        notificationDto.setEntityId(course.getId());
        notificationDto.setEventTime(course.getTimeEnd().atStartOfDay());
        notificationDto.setState(StateNotification.UNREAD);

        notificationService.createNotification(notificationDto);

        createNotificationForLesson(course.getScheduleLearnings());

        CourseResponse response = new CourseResponse();

        response.setCourse(courseMapper.mapToCourseDto(course, new CourseDetailsDto()));
        return ResponseEntity.status(ResponseConstants.STATUS_201).body(response);
    }

    private Course noneTypeSolutions(@Valid CourseInputDto courseDto) {
        log.atInfo().log(courseDto.toString());
        int countLesson = 0;
        LocalDateTime endDateTime = courseDto.getTimeEnd().atStartOfDay().withHour(courseDto.getEndLessonTime().getHour());
        List<String> listDays = Arrays.asList(courseDto.getListDay().split(",")); // "27/04/2025,27/04/2025,27/04/2025"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<LocalDate> days = new ArrayList<>();
        for (String s : listDays) {
            LocalDate date = LocalDate.parse(s.trim(), formatter);
            days.add(date);
        }

        List<ScheduleLearning> scheduleLearnings = new ArrayList<>();
        for (LocalDate date : days) {
            LocalDateTime start = date.atStartOfDay().withHour(courseDto.getStartLessonTime().getHour()).withMinute(courseDto.getStartLessonTime().getMinute());
            ScheduleLearningDto scheduleLearningDto = new ScheduleLearningDto();
            scheduleLearningDto.setTimeStart(start);
            scheduleLearningDto.setTimeEnd(start.withHour(endDateTime.getHour()).withMinute(endDateTime.getMinute()));
            scheduleLearningDto.setTeacher(courseDto.getTeacher());
            scheduleLearningDto.setLearningAddresses(courseDto.getAddressLearning());
            countLesson++;
            scheduleLearningDto.setName("Buổi số " + countLesson);
            if (LocalDateTime.now().isBefore(start)) {
                scheduleLearningDto.setState(StateLesson.NOT_YET);
            } else if (LocalDateTime.now().isAfter(scheduleLearningDto.getTimeEnd())) {
                scheduleLearningDto.setState(StateLesson.ABSENT);
            } else {
                scheduleLearningDto.setState(StateLesson.PRESENT);
            }
            scheduleLearnings.add(scheduleLearningMapper.mapToScheduleLearning(scheduleLearningDto, new ScheduleLearning()));
        }

        Course course = courseService.createCourse(courseDto);
        setStateCourse(courseDto, course);
        course.setNumberOfLessons(Integer.toString(scheduleLearnings.size()));
        scheduleLearnings.forEach(
                scheduleLearning -> {
                    scheduleLearning.setCourse(course);
                }
        );
        course.setScheduleLearnings(scheduleLearnings);
        return courseService.save(course);
    }

    private Course everyMonthSolutions(CourseInputDto courseDto) {
        log.atInfo().log(courseDto.toString());
        int countLesson = 0;
        LocalDateTime endDateTime = courseDto.getTimeEnd().atStartOfDay().withHour(courseDto.getEndLessonTime().getHour());
        List<String> listDays = Arrays.asList(courseDto.getListDay().split(",")); // "27/04/2025,27/04/2025,27/04/2025"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<LocalDate> days = new ArrayList<>();
        for (String s : listDays) {
            LocalDate date = LocalDate.parse(s.trim(), formatter);
            days.add(date);
        }
        List<ScheduleLearning> scheduleLearnings = new ArrayList<>();
        for (LocalDate date : days) {
            LocalDateTime start = date.atStartOfDay().withHour(courseDto.getStartLessonTime().getHour()).withMinute(courseDto.getStartLessonTime().getMinute());
            while (start.isBefore(endDateTime)) {
                ScheduleLearningDto scheduleLearningDto = new ScheduleLearningDto();
                scheduleLearningDto.setTimeStart(start);
                scheduleLearningDto.setTimeEnd(start.withHour(endDateTime.getHour()).withMinute(endDateTime.getMinute()));
                scheduleLearningDto.setTeacher(courseDto.getTeacher());
                scheduleLearningDto.setLearningAddresses(courseDto.getAddressLearning());
                countLesson++;
                scheduleLearningDto.setName("Buổi số " + countLesson);
                if (LocalDateTime.now().isBefore(start)) {
                    scheduleLearningDto.setState(StateLesson.NOT_YET);
                } else if (LocalDateTime.now().isAfter(scheduleLearningDto.getTimeEnd())) {
                    scheduleLearningDto.setState(StateLesson.ABSENT);
                } else {
                    scheduleLearningDto.setState(StateLesson.PRESENT);
                }
                scheduleLearnings.add(scheduleLearningMapper.mapToScheduleLearning(scheduleLearningDto, new ScheduleLearning()));

                start = start.plusMonths(1);
            }
        }

        Course course = courseService.createCourse(courseDto);
        setStateCourse(courseDto, course);
        course.setNumberOfLessons(Integer.toString(scheduleLearnings.size()));
        scheduleLearnings.forEach(
                scheduleLearning -> {
                    scheduleLearning.setCourse(course);
                }
        );
        course.setScheduleLearnings(scheduleLearnings);
        return courseService.save(course);
    }

    private Course everyDaySolutions(CourseInputDto courseDto) {
        log.atInfo().log(courseDto.toString());
        int countLesson = 0;
        LocalDateTime startDateTime = courseDto.getTimeStart().atStartOfDay().withHour(courseDto.getStartLessonTime().getHour());
        LocalDateTime endDateTime = courseDto.getTimeEnd().atStartOfDay().withHour(courseDto.getEndLessonTime().getHour());

        List<ScheduleLearning> scheduleLearnings = new ArrayList<>();
        while (startDateTime.isBefore(endDateTime)) {
            ScheduleLearningDto scheduleLearningDto = new ScheduleLearningDto();
            scheduleLearningDto.setTimeStart(startDateTime);
            scheduleLearningDto.setTimeEnd(startDateTime.withHour(endDateTime.getHour()).withMinute(endDateTime.getMinute()));
            scheduleLearningDto.setTeacher(courseDto.getTeacher());
            scheduleLearningDto.setLearningAddresses(courseDto.getAddressLearning());
            countLesson++;
            scheduleLearningDto.setName("Buổi số " + countLesson);
            if (LocalDateTime.now().isBefore(startDateTime)) {
                scheduleLearningDto.setState(StateLesson.NOT_YET);
            } else if (LocalDateTime.now().isAfter(scheduleLearningDto.getTimeEnd())) {
                scheduleLearningDto.setState(StateLesson.ABSENT);
            } else {
                scheduleLearningDto.setState(StateLesson.PRESENT);
            }
            scheduleLearnings.add(scheduleLearningMapper.mapToScheduleLearning(scheduleLearningDto, new ScheduleLearning()));

            startDateTime = startDateTime.plusDays(1);
        }

        Course course = courseService.createCourse(courseDto);
        setStateCourse(courseDto, course);
        course.setNumberOfLessons(Integer.toString(scheduleLearnings.size()));
        scheduleLearnings.forEach(
                scheduleLearning -> {
                    scheduleLearning.setCourse(course);
                }
        );
        course.setScheduleLearnings(scheduleLearnings);
        return courseService.save(course);
    }

    private Course everyWeekSolutions(CourseInputDto courseDto) {
        List<String> days = Arrays.asList(courseDto.getListDay().split(","));
        log.atInfo().log(courseDto.toString());
        int countLesson = 0;

        LocalDateTime startDateTime = courseDto.getTimeStart().atStartOfDay().withHour(courseDto.getStartLessonTime().getHour());
        LocalDateTime endDateTime = courseDto.getTimeEnd().atStartOfDay().withHour(courseDto.getEndLessonTime().getHour());

        List<ScheduleLearning> scheduleLearnings = new ArrayList<>();
        for (String day : days) {
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
            LocalDateTime lessonStartTime = getNextDateForDayOfWeek(startDateTime, dayOfWeek);
            LocalDateTime lessonEndTime = lessonStartTime.withHour(endDateTime.getHour()).withMinute(endDateTime.getMinute());

            while (lessonStartTime.isBefore(endDateTime)) {
                ScheduleLearningDto scheduleLearningDto = new ScheduleLearningDto();
                scheduleLearningDto.setTimeStart(lessonStartTime);
                scheduleLearningDto.setTimeEnd(lessonEndTime);
                scheduleLearningDto.setTeacher(courseDto.getTeacher());
                scheduleLearningDto.setLearningAddresses(courseDto.getAddressLearning());
                countLesson++;
                scheduleLearningDto.setName("Buổi học " + countLesson);
                if (LocalDateTime.now().isBefore(lessonStartTime)) {
                    scheduleLearningDto.setState(StateLesson.NOT_YET);
                } else if (LocalDateTime.now().isAfter(lessonEndTime)) {
                    scheduleLearningDto.setState(StateLesson.ABSENT);
                } else {
                    scheduleLearningDto.setState(StateLesson.PRESENT);
                }
                scheduleLearnings.add(scheduleLearningMapper.mapToScheduleLearning(scheduleLearningDto, new ScheduleLearning()));


                lessonStartTime = lessonStartTime.plusWeeks(1);
                lessonEndTime = lessonEndTime.plusWeeks(1);
            }
        }
        Course course = courseService.createCourse(courseDto);
        setStateCourse(courseDto, course);
        course.setNumberOfLessons(Integer.toString(scheduleLearnings.size()));
        scheduleLearnings.forEach(
                scheduleLearning -> {
                    scheduleLearning.setCourse(course);
                }
        );
        course.setScheduleLearnings(scheduleLearnings);
        return courseService.save(course);
    }

    private LocalDateTime getNextDateForDayOfWeek(LocalDateTime startDateTime, DayOfWeek dayOfWeek) {
        LocalDateTime nextDay = startDateTime;
        // Tìm ngày tiếp theo trùng với dayOfWeek
        while (nextDay.getDayOfWeek() != dayOfWeek) {
            nextDay = nextDay.plusDays(1);
        }
        return nextDay;
    }

    private void createNotificationForLesson(List<ScheduleLearning> scheduleLearnings) {
        List<NotificationDto> notificationDtos = new ArrayList<>();
        scheduleLearnings.forEach(
                scheduleLearning -> {
                    NotificationDto notificationDto = new NotificationDto();
                    notificationDto.setName(String.format("Buổi học %s chuẩn bị diễn ra", scheduleLearning.getName()));
                    notificationDto.setTimeNoti(scheduleLearning.getTimeStart().minusMinutes(30));
                    notificationDto.setCategory(NotificationCategory.LESSON);
                    notificationDto.setContent(String.format("Buổi học %s chuẩn bị diễn ra vào lúc %s tại %s", scheduleLearning.getName(), scheduleLearning.getTimeStart(), scheduleLearning.getLearningAddresses()));
                    notificationDto.setEntityId(scheduleLearning.getId());
                    notificationDto.setEventTime(scheduleLearning.getTimeStart());
                    notificationDto.setState(StateNotification.UNREAD);
                    notificationDtos.add(notificationDto);
                }
        );

        notificationService.createNotifications(notificationDtos);
    }

    private void setStateCourse(CourseInputDto courseDto, Course course) {
        if (LocalDateTime.now().isBefore(courseDto.getTimeStart().atStartOfDay())) {
            course.setState(StateCourse.NOT_YET);
        } else if (LocalDateTime.now().isAfter(courseDto.getTimeEnd().atStartOfDay())) {
            course.setState(StateCourse.END);
        } else {
            course.setState(StateCourse.ONGOING);
        }
    }

    @GetMapping("/")
    public ResponseEntity<Object> getAllCourses() {
        User findUser = userService.getAuthenticatedUser();

        List<CourseListOutputDto> courseDtosList = new ArrayList<>();
        courseService.getCoursesByUser(findUser).forEach(
                course -> {
                    CourseListOutputDto courseDto = courseMapper.mapToCourseListOutputDto(course, new CourseListOutputDto());
                    courseDtosList.add(courseDto);
                }
        );
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(new CourseResponse(courseDtosList, null));
    }

    @GetMapping("/fetch")
    public ResponseEntity<Object> getCourse(
            @Parameter(description = "Id của course cần xem", required = true)
            @RequestParam Long courseId) {
        Course course = courseService.findById(courseId);
        CourseDetailsDto courseDto = courseMapper.mapToCourseDto(course, new CourseDetailsDto());
        CourseResponse response = new CourseResponse();
        response.setCourse(courseDto);
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateCourse(
            @Parameter(description = "ID của course cần update", required = true)
            @RequestParam Long courseId, @Valid @RequestBody CourseInputDto courseDto) {
        boolean isUpdate = courseService.updateCourse(courseId, courseDto);
        if (isUpdate) {
            Course course = courseService.findById(courseId);
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
    }


    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteCourse(
            @Parameter(description = "ID của course cần xóa", required = true)
            @RequestParam Long courseId) {
        boolean isDelete = courseService.deleteCourse(courseId);
        if (isDelete) {
            return ResponseEntity.status(ResponseConstants.STATUS_200).body(new ResponseDto(ResponseConstants.STATUS_200, ResponseConstants.MESSAGE_200));
        } else {
            return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ErrorResponseDto(
                    "api/course/delete",
                    ResponseConstants.STATUS_417,
                    ResponseConstants.MESSAGE_417_DELETE,
                    LocalDateTime.now()
            ));
        }
    }


    // API lấy các khóa học có trạng thái "Đang diễn ra"
    @GetMapping("/state")
    public ResponseEntity<Object> getCoursesByState(
            @RequestParam String state) {
        User user = userService.getAuthenticatedUser();
        List<CourseListOutputDto> courseDtosList = new ArrayList<>();
        courseService.getCoursesByState(user, StateCourse.valueOf(state)).forEach(
                course -> {
                    CourseListOutputDto courseDto = courseMapper.mapToCourseListOutputDto(course, new CourseListOutputDto());
                    courseDtosList.add(courseDto);
                }
        );
        CourseResponse response = new CourseResponse();
        response.setCourses(courseDtosList);
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
    }

    // API lấy các khóa học trong khoảng thời gian cho trước
    @GetMapping("/time")
    public ResponseEntity<Object> getCoursesBetweenTimes(
            @RequestParam("startTime") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startTime,
            @RequestParam("endTime") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endTime) {
        User user = userService.getAuthenticatedUser();
        List<CourseListOutputDto> courseDtosList = new ArrayList<>();
        courseService.getCoursesBetweenTimes(user, startTime, endTime).forEach(
                course -> {
                    CourseListOutputDto courseDto = courseMapper.mapToCourseListOutputDto(course, new CourseListOutputDto());
                    courseDtosList.add(courseDto);
                }
        );
        CourseResponse response = new CourseResponse();
        response.setCourses(courseDtosList);
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
    }
}
