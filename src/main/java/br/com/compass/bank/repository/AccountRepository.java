package br.com.compass.bank.repository;

import br.com.compass.bank.model.Account;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.Objects;

public class AccountRepository {

    private static final SessionFactory factory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(Account.class)
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
     * Finds an account by its ID.
     *
     * @param id the ID of the account to find.
     * @return the Account with the given ID, or null if not found.
     */
    public static Account find(Long id) {
        Session session = getSession();

        return session.get(Account.class, id);
    }

    /**
     * Finds all accounts associated with a particular user.
     *
     * @param id the ID of the user.
     * @return a list of accounts for the specified user.
     */
    public static List<Account> findByUser(Long id) {
        Session session = getSession();

        return session.createQuery("FROM Account WHERE user.id = :id", Account.class)
                .setParameter("id", id)
                .list();
    }

    /**
     * Saves or updates the account in the database.
     *
     * @param account the account to save or update.
     */
    public static void save(Account account) {
        Session session = getSession();
        session.beginTransaction();

        if (Objects.isNull(session.find(Account.class, account.getId())))
            session.persist(account);
        else
            session.merge(account);

        session.getTransaction().commit();
    }

    /**
     * Deletes an account from the database by ID.
     *
     * @param id the ID of the account to delete.
     */
    public static void delete(Long id) {
        Session session = getSession();
        session.beginTransaction();

        Account account = session.get(Account.class, id);
        if (account != null)
            session.remove(account);

        session.getTransaction().commit();
    }

    /**
     * Closes the session factory, should be called on application shutdown.
     */
    public static void shutdown() {
        factory.close();
    }
}

