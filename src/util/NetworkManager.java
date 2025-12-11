package util;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class NetworkManager {
    private static NetworkManager instance;

    private boolean isServer;
    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private List<NetworkMessageListener> listeners;
    private NetworkThread networkThread;

    private NetworkManager() {
        listeners = new ArrayList<>();
    }

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public void startServer(int port) throws IOException {
        isServer = true;
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        // Start thread untuk menerima koneksi
        new Thread(() -> {
            try {
                System.out.println("Waiting for client connection...");
                socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());

                setupStreams();
                startNetworkThread();

                notifyListeners(Constants.MSG_PLAYER_JOINED, "Client connected");
            } catch (IOException e) {
                System.err.println("Error accepting client: " + e.getMessage());
            }
        }).start();
    }

    public void connectToServer(String host, int port) throws IOException {
        isServer = false;
        socket = new Socket(host, port);
        System.out.println("Connected to server: " + host + ":" + port);

        setupStreams();
        startNetworkThread();
    }

    private void setupStreams() throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void startNetworkThread() {
        networkThread = new NetworkThread(in, this);
        networkThread.start();
    }

    public synchronized void sendMessage(String messageType, String data) {
        if (out != null) {
            String message = messageType + Constants.MESSAGE_DELIMITER + data;
            out.println(message);
        }
    }

    public void handleIncomingMessage(String message) {
        if (message == null || message.isEmpty()) return;
        String[] parts = message.split("\\" + Constants.MESSAGE_DELIMITER, 2);
        if (parts.length >= 1) {
            String messageType = parts[0];
            String data = parts.length > 1 ? parts[1] : "";
            notifyListeners(messageType, data);
        }
    }

    public void addListener(NetworkMessageListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NetworkMessageListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(String messageType, String data) {
        for (NetworkMessageListener listener : listeners) {
            listener.onMessageReceived(messageType, data);
        }
    }

    public void disconnect() {
        System.out.println("Force disconnecting...");

        // 1. Matikan Thread Listener terlebih dahulu
        if (networkThread != null) {
            networkThread.stopThread();
        }

        // 2. TUTUP SOCKET UTAMA DULUAN (Ini kunci agar tidak macet)
        // Dengan menutup socket, stream input/output akan otomatis error dan berhenti
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            // Abaikan error saat menutup, yang penting perintah tutup jalan
        }

        // 3. Tutup Server Socket (Agar port 5000 bisa dipakai lagi)
        try {
            if (isServer && serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 4. Bersihkan Stream (Opsional, karena socket sudah tutup)
        try {
            if (out != null) out.close();
            if (in != null) in.close();
        } catch (IOException e) {
            // Abaikan
        }

        // 5. Reset Instance
        instance = null;

        System.out.println("Disconnected completely.");
    }

    public boolean isServer() {
        return isServer;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public interface NetworkMessageListener {
        void onMessageReceived(String messageType, String data);
    }
}