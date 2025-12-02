package util;

import java.io.BufferedReader;
import java.io.IOException;

public class NetworkThread extends Thread {

    private BufferedReader in;
    private NetworkManager manager;
    private volatile boolean running = true;

    public NetworkThread(BufferedReader in, NetworkManager manager) {
        this.in = in;
        this.manager = manager;
    }

    @Override
    public void run() {
        System.out.println("Network thread started.");

        try {
            String line;
            while (running && (line = in.readLine()) != null) {
                manager.handleIncomingMessage(line);
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("NetworkThread error: " + e.getMessage());
            }
        }

        System.out.println("Network thread stopped.");
    }

    public void stopThread() {
        running = false;
        try {
            in.close();
        } catch (IOException ignored) {}
    }
}
