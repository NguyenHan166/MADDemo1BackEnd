package com.nguyenhan.maddemo1.repository;

import com.nguyenhan.maddemo1.constants.StateLesson;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.ScheduleLearning;
import com.nguyenhan.maddemo1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<ScheduleLearning> findByUserAndState(User user, StateLesson state);
    List<ScheduleLearning> findByState(StateLesson state);

    @Modifying
    @Query("DELETE FROM ScheduleLearning s WHERE s.id = :id ")
    void deleteScheduleLearningById(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM ScheduleLearning s WHERE s.course.id = :id ")
    void deleteScheduleLearningByCourse(@Param("id") Long id);
}
