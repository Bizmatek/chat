package server.dao;

import org.aeonbits.owner.Config;

import static org.aeonbits.owner.Config.*;

@Sources("classpath:db/queries.properties")
public interface UsersQueriesConfig extends Config {
    @Key("INSERT_USER")
    String insertUser();

    @Key("SELECT_USER_BY_NAME")
    String selectUserByName();
}
