package com.nguyenhan.maddemo1.repository;

import com.nguyenhan.maddemo1.constants.NotificationCategory;
import com.nguyenhan.maddemo1.model.Notification;
import com.nguyenhan.maddemo1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);
    List<Notification> findByEntityId(Long id);
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.entityId = :entityId AND n.category = :category")
    void deleteByEntityIdAndCategory(@Param("entityId") Long entityId, @Param("category") NotificationCategory category);

}
