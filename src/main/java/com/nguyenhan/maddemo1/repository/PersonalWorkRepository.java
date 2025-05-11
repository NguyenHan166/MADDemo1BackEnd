package com.nguyenhan.maddemo1.repository;

import com.nguyenhan.maddemo1.constants.StateAssignment;
import com.nguyenhan.maddemo1.model.Course;
import com.nguyenhan.maddemo1.model.PersonalWork;
import com.nguyenhan.maddemo1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<PersonalWork> findByUserAndState(User user, StateAssignment state);

    List<PersonalWork> findByState(StateAssignment stateAssignment);

    @Modifying
    @Query("DELETE FROM PersonalWork p WHERE p.id = :id")
    void deletePersonalWorkById(@Param("id") Long id);
}