package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import org.easybot.entity.CommonStation;
import org.easybot.entity.TelegramAnswer;
import org.easybot.entity.TelegramUser;
import org.easybot.repository.TelegramUserRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

import static org.easybot.CommonTexts.*;

@Component
@Slf4j
public class TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;
    private final TelegramAnswer telegramAnswer;

    private final MessageSource messageSource;


    public TelegramUserService(TelegramUserRepository telegramUserRepository, TelegramAnswer telegramAnswer, MessageSource messageSource)
    {
        this.telegramUserRepository = telegramUserRepository;
        this.telegramAnswer = telegramAnswer;
        this.messageSource = messageSource;
    }

    public void resolveTelegramUserById(User user)
    {
        Long userId = user.getId();
         telegramUserRepository.findByUserId(userId)
                .ifPresentOrElse(tUser ->{
                    tUser.resolveLocaleFromLanguageCode(tUser.getLanguageCode());
                    telegramAnswer.setTelegramUser(tUser);
                    log.info("User with id: {} and name {} is found in DB", tUser.getUserId(), tUser.getFirstName());
                    },
                        ()-> {
            TelegramUser telegramUser = new TelegramUser();
            telegramUser.setUserId(userId);
            telegramUser.setFirstName(user.getFirstName());
            telegramUser.setLanguageCode(user.getLanguageCode());
            telegramUser.resolveLocaleFromLanguageCode(user.getLanguageCode());
            telegramAnswer.setTelegramUser(telegramUserRepository.save(telegramUser));
            log.info("User with id: {} and name {} is NOT found in DB. Persisting ... ", telegramUser.getUserId(), telegramUser.getFirstName());
        });

    }
}
