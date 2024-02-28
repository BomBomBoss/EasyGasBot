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
    public void consumeCallBackQuery(UpdateWrapper wrapper)
    {
        log.info(commonLogText, CALL_BACK_QUERY);
        mainService.processCallBackQuery(wrapper);

    }

    @Override
    @RabbitListener(queues = NOT_SUPPORTED_MESSAGE_UPDATE)
    public void consumeNotSupportedUpdate(UpdateWrapper wrapper)
    {
        log.info(commonLogText, NOT_SUPPORTED_MESSAGE_UPDATE);
        log.error("Unsupported format received from user: {} ", wrapper.user());
        mainService.processUnsupportedUpdate(wrapper);

    }
}
