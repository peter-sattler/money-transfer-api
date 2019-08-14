package net.sattler22.transfer.model;

import static java.math.BigDecimal.ZERO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account Business Object
 *
 * @author Pete Sattler
 * @version August 2019
 */
@JsonIgnoreProperties({ "lock" })
public final class Account implements Serializable {

    private static final long serialVersionUID = -5230064948832981890L;
    private final int number;
    private final AccountType type;
    @JsonManagedReference
    private final Customer owner;
    private volatile BigDecimal balance;
    private final Object lock = new Object();

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
        this.balance = (balance == null) ? ZERO : balance;
    }

    /**
     * Credit funds to the account
     */
    public void credit(BigDecimal amount) {
        if (amount.compareTo(ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");
        synchronized (lock) {
            balance = balance.add(amount);
        }
    }

    /**
     * Debit funds from the account
     */
    public void debit(BigDecimal amount) {
        synchronized (lock) {
            final BigDecimal newBalance = balance.subtract(amount);
            if (newBalance.compareTo(ZERO) < 0)
                throw new IllegalStateException("Transaction would lead to an overdrawn account");
            this.balance = newBalance;
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
