package br.com.compass.bank.internal;

import br.com.compass.bank.model.Account;
import br.com.compass.bank.model.Transaction;
import br.com.compass.bank.model.User;

import lombok.Getter;
import lombok.extern.java.Log;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * A utility class for managing the Hibernate SessionFactory and database connection.
 */
@Log
public class DatabaseConnection {

    /**
     * The static SessionFactory instance used for database operations.
     * It is initialized lazily using the buildSessionFactory method.
     */
    @Getter
    private static final SessionFactory factory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(Account.class)
            .addAnnotatedClass(User.class)
            .addAnnotatedClass(Transaction.class)
            .buildSessionFactory();

    /**
     * Closes the SessionFactory and releases any resources held by it.
     */
    public static void shutdown() {
        getFactory().close();
    }
}
