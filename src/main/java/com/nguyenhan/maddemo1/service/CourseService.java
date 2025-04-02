package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.dto.CourseDto;
import com.nguyenhan.maddemo1.exception.ResourceAlreadyExistsException;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.mapper.CourseMapper;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.repository.CourseRepository;
import com.nguyenhan.maddemo1.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.courseMapper = courseMapper;
    }

    public List<Course> listCourses() {
        return courseRepository.findAll();
    }

    public List<Course> listCoursesOfUser(Long userID) {
        if (userRepository.findById(userID).isEmpty()) {
            throw new ResourceNotFoundException("User", "userID", userID.toString());
        }
        return courseRepository.findByUserId(userID);
    }

    public Course findById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", "id", id.toString()));
    }

    public Course createCourse(CourseDto courseDto) {
        if (courseRepository.findByName(courseDto.getName()).isPresent()) {
            throw new ResourceAlreadyExistsException("Course", "name", courseDto.getName());
        }

        Course course = new Course();
        log.atInfo().log("Creating new course " + course.getCreatedBy());
        courseMapper.mapToCourse(courseDto, course);
        return courseRepository.save(course);
    }

    public boolean updateCourse(Long id, CourseDto courseDto) {

        boolean isUpdate = false;
        Course course = courseRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Course", "id", id.toString())
        );

        courseMapper.mapToCourse(courseDto, course);
        courseRepository.save(course);
        isUpdate = true;
        return isUpdate;
    }

    public boolean deleteCourse(Long id) {
        boolean isDelete = false;
        Course course = courseRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Course", "id", id.toString())
        );
        courseRepository.deleteById(id);
        isDelete = true;
        return isDelete;
    }
}
