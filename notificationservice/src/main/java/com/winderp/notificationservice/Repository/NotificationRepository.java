package com.winderp.notificationservice.Repository;

import com.winderp.notificationservice.Models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByDateEnvoiDesc(Long userId);

    List<Notification> findByUserIdAndReadedFalse(Long userId);
}