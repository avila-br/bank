package br.com.compass.bank.repository;

import br.com.compass.bank.config.HibernateConfig;
import br.com.compass.bank.model.Account;
import br.com.compass.bank.model.AccountType;
import br.com.compass.bank.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest {

    private static User user = User.builder()
            .name("John Doe")
            .cpf("123.456.789-00")
            .phone("+55 77 98802-8746")
            .build();

    @BeforeAll
    static void setup() {
        try (Session session = HibernateConfig.getFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            fail("User setup failed due to exception: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        HibernateConfig.shutdown();
    }

    @Test
    void testCreateAccount() {
        Account account = Account.builder()
                .user(user)
                .type(AccountType.CHECKING)
                .build();

        try (Session session = HibernateConfig.getFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            AccountRepository.save(account);
            transaction.commit();

            assertNotNull(account.getId());
            assertEquals(user, account.getUser());
            assertEquals(AccountType.CHECKING, account.getType());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    void testFindAccountById() {
        Account account = Account.builder()
                .user(user)
                .type(AccountType.SAVINGS)
                .build();

        try (Session session = HibernateConfig.getFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            AccountRepository.save(account);
            transaction.commit();

            Account retrievedAccount = AccountRepository.find(account.getId());
            assertNotNull(retrievedAccount);
            assertEquals(account.getId(), retrievedAccount.getId());
            assertEquals(AccountType.SAVINGS, retrievedAccount.getType());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    void testFindAccountByUser() {
        Account account1 = Account.builder()
                .user(user)
                .type(AccountType.CHECKING)
                .build();

        Account account2 = Account.builder()
                .user(user)
                .type(AccountType.SAVINGS)
                .build();

        try (Session session = HibernateConfig.getFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            AccountRepository.save(account1);
            AccountRepository.save(account2);
            transaction.commit();

            // Find accounts by user
            var accounts = AccountRepository.findByUser(user.getId());
            assertNotNull(accounts);
            assertEquals(2, accounts.size());
            assertTrue(accounts.contains(account1));
            assertTrue(accounts.contains(account2));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    void testUniqueAccountTypePerUser() {
        Account account1 = Account.builder()
                .user(user)
                .type(AccountType.CHECKING)
                .build();

        Account account2 = Account.builder()
                .user(user)
                .type(AccountType.CHECKING)
                .build();

        try (Session session = HibernateConfig.getFactory().openSession()) {
            Transaction first = session.beginTransaction();
            AccountRepository.save(account1);
            first.commit();

            assertThrows(Exception.class, () -> {
                Transaction second = session.beginTransaction();
                AccountRepository.save(account2);
                second.commit();
            });
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getMessage().contains("unique constraint violation"));
        }
    }

    @Test
    void testDeleteAccount() {
        Account account = Account.builder()
                .user(user)
                .type(AccountType.CHECKING)
                .build();

        try (Session session = HibernateConfig.getFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            AccountRepository.save(account);
            transaction.commit();

            assertNotNull(account.getId());

            // Delete the account
            AccountRepository.delete(account.getId());

            Account deletedAccount = AccountRepository.find(account.getId());
            assertNull(deletedAccount);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    void testSaveOrUpdateAccount() {
        Account account = Account.builder()
                .user(user)
                .type(AccountType.CHECKING)
                .build();

        try (Session session = HibernateConfig.getFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            AccountRepository.save(account);
            transaction.commit();

            Account other = Account.builder()
                    .user(user)
                    .type(AccountType.CHECKING)
                    .build();

            // Save again (this should be an update now)
            transaction = session.beginTransaction();
            AccountRepository.save(other);
            transaction.commit();

            // Retrieve and verify update
            Account updatedAccount = AccountRepository.find(account.getId());
            assertNotNull(updatedAccount);
            assertEquals(AccountType.SAVINGS, updatedAccount.getType());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed due to exception: " + e.getMessage());
        }
    }
}
