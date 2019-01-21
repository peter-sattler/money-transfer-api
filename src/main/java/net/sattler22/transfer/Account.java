package net.sattler22.transfer;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Revolut&copy; Account
 * 
 * @author Pete Sattler
 * @version January 2019
 * @implSpec This class is immutable and thread-safe
 */
public final class Account implements Serializable {

    private static final long serialVersionUID = -9088851442796213109L;
    private final int number;
    private final int customerId;
    private final BigDecimal balance;

    /**
     * Constructs a new account
     */
    public Account(int number, int customerId, BigDecimal balance) {
        this.number = number;
        this.customerId = customerId;
        this.balance = (balance == null) ? BigDecimal.ZERO : balance;
    }

    public int getNumber() {
        return number;
    }

    public int getCustomerId() {
        return customerId;
    }

    public BigDecimal getBalance() {
        return balance;
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
        return String.format("Account [number=%s, customerId=%s, balance=%s]", number, customerId, balance);
    }
}
