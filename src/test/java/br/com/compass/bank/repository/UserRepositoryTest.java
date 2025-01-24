package br.com.compass.bank.repository;

import br.com.compass.bank.config.HibernateConfig;
import br.com.compass.bank.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    @BeforeAll
    static void setup() {
        // ...
    }

    @AfterAll
    static void tearDown() {
        HibernateConfig.shutdown();
    }

    @Test
    void testCreateUser() {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getFactory().openSession()) {
            transaction = session.beginTransaction();

            User user = User.builder()
                    .name("John Doe")
                    .cpf("123.456.789-00")
                    .phone("+55 77 98802-8746")
                    .build();

            session.persist(user);
            transaction.commit();

            assertNotNull(user.getId());
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();

            e.printStackTrace();
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    void testFindUser() {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getFactory().openSession()) {
            transaction = session.beginTransaction();

            User user = User.builder()
                    .name("John Doe")
                    .cpf("123.456.789-00")
                    .phone("+55 77 98802-8746")
                    .build();

            session.persist(user);
            transaction.commit();

            User retrieved = session.get(User.class, user.getId());
            assertNotNull(retrieved);
            assertEquals("John Doe", retrieved.getName());
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();

            e.printStackTrace();
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

}
