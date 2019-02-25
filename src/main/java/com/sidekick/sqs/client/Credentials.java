package com.sidekick.sqs.client;

public final class Credentials {

    private String client;
    private String secret;
    private String region;

    public Credentials withClient(String client) {
        this.setClient(client);
        return this;
    }

    public Credentials withSecret(String secret) {
        this.setSecret(secret);
        return this;
    }

    public Credentials withRegion(String region) {
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
