package com.pedalpi.pedalpi.comunication;

public enum ProtocolType {
    ACK("ack"),
    EFFECT("effect"),
    PARAM("param"),
    PATCH("patch"),
    ERROR("error");

    private final String type;

    ProtocolType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}