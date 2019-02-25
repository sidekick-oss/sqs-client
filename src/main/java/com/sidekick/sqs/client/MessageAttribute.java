package com.sidekick.sqs.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public final class MessageAttribute {

    private String name;
    private String type;
    private String value;

    public MessageAttribute(String name, Object value) {
        build(name, value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    private void build(String name, Object value) {
        this.name = name;
        if (isString(value)) {
            this.type = "String";
            this.value = value.toString();
        } else if(isNumber(value)) {
            this.type = "Number";
            this.value = value.toString();
        } else {
            this.type = "Binary";
            this.value = convertToBinary(value);
        }
    }

    private boolean isString(Object value) {
        return value instanceof String;
    }

    private boolean isNumber(Object value) {
        if (value instanceof Number) {
            return true;
        }
        if (value.getClass().isPrimitive()) {
            switch (value.getClass().getName()) {
                case "void":
                    return false;
                case "boolean":
                    return false;
                default:
                    return true;
            }
        }
        return false;
    }

    private String convertToBinary(Object object) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutput output = new ObjectOutputStream(outputStream)) {
            output.writeObject(object);
            return outputStream.toString();
        } catch (IOException ex) {
            return "";
        }
    }
}
