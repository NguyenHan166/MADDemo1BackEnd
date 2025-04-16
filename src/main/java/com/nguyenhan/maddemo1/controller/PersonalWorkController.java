package com.nguyenhan.maddemo1.controller;

import com.nguyenhan.maddemo1.constants.ResponseConstants;
import com.nguyenhan.maddemo1.dto.PersonalWorkDto;
import com.nguyenhan.maddemo1.dto.PersonalWorkUpdateDto;
import com.nguyenhan.maddemo1.mapper.CourseMapper;
import com.nguyenhan.maddemo1.mapper.PersonalWorkMapper;
import com.nguyenhan.maddemo1.model.PersonalWork;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.PersonalWorkRepository;
import com.nguyenhan.maddemo1.service.CourseService;
import com.nguyenhan.maddemo1.service.PersonalWorkService;
import com.nguyenhan.maddemo1.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("api/personalwork")
@RestController
public class PersonalWorkController {

    private final PersonalWorkRepository personalWorkRepository;
    private final UserService userService;
    private final PersonalWorkService personalWorkService;
    private final PersonalWorkMapper personalWorkMapper;

    public PersonalWorkController(PersonalWorkRepository personalWorkRepository, CourseService courseService, UserService userService, CourseMapper courseMapper, UserService userService1, UserService userService2, PersonalWorkService personalWorkService, PersonalWorkMapper personalWorkMapper) {
        this.personalWorkRepository = personalWorkRepository;
        this.userService = userService;
        this.personalWorkService = personalWorkService;
        this.personalWorkMapper = personalWorkMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<PersonalWork> createPersonalWork(@Valid @RequestBody PersonalWorkDto request) {

        log.info("Controller create");
        User user = personalWorkService.getCurrentUser();
        request.setUserId(user.getId());
        log.info("Controller email: {}", user.getEmail());

        PersonalWork personalWork = personalWorkService.createPersonalWork(request);
        return ResponseEntity.status(ResponseConstants.STATUS_201).body(personalWork);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonalWork> updatePersonalWork(@Valid @RequestBody PersonalWorkUpdateDto request, @PathVariable Long id){
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(personalWorkService.updatePersonalWork(request, id));
    }

    @DeleteMapping("/{id}")
    public String deletePersonalWork(@Valid @PathVariable Long id){
        personalWorkService.deletePersonalWork(id);
        return "Personal Work has been deleted";
    }

    @GetMapping("/getByUserId")
    public ResponseEntity<List<PersonalWork>> getByUserId(){
        return ResponseEntity.status(ResponseConstants.STATUS_200).body(personalWorkService.getPersonalWorksByUserId());
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
