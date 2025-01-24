package br.com.compass.bank.config;

import br.com.compass.bank.model.Account;
import br.com.compass.bank.model.User;

import lombok.Getter;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Utility class for setting up Hibernate session factory for tests.
 * This configuration will use the hibernate.cfg.xml from the test resources.
 */
public class HibernateConfig {

    @Getter
    private static final SessionFactory factory;

    static {
        try {
            factory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Account.class)
                    .buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void shutdown() {
        factory.close();
    }

}
