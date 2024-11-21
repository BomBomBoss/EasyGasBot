package org.easybot.service;

import lombok.extern.log4j.Log4j2;
import org.easybot.wrapper.UpdateWrapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static org.easybot.RabbitQueue.CALL_BACK_QUERY;
import static org.easybot.RabbitQueue.NOT_SUPPORTED_MESSAGE_UPDATE;
import static org.easybot.RabbitQueue.TEXT_MESSAGE_UPDATE;

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
        mainService.processTextMessage(wrapper);

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
