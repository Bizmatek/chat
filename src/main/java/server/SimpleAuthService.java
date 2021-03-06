package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleAuthService implements AuthService {
    private class User {
        String login;
        String password;
        String nickName;

        public User(String login, String password, String nickName) {
            this.login = login;
            this.password = password;
            this.nickName = nickName;
        }
    }

    private List<User> usersList;

    public SimpleAuthService() {

        usersList = new ArrayList<>(Arrays.asList(
                new User("111", "111", "nick111"),
                new User("222", "222", "nick222"),
                new User("333", "333", "nick333"),
                new User("444", "444", "nick444")
        ));
    }

    @Override
    public String getLoginByLoginAndPassword(String login, String password) {

        for (User user : usersList) {
            if (login.equals(user.login)
                    && password.equals(user.password)) {
                return user.nickName;
            }
        }
        return null;
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        if (usersList.stream()
                .noneMatch(u -> u.login.equals(login) || u.nickName.equals(nickname))) {
            usersList.add(new User(login, password, nickname));
            return true;
        }
        return false;
    }
}
