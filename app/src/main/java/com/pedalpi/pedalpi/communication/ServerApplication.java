package com.pedalpi.pedalpi.communication;

import android.app.Application;

import java.io.IOException;

public class ServerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Runnable runnable = new Runnable() {
            @Override public void run() {
                runServer();
            }
        };

        new Thread(runnable).start();
    }

    private void runServer() {
        Server server = Server.getInstance();
        server.start(8888);

        try {
            while (true)
                server.waitClient();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}