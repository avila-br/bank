package br.com.compass.bank.model;

import br.com.compass.bank.validation.PhoneNumber;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.br.CPF;

import java.util.List;

/**
 * Represents a User entity in the system.
 * A User can have multiple associated accounts.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "t_user")
public class User {

    /**
     * The unique identifier for the User.
     * This value is auto-generated and cannot be updated.
     */
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     * The CPF (Cadastro de Pessoas Físicas) of the User.
     * It must be unique, cannot be null or blank, and must follow the CPF format.
     */
    @Column(name = "cpf", unique = true, nullable = false, updatable = false)
    @NotBlank(message = "CPF cannot be blank.")
    @CPF(message = "Invalid CPF format. Use 000.000.000-00 or 00000000000.")
    private String cpf;

    /**
     * The phone number of the User.
     * It must be unique, cannot be null or blank, and must match the custom phone number pattern.
     */
    @Column(name = "phone", unique = true, nullable = false)
    @NotBlank(message = "Phone number cannot be blank.")
    @PhoneNumber
    private String phone;

    /**
     * The name of the User.
     * It cannot be null or blank and must be between 2 and 100 characters long.
     */
    @Column(name = "name", nullable = false)
    @NotBlank(message = "Name cannot be blank.")
    @Size (min = 2, max = 100, message = "Name must be between 2 and 100 characters long.")
    private String name;

    /**
     * The list of accounts associated with the User.
     * This is a one-to-many relationship, where each User can have multiple accounts.
     * Changes to the User's accounts are cascaded, and orphan accounts are automatically removed.
     */
    @OneToMany (
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Account> accounts;

}
