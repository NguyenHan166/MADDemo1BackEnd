package com.nguyenhan.maddemo1.service;

import com.nguyenhan.maddemo1.dto.CourseDto;
import com.nguyenhan.maddemo1.exception.ResourceAlreadyExistsException;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.mapper.CourseMapper;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.CourseRepository;
import com.nguyenhan.maddemo1.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;
    private final UserService userService;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository, CourseMapper courseMapper, UserService userService) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.courseMapper = courseMapper;
        this.userService = userService;
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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        for (Course course : user.getCourses()) {
            if (course.getName().equals(courseDto.getName())) {
                throw new ResourceAlreadyExistsException("Course", "name", courseDto.getName());
            }
            if (course.getTimeStart().equals(courseDto.getTimeStart()) && course.getTimeEnd().equals(courseDto.getTimeEnd())) {
                throw new ResourceAlreadyExistsException("Course", "Course Time", course.getTimeStart().toString());
            }
        }

        Course course = new Course();
//        log.atInfo().log("Creating new course " + course.getCreatedBy());
        courseMapper.mapToCourse(courseDto, course);
        return courseRepository.save(course);
    }

    public boolean updateCourse(Long id, CourseDto courseDto) {

        boolean isUpdate = false;
        Course course = courseRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Course", "id", id.toString())
        );

        User user = userService.getAuthenticatedUser();

        boolean flag = false;
        for (Course c : user.getCourses()) {
            if (c.getId().equals(course.getId())) {
                flag = true;
                break;
            }
        }
        if (!flag) throw new ResourceNotFoundException("Course", "id", course.getId().toString());

        courseMapper.mapToCourse(courseDto, course);
        courseRepository.save(course);
        isUpdate = true;
        return isUpdate;
    }

    // Lấy các khóa học của người dùng sắp xếp theo thời gian bắt đầu
    public List<Course> getCoursesByUser(User user) {
        return courseRepository.findByUserOrderByTimeStartAsc(user);
    }

    // Lấy các khóa học có trạng thái
    public List<Course> getCoursesByState(User user ,String state) {
        return courseRepository.findByUserAndState(user ,state);
    }

    // Lấy các khóa học trong khoảng thời gian cho trước
    public List<Course> getCoursesBetweenTimes(LocalDateTime startTime, LocalDateTime endTime) {
        return courseRepository.findByTimeStartGreaterThanEqualAndTimeEndLessThanEqual(startTime, endTime);
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
