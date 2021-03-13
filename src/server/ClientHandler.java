package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //try to authenticate
        while (true) {
            try {
                String str = inputStream.readUTF();
                if (str.startsWith("/auth")) {
                    String[] token = str.split("\\s");
                    nickName = server.auth(token[1], token[2]);
                    if (nickName != null) {
                        System.out.printf("Client %s has been authorized \n", nickName);
                        sendMessage("/authok " + nickName);
                        server.subscribe(this);
                        break;
                    } else {
                        System.out.println("Unable to authenticate: invalid login/password \n");
                    }
                }
                if (str.startsWith("/reg")) {
                    String[] token = str.split("\\s");
                    if (token.length != 4) {
                        continue;
                    }
                    boolean isRegistration = server.getAuthService().registration(token[1], token[2], token[3]);

                    if(isRegistration){
                        sendMessage("/regok");
                    } else {
                        sendMessage("/regno");
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Runnable t1 = (() -> {
            while (true) {
                try {
                    String str = inputStream.readUTF();
                    if (str.equals("/end")) {
                        break;
                    }

                    if (str.startsWith("/w ")) {
                        String recipient = str.split("\\s")[1];
                        String msg = str.replace(str.split("\\s")[0], "").replace(str.split("\\s")[1], "");
                        server.sendDirect(this, recipient, msg);
                        continue;
                    }
                    server.sendBroadcast(this, str);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            server.unsubscribe(this);
            System.out.println("Client disconnected");

        });
        new Thread(t1).start();
    }

    public void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
