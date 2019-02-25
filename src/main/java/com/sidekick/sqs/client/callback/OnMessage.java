package com.sidekick.sqs.client.callback;

import software.amazon.awssdk.services.sqs.model.Message;

public interface OnMessage {
    void call(Message message);
}
