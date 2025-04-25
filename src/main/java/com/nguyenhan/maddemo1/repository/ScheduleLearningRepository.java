package com.nguyenhan.maddemo1.repository;

import com.nguyenhan.maddemo1.constants.StateLesson;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.ScheduleLearning;
import com.nguyenhan.maddemo1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleLearningRepository extends JpaRepository<ScheduleLearning, Long> {

    List<ScheduleLearning> findByUser(User user);

    Long user(User user);
    List<ScheduleLearning> findByCourseOrderByTimeStartAsc(Course course);
    List<ScheduleLearning> findByCourseAndTimeStartGreaterThanEqualAndTimeEndLessThanEqual(
            Course course ,LocalDateTime startTime, LocalDateTime endTime);
    List<ScheduleLearning> findByUserAndTimeStartGreaterThanEqualAndTimeEndLessThanEqual(
            User user, LocalDateTime startTime, LocalDateTime endTime
    );

    List<ScheduleLearning> findByUserAndStateOrState(User user, StateLesson state, StateLesson state1);
}
