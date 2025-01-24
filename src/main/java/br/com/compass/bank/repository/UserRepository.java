package br.com.compass.bank.repository;

import br.com.compass.bank.model.User;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Objects;

public class UserRepository {

    private static final SessionFactory factory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(User.class)
            .buildSessionFactory();

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
     *
     * @param user the user to save or update.
     */
    public static void save(User user) {
        Session session = getSession();
        session.beginTransaction();

        if (Objects.isNull(session.find(User.class, user.getId())))
            session.persist(user);
        else
            session.merge(user);

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

        User user = session.get(User.class, id);
        if (user != null)
            session.remove(user);

        session.getTransaction().commit();
    }

    /**
     * Closes the session factory, should be called on application shutdown.
     */
    public static void shutdown() {
        factory.close();
    }
}
