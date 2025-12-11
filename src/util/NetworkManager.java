package util;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList; // Gunakan ini agar aman thread

public class NetworkManager {
    private static NetworkManager instance;

    private boolean isServer;
    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // FIX: Gunakan CopyOnWriteArrayList untuk mencegah crash saat loop listener
    private final List<NetworkMessageListener> listeners;
    private NetworkThread networkThread;

    private NetworkManager() {
        listeners = new CopyOnWriteArrayList<>();
    }

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public void startServer(int port) throws IOException {
        isServer = true;

        // --- PERBAIKAN BUG PORT BUSY ---
        // 1. Buat socket tanpa port dulu
        serverSocket = new ServerSocket();
        // 2. Set Reuse Address SEBELUM bind
        serverSocket.setReuseAddress(true);
        // 3. Baru bind ke port
        serverSocket.bind(new InetSocketAddress(port));
        // -------------------------------

        System.out.println("Server started on port " + port);

        new Thread(() -> {
            try {
                System.out.println("Waiting for client connection...");
                socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());

                setupStreams();
                startNetworkThread();

                notifyListeners(Constants.MSG_PLAYER_JOINED, "Client connected");
            } catch (IOException e) {
                // Abaikan error jika socket ditutup manual
                if (serverSocket != null && !serverSocket.isClosed()) {
                    System.err.println("Error accepting client: " + e.getMessage());
                }
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

        if (networkThread != null) networkThread.stopThread();

        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {}

        try {
            if (isServer && serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException e) {}

        try {
            if (out != null) out.close();
            if (in != null) in.close();
        } catch (IOException e) {}

        // --- PERBAIKAN BUG STUCK SAAT RESTART ---
        // Hapus semua listener (Presenter Lama) agar Presenter Baru bisa masuk
        listeners.clear();

        // Jangan null-kan instance agar Singleton tetap hidup tapi bersih
        // instance = null; // Hapus baris ini jika ingin instance reusable, atau biarkan null tapi pastikan listeners clear.
        // Sebaiknya reset state variabel saja:
        isServer = false;
        socket = null;
        serverSocket = null;
        out = null;
        in = null;
        networkThread = null;

        System.out.println("Disconnected and Listeners Cleared.");
    }

    public boolean isServer() { return isServer; }
    public boolean isConnected() { return socket != null && socket.isConnected() && !socket.isClosed(); }

    public interface NetworkMessageListener {
        void onMessageReceived(String messageType, String data);
    }
}