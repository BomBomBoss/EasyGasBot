package org.easybot.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easybot.entity.TelegramAnswer;
import org.easybot.entity.TelegramUser;
import org.easybot.repository.TelegramUserRepository;
import org.easybot.wrapper.UpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;


    @Transactional
    public void resolveTelegramUserById(final UpdateWrapper wrapper, final TelegramAnswer telegramAnswer) {
        final User user = wrapper.user();
        final Long userId = user.getId();
        final Long chatId = wrapper.chatId();

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

    public void updateUser(final TelegramUser user)
    {
        telegramUserRepository.save(user);
    }

    public List<TelegramUser> findActiveUsersAfterDate(final LocalDateTime startDate) {
        return telegramUserRepository.findByUpdateTimeAfterOrderByUpdateTimeDesc(startDate);
    }

    public List<TelegramUser> findAllUsers() {
        return telegramUserRepository.findAll();
    }
}
