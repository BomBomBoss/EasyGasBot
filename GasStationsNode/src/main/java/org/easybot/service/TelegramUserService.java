package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import org.easybot.entity.TelegramAnswer;
import org.easybot.entity.TelegramUser;
import org.easybot.repository.TelegramUserRepository;
import org.easybot.wrapper.UpdateWrapper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@Slf4j
public class TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;
    private final TelegramAnswer telegramAnswer;


    public TelegramUserService(TelegramUserRepository telegramUserRepository, TelegramAnswer telegramAnswer)
    {
        this.telegramUserRepository = telegramUserRepository;
        this.telegramAnswer = telegramAnswer;
    }

    public void resolveTelegramUserById(UpdateWrapper wrapper)
    {
        User user = wrapper.user();
        Long userId = user.getId();
        Long chatId = wrapper.chatId();

        telegramUserRepository.findByUserId(userId)
                .ifPresentOrElse(tUser -> {
                            tUser.resolveLocaleFromLanguageCode(tUser.getLanguageCode());
                            telegramAnswer.setTelegramUser(tUser);
                            log.info("User with id: {} and name {} is found in DB", tUser.getUserId(), tUser.getFirstName());
                            tUser.setUpdateTime(LocalDateTime.now(ZoneOffset.UTC));
                            telegramUserRepository.save(tUser);
                            log.info("User saved in DB with updated time");
                        },
                        () -> {
                            TelegramUser telegramUser = new TelegramUser();
                            telegramUser.setUserId(userId);
                            telegramUser.setChatId(chatId);
                            telegramUser.setFirstName(user.getFirstName());
                            telegramUser.setLanguageCode(user.getLanguageCode());
                            telegramUser.resolveLocaleFromLanguageCode(user.getLanguageCode());
                            telegramUser.setUpdateTime(LocalDateTime.now(ZoneOffset.UTC));
                            telegramAnswer.setTelegramUser(telegramUserRepository.save(telegramUser));
                            log.info("User with id: {} and name {} is NOT found in DB. Persisting ... ", telegramUser.getUserId(), telegramUser.getFirstName());
                        });

    }

    public void updateUser(TelegramUser user)
    {
        telegramUserRepository.save(user);
    }

    public List <TelegramUser> findActiveUsersAfterDate(LocalDateTime startDate)
    {
        return telegramUserRepository.findByUpdateTimeAfterOrderByUpdateTimeDesc(startDate);
    }

    public List <TelegramUser> findAllUsers()
    {
        return telegramUserRepository.findAll();
    }
}
