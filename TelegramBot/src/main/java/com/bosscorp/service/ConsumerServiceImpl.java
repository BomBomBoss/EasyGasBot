package com.bosscorp.service;

import com.bosscorp.model.TelegramBotEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Slf4j
@Lazy
public class ConsumerServiceImpl extends TelegramBotEntity implements UpdateValidator{

    @Override
    public void onUpdateReceived(Update update)
    {
        contentValidate(update);
        log.info("Received update Id = [{}] from chat Id = [{}]", update.getUpdateId(), update.getMessage().getChatId());
    }

    @Override
    public void contentValidate(Update update)
    {
        Message message = update.getMessage();
        String chatId = message.getChatId().toString();

        if (!update.hasMessage())
        {
            try
            {
                execute(new SendMessage(chatId, "Sorry, something went wrong"));
                log.error("Error in chat Id = [{}]", chatId);
            }
            catch (TelegramApiException e)
            {
                e.printStackTrace();
            }
        }

    }
}
