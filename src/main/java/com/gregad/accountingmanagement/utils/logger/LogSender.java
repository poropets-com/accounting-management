package com.gregad.accountingmanagement.utils.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gregad.accountingmanagement.utils.logger.dto.LogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;

import java.time.LocalDateTime;

@EnableBinding(ILogSender.class)
public class LogSender {
    ObjectMapper mapper=new ObjectMapper();
    @Autowired
    ILogSender producer;
    public void sendLog(String email,String message) throws JsonProcessingException {
        LogDto log=new LogDto(email, LocalDateTime.now(),message);
        String logJson=mapper.writeValueAsString(log);
        producer.exceptionlog().send(MessageBuilder.withPayload(logJson).build());
        System.out.println(logJson);
    }
}
