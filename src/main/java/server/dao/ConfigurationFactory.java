package server.dao;

import org.aeonbits.owner.ConfigFactory;
import org.aeonbits.owner.Factory;

public class ConfigurationFactory {
    private static final Factory FACTORY = ConfigFactory.newInstance();

    private ConfigurationFactory() {
    }

    public static UsersQueriesConfig getUsersQueriesConfig() {
        return FACTORY.create(UsersQueriesConfig.class);
    }
}
