package com.bosscorp.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.easybot.RabbitQueue.ANSWER_EDITED_MESSAGE;
import static org.easybot.RabbitQueue.ANSWER_MESSAGE;
import static org.easybot.RabbitQueue.CALL_BACK_QUERY;
import static org.easybot.RabbitQueue.ERROR_MESSAGE;
import static org.easybot.RabbitQueue.NOT_SUPPORTED_MESSAGE_UPDATE;
import static org.easybot.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Configuration
public class RabbitConfiguration {
    @Bean
    public MessageConverter jsonMessageConverter()
    {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public org.springframework.amqp.core.Queue textMessageQueue()
    {
        return new Queue(TEXT_MESSAGE_UPDATE);
    }

    @Bean
    public Queue callBackQueue()
    {
        return new Queue(CALL_BACK_QUERY);
    }

    @Bean
    public Queue answerMessageQueue()
    {
        return new Queue(ANSWER_MESSAGE);
    }

    @Bean
    public Queue answerEditedMessageQueue()
    {
        return new Queue(ANSWER_EDITED_MESSAGE);
    }

    @Bean
    public Queue notSupprtedMessageQueue()
    {
        return new Queue(NOT_SUPPORTED_MESSAGE_UPDATE);
    }

    @Bean
    public Queue errorReportMessage()
    {
        return new Queue(ERROR_MESSAGE);
    }
}
