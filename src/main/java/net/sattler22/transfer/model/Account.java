package net.sattler22.transfer.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.jcip.annotations.Immutable;

/**
 * Account Business Object
 *
 * @author Pete Sattler
 * @version July 2019
 */
@Immutable
@JsonIgnoreProperties({ "lock" })
public final class Account implements Serializable {

    private static final long serialVersionUID = -5230064948832981890L;
    private final int number;
    private final AccountType type;
    private final Customer owner;
    private final BigDecimal balance;
    private final Object lock = new Object();

    /**
     * Constructs a new account with a ZERO balance
     */
    public Account(int number, AccountType type, Customer owner) {
        this(number, type, owner, null);
    }

    /**
     * Constructs a new account
     */
    @JsonCreator(mode=Mode.PROPERTIES)
    public Account(@JsonProperty("number") int number,
                   @JsonProperty("type") AccountType type,
                   @JsonProperty("owner") Customer owner,
                   @JsonProperty("balance") BigDecimal balance) {
        this.number = number;
        this.type = Objects.requireNonNull(type, "Account type is required");
        this.owner = Objects.requireNonNull(owner, "Account owner is required");
        this.balance = (balance == null) ? BigDecimal.ZERO : balance;
    }

    /**
     * Credit funds to the account
     */
    public Account credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");
        synchronized (lock) {
            return new Account(number, type, owner, balance.add(amount));
        }
    }

    /**
     * Debit funds from the account
     */
    public Account debit(BigDecimal amount) {
        synchronized (lock) {
            final BigDecimal newBalance = balance.subtract(amount);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalStateException("Transaction would lead to an overdrawn account");
            return new Account(number, type, owner, newBalance);
        }
    }

    public int getNumber() {
        return number;
    }

    public AccountType getType() {
        return type;
    }
    
    public Customer getOwner() {
        return owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Object getLock() {
        return lock;
    }

    public static Optional<Account> find(Customer owner, int number) {
        return owner.findAccount(number);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(number);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        final Account that = (Account) other;
        return this.number == that.number;
    }

    @Override
    public String toString() {
        return String.format("%s [number=%s, type=%s, owner=%s, balance=%s]", getClass().getSimpleName(), number, type, owner, balance);
    }
}
