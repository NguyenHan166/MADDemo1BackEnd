package com.nguyenhan.maddemo1.repository;

import com.nguyenhan.maddemo1.model.ScheduleLearning;
import com.nguyenhan.maddemo1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleLearningRepository extends JpaRepository<ScheduleLearning, Long> {

    List<ScheduleLearning> findAllByUserId(Long userId);

    Long user(User user);
}
