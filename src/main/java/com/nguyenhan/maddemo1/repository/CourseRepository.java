package com.nguyenhan.maddemo1.repository;

import com.nguyenhan.maddemo1.constants.StateCourse;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByUserId(Long userId);
    Optional<Course> findByName(String name);
    // Truy vấn lấy các khóa học của người dùng sắp xếp theo thời gian bắt đầu
    List<Course> findByUserOrderByTimeStartAsc(User user);
    // Truy vấn lấy các khóa học có trạng thái
    List<Course> findByUserAndState(User user , StateCourse state);
    List<Course> findByUserAndStateOrState(User user , StateCourse state , StateCourse course);
    // Truy vấn lấy các khóa học có thời gian bắt đầu và kết thúc nằm trong khoảng thời gian
    List<Course> findByUserAndTimeStartGreaterThanEqualAndTimeEndLessThanEqual(
            User user ,LocalDate startTime, LocalDate endTime);
}
