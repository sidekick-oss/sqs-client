package com.sidekick.sqs.client.config;

public final class SqsCredentials {

    private String client;
    private String secret;
    private String region;

    public SqsCredentials withClient(String client) {
        this.setClient(client);
        return this;
    }

    public SqsCredentials withSecret(String secret) {
        this.setSecret(secret);
        return this;
    }

    public SqsCredentials withRegion(String region) {
        this.setRegion(region);
        return this;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
