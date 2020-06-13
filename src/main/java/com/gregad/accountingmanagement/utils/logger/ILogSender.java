package com.gregad.accountingmanagement.utils.logger;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ILogSender {
    String EXCEPTION_LOG="exceptionlog";
    @Output(EXCEPTION_LOG)
    MessageChannel exceptionlog();
    
}
