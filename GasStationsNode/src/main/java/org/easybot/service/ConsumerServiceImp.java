package org.easybot.service;

import lombok.extern.log4j.Log4j2;
import org.easybot.wrapper.UpdateWrapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.easybot.RabbitQueue.*;

@Log4j2
@Service
public class ConsumerServiceImp implements RabbitConsumer {
    private final MainService mainService;
    String commonLogText = "Gas Station Node received: \"{}\"";

    public ConsumerServiceImp(MainService mainService)
    {
        this.mainService = mainService;
    }


    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessage(UpdateWrapper wrapper)
    {
        log.info(commonLogText, TEXT_MESSAGE_UPDATE);
        mainService.processTextMessage(wrapper, wrapper.update().getMessage().getText());

    }

    @Override
    @RabbitListener(queues = CALL_BACK_QUERY)
    public void consumeCallBackQuery(Update update)
    {
        log.info(commonLogText, CALL_BACK_QUERY);
        mainService.processCallBackQuery(update);

    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessage(Update update)
    {
        log.info(commonLogText, PHOTO_MESSAGE_UPDATE);


    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessage(Update update)
    {
        log.info(commonLogText, DOC_MESSAGE_UPDATE);

    }

    @Override
    @RabbitListener(queues = UPDATE_EXCEPTION)
    public void consumeUpdateException(Update update)
    {
        log.info(commonLogText, UPDATE_EXCEPTION);

    }
}
