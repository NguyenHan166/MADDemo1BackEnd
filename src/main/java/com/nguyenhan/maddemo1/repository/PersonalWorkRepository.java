package com.nguyenhan.maddemo1.repository;

import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.PersonalWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalWorkRepository extends JpaRepository<PersonalWork, Long> {
    List<PersonalWork> findByUserId(Long userId);
//    List<PersonalWork> findByCourseId(Long courseId);

//    @Override
//    void delete(PersonalWork entity);

    Optional<PersonalWork> findByName(String name);
}