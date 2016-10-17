package com.pedalpi.pedalpi.communication;

import org.json.JSONException;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;

public class Server {
    public interface OnMessageListener {
        void onMessage(Message message) throws JSONException;
    }

    private static Server instance;

    public static synchronized Server getInstance() {
        if (instance != null)
            return instance;

        Server.instance = new Server();

        return Server.instance;
    }

    private ServerSocket connection;
    private List<Client> clients = new LinkedList<>();

    private OnMessageListener listener;

    private Transmission transmission;

    private Server() {
        listener = new Server.OnMessageListener() {public void onMessage(Message message) {}};
    }

    public void start(int port) {
        try {
            this.connection = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);//e.printStackTrace();
        }

        transmission = new Transmission(this);
        new Thread(transmission).start();
    }

    public void close() {
        transmission.close();
        try {
            for (Client client : clients)
                client.disconnect();

            connection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void waitClient() throws IOException {
        Client client = new Client(connection.accept());
        client.send(new Message(ProtocolType.ACK));
        this.clients.add(client);
    }

    public void send(Message message) {
        for (Client clients : this.clients)
            clients.send(message);
    }

    public void setListener(OnMessageListener listener) {
        this.listener = listener;
    }

    public OnMessageListener getListener() {
        return listener;
    }

    public List<Client> getClients() {
        return clients;
    }
}
