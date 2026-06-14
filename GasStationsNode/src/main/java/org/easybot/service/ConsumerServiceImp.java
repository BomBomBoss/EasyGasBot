package org.easybot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.easybot.RabbitQueue.CALL_BACK_QUERY;
import static org.easybot.RabbitQueue.NOT_SUPPORTED_MESSAGE_UPDATE;
import static org.easybot.RabbitQueue.TEXT_MESSAGE_UPDATE;
import org.easybot.wrapper.UpdateWrapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerServiceImp implements RabbitConsumer {

    private final MainService mainService;
    private final static String COMMON_LOG_TEXT = "Gas Station Node received: \"{}\"";

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessage(final UpdateWrapper wrapper)
    {
        try {
            log.info(COMMON_LOG_TEXT, TEXT_MESSAGE_UPDATE);
            mainService.processTextMessage(wrapper);
        }  catch (Exception e) {
            log.error("Error during consuming rabbit mq queue 'Text Message Update' with reason: '{}'", e.getMessage());
        }

    }

    @Override
    @RabbitListener(queues = CALL_BACK_QUERY)
    public void consumeCallBackQuery(final UpdateWrapper wrapper)
    {
        try {
            log.info(COMMON_LOG_TEXT, CALL_BACK_QUERY);
            mainService.processCallBackQuery(wrapper);
        }  catch (Exception e) {
            log.error("Error during consuming rabbit mq queue 'Call back query' with reason: '{}'", e.getMessage());
        }

    }

    @Override
    @RabbitListener(queues = NOT_SUPPORTED_MESSAGE_UPDATE)
    public void consumeNotSupportedUpdate(final UpdateWrapper wrapper)
    {
        try {
            log.info(COMMON_LOG_TEXT, NOT_SUPPORTED_MESSAGE_UPDATE);
            log.error("Unsupported format received from user: {} ", wrapper.user());
            mainService.processUnsupportedUpdate(wrapper);
        }
        catch (Exception e) {
            log.error("Error during consuming rabbit mq queue 'Not Supported Message' with reason: '{}'", e.getMessage());
        }

    }
}
