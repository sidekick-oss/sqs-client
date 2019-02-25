package com.sidekick.sqs.client;

public interface OnError {
    void call(Exception exception);
}
