package com.nguyenhan.maddemo1.repository;

import com.nguyenhan.maddemo1.constants.StateAssignment;
import com.nguyenhan.maddemo1.model.Assignment;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByUserOrderByTimeEndAsc(User user);

    List<Assignment> findByCourseOrderByTimeEndAsc(Course course);
    List<Assignment> findByUserAndState(User user, StateAssignment state);

    List<Assignment> findByState(StateAssignment stateAssignment);
}
