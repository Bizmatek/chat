package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    private List<ClientHandler> clientsList;
    private final int PORT = 8189;
    private Socket socket;
    private AuthService authService;

    public static void main(String[] args) {
        Server server = new Server();
        server.init();
    }

    private void init() {
        authService = new SimpleAuthService();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            clientsList = Collections.synchronizedList(new ArrayList<>());

            while (true) {
                socket = serverSocket.accept();
                System.out.println("client connected");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clientsList.add(clientHandler);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clientsList.remove(clientHandler);
        broadcastClientList();
    }

    public void sendBroadcast(ClientHandler sender, String msg) {
        String message = String.format("%s: %s", sender.getNickName(), msg);
        clientsList.forEach(c -> c.sendMessage(message));
    }

    public void sendDirect(ClientHandler sender, String recipient, String msg) {
        clientsList.stream()
                .filter(c -> c.getNickName().equals(recipient) || c.getNickName().equals(sender.getNickName()))
                .forEach(c -> c.sendMessage(String.format("%s: %s", sender.getNickName(), msg)));
    }

    public String auth(String login, String password) {
        return authService.getLoginByLoginAndPassword(login, password);
    }

    public AuthService getAuthService() {
        return authService;
    }

    private void broadcastClientList() {
        StringBuilder sb = new StringBuilder("/clientsList ");
        clientsList.stream()
                .forEach(c -> sb.append(c.getNickName()).append(" "));
//        for (ClientHandler c : clientsList) {
//            sb.append(c.getNickName()).append(" ");
//        }
        String msg = sb.toString();
        clientsList.forEach(c -> c.sendMessage(msg));
//        for (ClientHandler c : clientsList) {
//            c.sendMessage(msg);
//        }
    }
}
