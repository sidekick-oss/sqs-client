package com.sidekick.sqs.client.config;

import java.util.ArrayList;
import java.util.List;

public final class QueueConfig {

    private String name; // Name of the queue
    private String url;  // Url of the queue
    private int waitTimeSeconds; // Default queue poll time
    private int maxNumberOfMessages; // Max amount of messages to receive in one request
    private boolean delete; // Delete messages after successful processing
    private int retries; // Default processing retries
    private int consumers; // Default consumers amount
    private int poolSize; // Default pool size for consuming messages
    private List<String> messageAttributesNames; // Get all message attributes by default
    private List<String> attributeNames; // Get only approximate receive count by default

    public QueueConfig() {
        super();
        this.setName("");
        this.setUrl("");
        this.setWaitTimeSeconds(20);
        this.setMaxNumberOfMessages(10);
        this.setDelete(false);
        this.setRetries(10);
        this.setConsumers(1);
        this.setMessageAttributesNames(List.of("All"));
        this.setAttributeNames(List.of("ApproximateReceiveCount"));
    }

    public QueueConfig withName(String name) {
        this.setName(name);
        return this;
    }

    public QueueConfig withUrl(String url) {
        this.setUrl(url);
        return this;
    }

    public QueueConfig withWaitTimeSeconds(int waitTimeSeconds) {
        this.setWaitTimeSeconds(waitTimeSeconds);
        return this;
    }

    public QueueConfig withMaxNumberOfMessages(int maxNumberOfMessages) {
        this.setMaxNumberOfMessages(maxNumberOfMessages);
        return this;
    }

    public QueueConfig withDelete(boolean delete) {
        this.setDelete(delete);
        return this;
    }

    public QueueConfig withRetries(int retries) {
        this.setRetries(retries);
        return this;
    }

    public QueueConfig withConsumers(int consumers) {
        this.setConsumers(consumers);
        return this;
    }

    public QueueConfig withMessageAttributesNames(List<String> messageAttributesNames) {
        this.setMessageAttributesNames(messageAttributesNames);
        return this;
    }

    public QueueConfig withAttributeNames(List<String> attributeNames) {
        this.setAttributeNames(attributeNames);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWaitTimeSeconds() {
        return waitTimeSeconds;
    }

    public void setWaitTimeSeconds(int waitTimeSeconds) {
        this.waitTimeSeconds = waitTimeSeconds;
    }

    public int getMaxNumberOfMessages() {
        return maxNumberOfMessages;
    }

    public void setMaxNumberOfMessages(int maxNumberOfMessages) {
        this.maxNumberOfMessages = maxNumberOfMessages;
    }

    public boolean delete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getConsumers() {
        return consumers;
    }

    public void setConsumers(int consumers) {
        this.consumers = consumers;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public List<String> getMessageAttributesNames() {
        return messageAttributesNames;
    }

    public void setMessageAttributesNames(List<String> messageAttributesNames) {
        if (messageAttributesNames == null) {
            messageAttributesNames = new ArrayList<>();
        }
        this.messageAttributesNames = messageAttributesNames;
    }

    public List<String> getAttributeNames() {
        return attributeNames;
    }

    public void setAttributeNames(List<String> attributeNames) {
        if (attributeNames == null) {
            this.attributeNames = new ArrayList<>();
        }
        this.attributeNames = attributeNames;
    }
}
