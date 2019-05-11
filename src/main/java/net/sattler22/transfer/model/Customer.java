package net.sattler22.transfer.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Revolut Customer Business Object
 *
 * @author Pete Sattler
 * @version January 2019
 * @implSpec This class is immutable and thread-safe
 */
public final class Customer implements Serializable {

    private static final long serialVersionUID = 5697946845021941295L;
    private final int id;
    private final String firstName;
    private final String lastName;
    private final Set<Account> accounts = Collections.synchronizedSet(new HashSet<>());

    /**
     * Constructs a new customer
     */
    public Customer(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = Objects.requireNonNull(firstName, "First name is required");
        this.lastName = Objects.requireNonNull(lastName, "Last name is required");
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    /**
     * Add a new account
     * 
     * @return True if the account was added. Otherwise, returns false when the account already exists.
     */
    public boolean addAccount(Account account) {
        return accounts.add(account);
    }

    /**
     * Delete an existing account
     * 
     * @return True if the account was deleted. Otherwise, returns false when the account is non-existent.
     */
    public boolean deleteAccount(Account account) {
        return accounts.remove(account);
    }

    /**
     * Get all of the customer's accounts
     */
    public Set<Account> getAccounts() {
        return Collections.unmodifiableSet(accounts);
    }

    /**
     * Find a specific account
     * 
     * @param number The account number
     */
    public Optional<Account> findAccount(int number) {
        return accounts.stream().filter(account -> account.getNumber() == number).findFirst();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        final Customer that = (Customer) other;
        return this.id == that.id;
    }

    @Override
    public String toString() {
        return String.format("Customer [id=%s, firstName=%s, lastName=%s]", id, firstName, lastName);
    }
}
