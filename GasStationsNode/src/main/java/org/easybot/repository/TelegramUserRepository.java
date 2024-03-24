package org.easybot.repository;

import org.easybot.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
    Optional<TelegramUser> findByUserId(Long userId);
    List<TelegramUser> findByUpdateTimeAfterOrderByUpdateTimeDesc(LocalDateTime startDate);
}
