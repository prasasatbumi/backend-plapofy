package com.finprov.loan.repository;

import com.finprov.loan.entity.Notification;
import com.finprov.loan.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
  List<Notification> findByUser(User user);

  List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

  long countByUserIdAndReadFalse(Long userId);
}
