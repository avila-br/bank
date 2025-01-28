package br.com.compass.bank.repository;

import br.com.compass.bank.internal.DatabaseConnection;
import br.com.compass.bank.model.User;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Objects;

/**
 * UserRepository provides CRUD operations for User entities using Hibernate.
 * It is responsible for interacting with the database to manage User data.
 */
public class UserRepository {

    // Hibernate SessionFactory to manage session creation
    private static final SessionFactory factory = DatabaseConnection.getFactory();

    // ThreadLocal to store a Session for the current thread
    private static final ThreadLocal<Session> context = new ThreadLocal<>();

    /**
     * Gets the current Hibernate session. Creates a new one if none exists for the current thread.
     *
     * @return the current Hibernate session.
     */
    private static Session getSession() {
        Session session = context.get();
        if (session == null || !session.isOpen()) {
            session = factory.openSession();
            context.set(session);
        }

        return session;
    }

    /**
     * Closes the current Hibernate session.
     * This method should be called after each operation to release resources.
     */
    private static void closeSession() {
        Session session = context.get();
        if (session != null && session.isOpen())
            session.close();

        context.remove();
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user to find.
     * @return the User with the given ID, or null if not found.
     */
    public static User find(Long id) {
        Session session = getSession();

        return session.get(User.class, id);
    }

    /**
     * Finds all users in the database.
     *
     * @return a list of all users.
     */
    public static List<User> list() {
        Session session = getSession();

        return session.createQuery("FROM User", User.class).list();
    }

    /**
     * Saves or updates the user in the database.
     * If the user does not exist, it will be inserted; otherwise, it will be updated.
     *
     * @param user the user to save or update.
     */
    public static void save(User user) {
        Session session = getSession();
        session.beginTransaction();

        // Check if the user exists in the database
        if (user.getId() == null || Objects.isNull(session.find(User.class, user.getId())))
            session.persist(user); // Insert new user
        else
            session.merge(user); // Update existing user

        session.getTransaction().commit();
    }

    /**
     * Deletes a user from the database by ID.
     *
     * @param id the ID of the user to delete.
     */
    public static void delete(Long id) {
        Session session = getSession();
        session.beginTransaction();

        // Find the user by ID and remove it
        User user = session.get(User.class, id);
        if (user != null)
            session.remove(user);

        session.getTransaction().commit();
    }

    /**
     * Closes the session factory, should be called on application shutdown.
     * This method releases resources and closes the Hibernate factory.
     */
    public static void shutdown() {
        factory.close();
    }
}
