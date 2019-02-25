package com.sidekick.sqs.client;

import com.sidekick.sqs.client.callback.OnError;
import com.sidekick.sqs.client.callback.OnMessage;
import com.sidekick.sqs.client.config.SqsCredentials;
import com.sidekick.sqs.client.config.QueueConfig;
import com.sidekick.sqs.client.exception.MessageException;
import com.sidekick.sqs.client.exception.QueueException;
import com.sidekick.sqs.client.util.Json;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Queue {

    private QueueConfig config;
    private SqsCredentials credentials;
    private SqsClient client;
    private TaskQueue queueTask;

    private boolean stopped = true; // Listen for new messages when 'false'
    private OnMessage onMessage = null; // Code block to execute when a message was received
    private OnError onError = null; // Code block to execute when an error occurs

    public Queue(QueueConfig config, SqsCredentials credentials) {
        super();
        this.config = config;
        this.credentials = credentials;
        init();
    }

    public SqsClient getClient() {
        return client;
    }

    public void receive() {
        receive(this.onMessage, this.onError);
    }

    public void receive(OnMessage onMessage, OnError onError) {
        if (isStopped()) {
            if (onMessage != null && onError != null) {
                this.onMessage = onMessage;
                this.onError = onError;
            }
            if (this.onMessage == null || this.onError == null) {
                throw new QueueException("No onMessage and onError callbacks were specified to execute");
            }
            this.resume();
            IntStream.range(0, this.config.getConsumers())
                    .parallel()
                    .forEach(consumer -> submitReceive());
        } else {
            throw new QueueException("Already receiving messages. Stop execution calling stop() method before calling receive()");
        }
    }

    public void stop() {
        this.stopped = true;
        this.queueTask.shutdown();
    }

    public SendMessageResponse send(Object object) {
        return send(object, null);
    }

    public SendMessageResponse send(Object object, List<MessageAttribute> attributes) {
        return send(object, attributes, 0);
    }

    public SendMessageResponse send(Object object, List<MessageAttribute> attributes, int delaySeconds) {
        String messageString = Json.toJson(object);
        return send(messageString, attributes, delaySeconds);
    }

    public SendMessageResponse send(String message) {
        return send(message, null);
    }

    public SendMessageResponse send(String message, List<MessageAttribute> attributes) {
        return send(message, attributes, 0);
    }

    public SendMessageResponse send(String message, List<MessageAttribute> attributes, int delaySeconds) {
        SendMessageRequest.Builder request = SendMessageRequest
                .builder()
                .queueUrl(queueUrl())
                .messageBody(message);
        if (attributes != null && !attributes.isEmpty()) {
            request.messageAttributes(buildAttributes(attributes));
        }
        if (delaySeconds > 0) {
            request.delaySeconds(delaySeconds);
        }
        return client.sendMessage(request.build());
    }

    public DeleteMessageResponse delete(Message message) {
        return delete(message.receiptHandle());
    }

    public DeleteMessageResponse delete(String receiptHandle) {
        return client.deleteMessage(
                DeleteMessageRequest.builder()
                        .queueUrl(queueUrl())
                        .receiptHandle(receiptHandle).build()
        );
    }

    private void init() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                this.credentials.getClient(),
                this.credentials.getSecret()
        );
        client = SqsClient.builder()
                .region(Region.of(this.credentials.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
        queueTask = new TaskQueue(this.config.getPoolSize());
    }

    private boolean isStopped() {
        return this.stopped;
    }

    private void resume() {
        this.stopped = false;
        this.queueTask.init();
    }

    private Map<String, MessageAttributeValue> buildAttributes(List<MessageAttribute> attributes) {
        Map<String, MessageAttributeValue> sqsAttributes = new HashMap<>();
        for (MessageAttribute attribute : attributes) {
            String name = attribute.getName();
            String type = attribute.getType();
            String value = attribute.getValue();
            sqsAttributes.put(name, MessageAttributeValue.builder().dataType(type).stringValue(value).build());
        }
        return sqsAttributes;
    }

    private String queueUrl() {
        String url = this.config.getUrl();
        if (url == null) {
            try {
                url = client.getQueueUrl(
                        GetQueueUrlRequest.builder()
                                .queueName(config.getName())
                                .build()
                ).queueUrl();
                this.config.setUrl(url);
            } catch (QueueDoesNotExistException ex) {
                stop();
                throw new QueueException(String.format("Queue %s doesn't exists for region %s. Stopping execution.", config.getName(), credentials.getRegion()), ex);
            }
        }
        return url;
    }

    private void pollMessages() {
        try {
            ReceiveMessageRequest.Builder request = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl())
                    .messageAttributeNames(this.config.getMessageAttributesNames())
                    .attributeNames(this.config.getAttributeNames().stream().map(QueueAttributeName::fromValue).collect(Collectors.toList()))
                    .waitTimeSeconds(this.config.getWaitTimeSeconds())
                    .maxNumberOfMessages(this.config.getMaxNumberOfMessages());
            List<Message> messages = this.client.receiveMessage(request.build()).messages();
            for (Message message : messages) {
                int receiveCount = Integer.parseInt(message.attributes().getOrDefault(MessageSystemAttributeName.APPROXIMATE_RECEIVE_COUNT, "0"));
                if (receiveCount > this.config.getRetries()) {
                    onError.call(new MessageException(
                            String.format("Exceeded max retries. Tried to process this message for %s times. Message body is: %s. I'll delete the message",
                                    receiveCount, message.body())));
                    submitDelete(message);
                } else {
                    onMessage.call(message);
                    if (this.config.delete()) {
                        submitDelete(message);
                    }
                }
            }
        } catch (Exception ex) {
            onError.call(new MessageException(String.format("An error has occurred while polling new messages from queue %s", this.config.getName()), ex));
        } finally {
            if (!this.isStopped()) {
                submitReceive();
            }
        }
    }

    private void submitDelete(Message message) {
        queueTask.submit(() -> this.delete(message));
    }

    private void submitReceive() {
        queueTask.submit(this::pollMessages);
    }
}
