package com.sidekick.sqs.client;

import software.amazon.awssdk.services.sqs.model.Message;

public interface OnMessage {
    void call(Message message);
}
