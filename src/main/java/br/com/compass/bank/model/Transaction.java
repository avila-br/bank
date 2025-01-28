package br.com.compass.bank.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a transaction between two accounts in the system.
 * <p>
 * This class is a JPA entity that is mapped to the "t_transaction" table in the database.
 * Each transaction includes details about the sender, receiver, transaction type, amount, and timestamp.
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "t_transaction")
public class Transaction {

    /**
     * The unique identifier for the transaction.
     * <p>
     * This is the primary key for the transaction table.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private Long id;

    /**
     * The account from which the money is being sent.
     * <p>
     * This field establishes a many-to-one relationship with the {@link Account} entity.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "sender")
    private Account sender;

    /**
     * The account to which the money is being received.
     * <p>
     * This field establishes a many-to-one relationship with the {@link Account} entity.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "receiver")
    private Account receiver;

    /**
     * The type of the transaction (e.g., deposit, withdraw, transfer).
     * <p>
     * This field is represented by the {@link TransactionType} enum.
     * </p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    /**
     * The amount of money involved in the transaction.
     * <p>
     * This field represents the transaction amount and must be greater than zero for valid transactions.
     * </p>
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * The timestamp when the transaction occurred.
     * <p>
     * The default value is the current timestamp when the transaction is created.
     * </p>
     */
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

}
