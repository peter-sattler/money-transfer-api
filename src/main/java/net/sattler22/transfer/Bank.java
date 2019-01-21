package net.sattler22.transfer;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Revolut&copy; Banking Institution
 * 
 * @author Pete Sattler
 * @version January 2019
 * @implSpec This class is immutable and thread-safe
 */
public final class Bank implements Serializable {

    private static final long serialVersionUID = 8414304479231837140L;
    private final int id;
    private final String name;
    private final Set<Account> accounts = Collections.synchronizedSet(new HashSet<>());

    /**
     * Constructs a new banking institution
     */
    public Bank(int id, String name) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Bank name is required");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean addAccount(Account account) {
        return accounts.add(account);
    }

    public Set<Account> getAccounts() {
        return new HashSet<>(accounts);  //Defensive copy
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
        final Bank that = (Bank) other;
        return this.id == that.id;
    }

    @Override
    public String toString() {
        return String.format("Bank [id=%s, name=%s, accounts=%s]", id, name, accounts);
    }
}
