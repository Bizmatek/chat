package server;

public interface AuthService {
    String getLoginByLoginAndPassword(String login, String password);
    boolean registration(String login, String password, String nickname);
}
