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
}
