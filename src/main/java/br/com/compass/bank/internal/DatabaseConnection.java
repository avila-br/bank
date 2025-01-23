package br.com.compass.bank.internal;

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
    private static final SessionFactory factory = buildSessionFactory();

    /**
     * Builds and configures the SessionFactory.
     * It reads the Hibernate configuration file (hibernate.cfg.xml)
     * and creates a SessionFactory for interacting with the database.
     *
     * @return The created SessionFactory.
     * @throws ExceptionInInitializerError If there is an error during the creation of the SessionFactory.
     */
    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable e) {
            log.severe("Initial SessionFactory creation failed: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Closes the SessionFactory and releases any resources held by it.
     */
    public static void shutdown() {
        getFactory().close();
    }
}
