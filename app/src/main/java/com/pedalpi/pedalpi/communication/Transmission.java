package com.pedalpi.pedalpi.communication;

import org.json.JSONException;

import java.io.IOException;

class Transmission implements Runnable {

    private boolean status;
    private Server server;

    public Transmission(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        this.status = true;

        while (status) {
            Message message = getMessage();
            if (message != null)
                try {
                    this.server.getListener().onMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }

    private Message getMessage() {
        if (server.getClients().isEmpty())
            return null;

        Client client = server.getClients().get(0);

        try {
            String data = client.getStreamLine();
            if (data == null) {
                //Log.i("DATA", "String empty");
                return null;
            }

            return MessageBuilder.generate(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Message(ProtocolType.ERROR);
    }

    public void close() {
        this.status = false;
    }
}
