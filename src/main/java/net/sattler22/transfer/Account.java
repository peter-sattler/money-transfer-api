package net.sattler22.transfer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Revolut&copy; Account
 *
 * @author Pete Sattler
 * @version January 2019
 * @implSpec This class is immutable and thread-safe
 */
public final class Account implements Serializable {

    private static final long serialVersionUID = -9088851442796213109L;
    private static final AtomicInteger counter = new AtomicInteger(0);
    private final int number;
    private final Customer owner;
    private final BigDecimal balance;
    private final Object lockObject = new Object();

    /**
     * Constructs a new account
     */
    public Account(Customer customer, BigDecimal balance) {
        this(counter.incrementAndGet(), customer, balance);
    }

    private Account(int number, Customer owner, BigDecimal balance) {
        this.number = number;
        this.owner = owner;
        this.balance = (balance == null) ? BigDecimal.ZERO : balance;
    }

    /**
     * Credit funds to the account
     */
    public Account credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");
        synchronized (lockObject) {
            return new Account(number, owner, balance.add(amount));
        }
    }

    /**
     * Debit funds from the account
     */
    public Account debit(BigDecimal amount) {
        synchronized (lockObject) {
            final BigDecimal newBalance = balance.subtract(amount);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalStateException("Transaction would lead to an overdrawn account");
            return new Account(number, owner, newBalance);
        }
    }

    public int getNumber() {
        return number;
    }

    public Customer getOwner() {
        return owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, owner, balance);
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
        if (this.number == that.number)
            return true;
        if (Objects.equals(this.owner, that.owner))
            return true;
        return Objects.equals(this.balance, that.balance);
    }

    @Override
    public String toString() {
        return String.format("Account [number=%s, owner=%s, balance=%s]", number, owner, balance);
    }
}
