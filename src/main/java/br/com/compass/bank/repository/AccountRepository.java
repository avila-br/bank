package br.com.compass.bank.repository;

import br.com.compass.bank.internal.DatabaseConnection;
import br.com.compass.bank.model.Account;
import br.com.compass.bank.model.User;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class AccountRepository {

    private static final SessionFactory factory = DatabaseConnection.getFactory();

    private static final ThreadLocal<Session> context = new ThreadLocal<>();

    /**
     * Gets the current Hibernate session. Creates a new one if none exists for the current thread.
     *
     * @return the current Hibernate session.
     */
    public static Session getSession() {
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
    public static void closeSession() {
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

        try {
            // Save or update the associated user
            User user = account.getUser();
            if (user != null) {
                if (user.getId() == null || session.find(User.class, user.getId()) == null)
                    session.persist(user);
                else
                    session.merge(user);

                account.setUser(user);
            }

            // Save or update the account
            if (account.getId() == null || session.find(Account.class, account.getId()) == null)
                session.persist(account);
            else
                session.merge(account);

            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
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
     * Lists all accounts in the database.
     *
     * @return a list of all accounts.
     */
    public static List<Account> list() {
        Session session = getSession();

        return session.createQuery("FROM Account", Account.class).list();
    }

    /**
     * Closes the session factory, should be called on application shutdown.
     */
    public static void shutdown() {
        factory.close();
    }
}

