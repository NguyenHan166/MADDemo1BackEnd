package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.dto.CourseDto;
import com.nguyenhan.maddemo1.dto.PersonalWorkDto;
import com.nguyenhan.maddemo1.dto.PersonalWorkUpdateDto;
import com.nguyenhan.maddemo1.exception.ResourceAlreadyExistsException;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.mapper.CourseMapper;
import com.nguyenhan.maddemo1.mapper.PersonalWorkMapper;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.PersonalWork;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.CourseRepository;
import com.nguyenhan.maddemo1.repository.PersonalWorkRepository;
import com.nguyenhan.maddemo1.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.nguyenhan.maddemo1.config.PersonalWorkSecurity;

import java.util.List;


@Service
@Slf4j
public class PersonalWorkService {

    private final PersonalWorkRepository personalWorkRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PersonalWorkMapper personalWorkMapper;

    public PersonalWorkService(PersonalWorkRepository personalWorkRepository, CourseRepository courseRepository, UserRepository userRepository, CourseMapper courseMapper, PersonalWorkMapper personalWorkMapper, PersonalWorkSecurity personalWorkSecurity) {
        this.personalWorkRepository = personalWorkRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.personalWorkMapper = personalWorkMapper;
    }


    public PersonalWork createPersonalWork(PersonalWorkDto request) {
//        if (personalWorkRepository.findByName(request.getName()).isPresent()) {
//            throw new ResourceAlreadyExistsException("Event", "name", request.getName());
//        }

        PersonalWork personalWork = personalWorkMapper.toPersonalWork(request);
        return personalWorkRepository.save(personalWork);
    }

    @Transactional
    @PreAuthorize("@personalWorkSecurity.isOwner(#id)")
    public PersonalWork updatePersonalWork(PersonalWorkUpdateDto request, Long id){
        log.info("Id personalwork update: {}", id);
        PersonalWork personalWork = personalWorkRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("PersonalWork", "id", id.toString())
        );

        log.info("Ten per:{}", personalWork.getName());

        personalWorkMapper.updatePersonalWorkMapper(personalWork, request);
        log.info("Ten per:{}", personalWork.getName());
        return personalWorkRepository.save(personalWork);
    }

    @PreAuthorize("@personalWorkSecurity.isOwner(#id)")
    public void deletePersonalWork(Long id){
        PersonalWork personalWork = personalWorkRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("PersonalWork", "id", id.toString())
        );

        personalWorkRepository.deleteById(id);
        log.info("PersonalWork with ID {} deleted successfully.", id);
    }

    public List<PersonalWork> getPersonalWorksByUserId(){
        User user = getCurrentUser();

        return personalWorkRepository.findByUserId(user.getId());
    }

//    public List<PersonalWork> getPersonalWorksByCourseId(Long courseId){
//        return personalWorkRepository.findByCourseId(courseId);
//    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("PersonalWork Service: principal: {}", principal);

        if (principal instanceof User) {
            return (User) principal;
        } else if (principal instanceof String) {
            // Trường hợp khi principal là email (chuỗi)
            String username = (String) principal;
            return userRepository.findByEmail(username).orElseThrow(() ->
                    new UsernameNotFoundException("User not found with email: " + username));
        }

        return null; // hoặc throw exception nếu không tìm thấy
    }



//    public boolean updateCourse(Long id, CourseDto courseDto) {
//
//        boolean isUpdate = false;
//        Course course = courseRepository.findById(id).orElseThrow(
//                () -> new ResourceNotFoundException("Course", "id", id.toString())
//        );
//
//        courseMapper.mapToCourse(courseDto, course);
//        courseRepository.save(course);
//        isUpdate = true;
//        return isUpdate;
//    }
//
//    public boolean deleteCourse(Long id) {
//        boolean isDelete = false;
//        Course course = courseRepository.findById(id).orElseThrow(
//                () -> new ResourceNotFoundException("Course", "id", id.toString())
//        );
//        courseRepository.deleteById(id);
//        isDelete = true;
//        return isDelete;
//    }
}
