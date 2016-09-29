package com.pedalpi.pedalpi.comunication;

import java.io.IOException;

class Transmission implements Runnable {

    private boolean status;
    private Client client;

    public Transmission(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        this.status = true;

        while (status) {
            Message message = getMessage();
            this.client.send(new Message(ProtocolType.ACK));

            this.client.getListener().onMessage(message);
        }
    }

    private Message getMessage() {
        try {
            return MessageBuilder.generate(client.getStreamLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Message(ProtocolType.ERROR);
    }

    public void close() {
        this.status = false;
    }
}