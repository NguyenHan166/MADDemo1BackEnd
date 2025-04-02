package com.nguyenhan.maddemo1.repository;

import com.nguyenhan.maddemo1.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByUserId(Long userId);
    Optional<Course> findByName(String name);
}
