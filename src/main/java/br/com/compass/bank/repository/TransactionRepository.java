package br.com.compass.bank.repository;

import br.com.compass.bank.internal.DatabaseConnection;
import br.com.compass.bank.model.Account;
import br.com.compass.bank.model.Transaction;
import br.com.compass.bank.model.TransactionType;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionRepository {

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
     */
    private static void closeSession() {
        Session session = context.get();
        if (session != null && session.isOpen())
            session.close();

        context.remove();
    }

    /**
     * Finds all transactions for a particular account, either as sender or receiver.
     *
     * @param account the account to find transactions for.
     * @return a list of transactions for the specified account, either as sender or receiver.
     */
    public static List<Transaction> findByAccount(Account account) {
        Session session = getSession();

        return session.createQuery("FROM Transaction t WHERE t.sender = :account OR t.receiver = :account", Transaction.class)
                .setParameter("account", account)
                .list();
    }

    /**
     * Finds all transactions where the specified account is the sender.
     *
     * @param account the account to find transactions for as sender.
     * @return a list of transactions where the account is the sender.
     */
    public static List<Transaction> findBySender(Account account) {
        Session session = getSession();

        return session.createQuery("FROM Transaction t WHERE t.sender = :account", Transaction.class)
                .setParameter("account", account)
                .list();
    }

    /**
     * Finds all transactions where the specified account is the receiver.
     *
     * @param account the account to find transactions for as receiver.
     * @return a list of transactions where the account is the receiver.
     */
    public static List<Transaction> findByReceiver(Account account) {
        Session session = getSession();

        return session.createQuery("FROM Transaction t WHERE t.receiver = :account", Transaction.class)
                .setParameter("account", account)
                .list();
    }

    /**
     * Saves a transaction to the database.
     *
     * @param transaction the transaction to save.
     */
    public static void save(Transaction transaction) {
        Session session = getSession();
        session.beginTransaction();

        try {
            if (transaction.getId() == null || session.find(Transaction.class, transaction.getId()) == null)
                session.persist(transaction);
            else
                session.merge(transaction);

            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    /**
     * Performs a withdrawal transaction on an account.
     *
     * @param from the account from which funds are withdrawn.
     * @param amount the amount to withdraw.
     */
    public static void withdraw(Account from, BigDecimal amount) {
        Transaction transaction = Transaction.builder()
                .sender(from)
                .receiver(null)
                .type(TransactionType.WITHDRAWAL)
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .build();

        save(transaction);
    }

    /**
     * Performs a deposit transaction on an account.
     *
     * @param to the account to which funds are deposited.
     * @param amount the amount to deposit.
     */
    public static void deposit(Account to, BigDecimal amount) {
        Transaction transaction = Transaction.builder()
                .sender(null)
                .receiver(to)
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .build();

        save(transaction);
    }

    /**
     * Performs a transfer transaction between two accounts.
     *
     * @param from the account from which funds are withdrawn.
     * @param to the account to which funds are deposited.
     * @param amount the amount to transfer.
     */
    public static void transfer(Account from, Account to, BigDecimal amount) {
        Transaction transaction = Transaction.builder()
                .sender(from)
                .receiver(to)
                .type(TransactionType.TRANSFER)
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .build();

        save(transaction);
    }

    /**
     * Lists all transactions.
     *
     * @return a list of all transactions.
     */
    public static List<Transaction> list() {
        Session session = getSession();

        return session.createQuery("FROM Transaction", Transaction.class).list();
    }

    /**
     * Closes the session factory, should be called on application shutdown.
     */
    public static void shutdown() {
        factory.close();
    }

}
