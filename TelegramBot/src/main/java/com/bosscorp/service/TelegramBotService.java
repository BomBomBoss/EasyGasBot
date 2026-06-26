package com.bosscorp.service;

import com.bosscorp.model.TelegramBotEntity;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import static org.easybot.RabbitQueue.CALL_BACK_QUERY;
import static org.easybot.RabbitQueue.NOT_SUPPORTED_MESSAGE_UPDATE;
import static org.easybot.RabbitQueue.TEXT_MESSAGE_UPDATE;
import org.easybot.wrapper.UpdateWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

@Service
@Slf4j
@Lazy
public class TelegramBotService extends TelegramBotEntity implements UpdateService {

    private final RabbitTemplate rabbitTemplate;
    private final RateLimiter rateLimiter;

    @Value("${user.is.admin}")
    private String adminId;

    public TelegramBotService(final RabbitTemplate rabbitTemplate, final RateLimiter rateLimiter) {
        this.rabbitTemplate = rabbitTemplate;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void onUpdateReceived(Update update)
    {
        Long chatId = getChatId(update);
        UpdateWrapper updateWrapper = new UpdateWrapper(update, getFromUser(update), chatId, isUserAdmin(chatId));

        String queue = distributeMessageType(update);
        log.info("Sending update from client {} to node", updateWrapper.user());
        rabbitTemplate.convertAndSend(queue, updateWrapper);
    }

    @Override
    public String distributeMessageType(Update update)
    {

        if (update.hasMessage())
        {
            Message message = update.getMessage();

            if (message.hasText()) return TEXT_MESSAGE_UPDATE;
            else return NOT_SUPPORTED_MESSAGE_UPDATE;
        }
        else if (update.hasCallbackQuery()) return CALL_BACK_QUERY;

        return NOT_SUPPORTED_MESSAGE_UPDATE;
    }

    public void sendResponseToClient(SendMessage sendMessage) {
        try {
            rateLimiter.acquire();
            execute(sendMessage);
        }
        catch (TelegramApiException e)
        {
            log.error("Error during sending response to client {} with reason {}", sendMessage.getChatId(), e.getMessage() );
        }
    }

    public void sendResponseWithEditedTextToClient(EditMessageText editMessageText)
    {
        try
        {
            execute(editMessageText);
        }
        catch (TelegramApiException e)
        {
            log.error("Error during sending edited text response to client with reason {}", e.getMessage());
        }
    }

    private User getFromUser(Update update)
    {
        return update.hasMessage() ? update.getMessage().getFrom()
                : update.hasCallbackQuery() ? update.getCallbackQuery().getFrom()
                : new User();

    }

    private Long getChatId(Update update)
    {
        return update.hasMessage() ? update.getMessage().getChatId()
                : update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId()
                : 0;
    }

    private Boolean isUserAdmin(Long chatId)
    {
        return Objects.equals(Long.parseLong(adminId), chatId);
    }

}


