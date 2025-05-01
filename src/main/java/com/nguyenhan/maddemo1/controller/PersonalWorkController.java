package com.nguyenhan.maddemo1.controller;
import com.nguyenhan.maddemo1.constants.NotificationCategory;
import com.nguyenhan.maddemo1.constants.ResponseConstants;
import com.nguyenhan.maddemo1.constants.StateNotification;
import com.nguyenhan.maddemo1.dto.NotificationDto;
import com.nguyenhan.maddemo1.dto.PersonalWorkDto;
import com.nguyenhan.maddemo1.dto.PersonalWorkUpdateDto;
import com.nguyenhan.maddemo1.mapper.CourseMapper;
import com.nguyenhan.maddemo1.mapper.PersonalWorkMapper;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.PersonalWork;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.PersonalWorkRepository;
import com.nguyenhan.maddemo1.responses.ErrorResponseDto;
import com.nguyenhan.maddemo1.responses.PersonalWorkResponse;
import com.nguyenhan.maddemo1.responses.ResponseDto;
import com.nguyenhan.maddemo1.service.CourseService;
import com.nguyenhan.maddemo1.service.NotificationService;
import com.nguyenhan.maddemo1.service.PersonalWorkService;
import com.nguyenhan.maddemo1.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping("api/personalwork")
@RestController
public class PersonalWorkController {

    private final UserService userService;
    private final PersonalWorkService personalWorkService;
    private final PersonalWorkMapper personalWorkMapper;
    private final NotificationService notificationService;

    public PersonalWorkController(UserService userService, PersonalWorkService personalWorkService, PersonalWorkMapper personalWorkMapper, NotificationService notificationService) {
        this.userService = userService;
        this.personalWorkService = personalWorkService;
        this.personalWorkMapper = personalWorkMapper;
        this.notificationService = notificationService;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createPersonalWork(@Valid @RequestBody PersonalWorkDto request) {

        log.info("Controller create");
        User user = userService.getAuthenticatedUser();
        request.setUserId(user.getId());
        log.info("Controller email: {}", user.getEmail());

        PersonalWork personalWork = personalWorkService.createPersonalWork(request);

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setName(String.format("Công việc %s chuẩn bị kết thúc", personalWork.getName()));
        notificationDto.setTimeNoti(personalWork.getTimeEnd().minusDays(1));
        notificationDto.setCategory(NotificationCategory.PERSONAL_WORK);
        notificationDto.setContent(String.format("Công việc %s chuẩn bị kết thúc vào lúc %s tại %s", personalWork.getName(), personalWork.getTimeEnd(), personalWork.getWorkAddress()));
        notificationDto.setEntityId(personalWork.getId());
        notificationDto.setEventTime(personalWork.getTimeEnd());
        notificationDto.setState(StateNotification.UNREAD);

        notificationService.createNotification(notificationDto);

        request = personalWorkMapper.toPersonalWorkDto(personalWork);
        PersonalWorkResponse response = new PersonalWorkResponse();
        response.setPersonalWork(request);
        return ResponseEntity.status(ResponseConstants.STATUS_201).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updatePersonalWork(@Valid @RequestBody PersonalWorkUpdateDto request, @RequestParam Long id){
        PersonalWork personalWork = personalWorkService.updatePersonalWork(request, id);
        PersonalWorkDto personalWorkDto = personalWorkMapper.toPersonalWorkDto(personalWork);
        PersonalWorkResponse response = new PersonalWorkResponse();
        response.setPersonalWork(personalWorkDto);
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deletePersonalWork(@Valid @RequestParam Long id){
        boolean isSuccess = personalWorkService.deletePersonalWork(id);
        if (isSuccess){
            return ResponseEntity.status(ResponseConstants.STATUS_200).body(new ResponseDto(ResponseConstants.STATUS_200 , ResponseConstants.MESSAGE_200));
        }else{
            return ResponseEntity.status(ResponseConstants.STATUS_417).body(new ErrorResponseDto(
                    "api/personalwork/delete",
                    ResponseConstants.STATUS_417,
                    ResponseConstants.MESSAGE_417_DELETE,
                    LocalDateTime.now()
            ));
        }

    }

    @GetMapping("/getByUser")
    public ResponseEntity<Object> getByUser(){
        List<PersonalWork> personalWorks = personalWorkService.getPersonalWorksByUserId();
        List<PersonalWorkDto> personalWorkDtos = new ArrayList<>();
        personalWorks.forEach(personalWork -> {
            PersonalWorkDto dto = personalWorkMapper.toPersonalWorkDto(personalWork);
            personalWorkDtos.add(dto);
        });
        PersonalWorkResponse response = new PersonalWorkResponse();
        response.setPersonalWorks(personalWorkDtos);
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(response);
    }

//    @GetMapping("/getByCourseId/{courseId}")
//    public ResponseEntity<List<PersonalWork>> getByCourseId(@PathVariable Long courseId){
//        return ResponseEntity.status(ResponseConstants.STATUS_200).body(personalWorkService.getPersonalWorksByCourseId(courseId));
//    }

//    @GetMapping("/")
//    public ResponseEntity<List<CourseDto>> getAllCourses(@RequestParam Long userId) {
//        List<CourseDto> courseDtosList = new ArrayList<>();
//        courseService.listCoursesOfUser(userId).forEach(
//                course -> {
//                    CourseDto courseDto = courseMapper.mapToCourseDto(course, new CourseDto());
//                    courseDtosList.add(courseDto);
//                }
//        );
//        return ResponseEntity.status(ResponseConstants.STATUS_200).body(courseDtosList);
//    }
//
//    @GetMapping("/fetch")
//    public ResponseEntity<CourseDto> getCourse(@RequestParam Long courseId) {
//        Course course = courseService.findById(courseId);
//        CourseDto courseDto = courseMapper.mapToCourseDto(course, new CourseDto());
//        return ResponseEntity.status(ResponseConstants.STATUS_200).body(courseDto);
//    }
//
//    @PutMapping("/update")
//    public ResponseEntity<CourseDto> updateCourse(@RequestParam Long courseId ,@Valid @RequestBody CourseDto courseDto) {
//        boolean isUpdate = courseService.updateCourse(courseId, courseDto);
//        if (isUpdate) {
//            return ResponseEntity.status(ResponseConstants.STATUS_417).body(courseDto);
//        }else{
//            return ResponseEntity.status(ResponseConstants.STATUS_417).body(courseDto);
//        }
//    }
//
//    @DeleteMapping("/delete")
//    public ResponseEntity<ResponseDto> deleteCourse(@RequestParam Long courseId) {
//        boolean isDelete = courseService.deleteCourse(courseId);
//        if (isDelete) {
//            return ResponseEntity.status(ResponseConstants.STATUS_200).body(new ResponseDto(ResponseConstants.STATUS_200, ResponseConstants.MESSAGE_200));
//        }else{
//            return ResponseEntity.status(ResponseConstants.STATUS_400).body(new ResponseDto(ResponseConstants.STATUS_417, ResponseConstants.MESSAGE_417_DELETE));
//        }
//    }
}