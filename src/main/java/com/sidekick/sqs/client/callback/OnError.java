package com.sidekick.sqs.client.callback;

public interface OnError {
    void call(Exception exception);
}
