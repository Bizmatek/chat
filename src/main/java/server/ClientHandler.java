package server;

import lombok.extern.log4j.Log4j2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

@Log4j2
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
            socket.setSoTimeout(10000);
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
                        log.info("Client %s has been authorized %n", nickName);
                        sendMessage("/authok " + nickName);
                        socket.setSoTimeout(0);
                        server.subscribe(this);
                        break;
                    } else {
                        log.info("Unable to authenticate: invalid login/password %n");
                    }
                }
                if (str.startsWith("/reg")) {
                    String[] token = str.split("\\s");
                    if (token.length != 4) {
                        continue;
                    }
                    boolean isRegistration = server.getAuthService().registration(token[1], token[2], token[3]);

                    if (isRegistration) {
                        sendMessage("/regok");
                    } else {
                        sendMessage("/regno");
                    }

                }
            } catch (SocketTimeoutException e) {
                break;
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
                } catch (SocketTimeoutException e) {
                    break;
                }catch (IOException e) {
                    e.printStackTrace();
                }

            }
            server.unsubscribe(this);
            log.info("Client disconnected");

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
