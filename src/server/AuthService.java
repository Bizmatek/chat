package server;

public interface AuthService {
    String getLoginByLoginAndPassword(String login, String password);
}
