package com.nguyenhan.maddemo1.repository;

import com.nguyenhan.maddemo1.model.ScheduleLearning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleLearningRepository extends JpaRepository<ScheduleLearning, Long> {
}
