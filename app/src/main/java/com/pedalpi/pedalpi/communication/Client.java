package com.pedalpi.pedalpi.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Client {

    private Socket connection;
    private PrintStream out;
    private BufferedReader in;

    public Client(Socket connection) {
        try {
            this.connection = connection;
            this.out = new PrintStream(this.connection.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() throws IOException {
        connection.close();
    }

    public void send(Message message) {
        out.print(message.toString());
    }

    public String getStreamLine() throws IOException {
        return in.readLine();
    }

    public Socket getConnection() {
        return connection;
    }
}
