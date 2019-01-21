package net.sattler22.transfer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Revolut&copy; Transaction
 * 
 * @author Pete Sattler
 * @version January 2019
 * @implSpec This class is immutable and thread-safe
 */
public final class Transaction implements Serializable {

    private static final long serialVersionUID = -5918933777931562017L;
    private final LocalDateTime dateTime;
    private final Account sourceAccount;
    private final Account targetAccount;
    private final BigDecimal amount;

    /**
     * Constructs a new transaction
     */
    public Transaction(LocalDateTime dateTime, Account sourceAccount, Account targetAccount, BigDecimal amount) {
        this.dateTime = Objects.requireNonNull(dateTime, "Transaction date/time is required");
        this.sourceAccount = Objects.requireNonNull(sourceAccount, "From account is required");
        this.targetAccount = Objects.requireNonNull(targetAccount, "To account is required");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        this.amount = amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, sourceAccount, targetAccount);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        final Transaction that = (Transaction) other;
        return Objects.equals(this.dateTime, that.dateTime) && Objects.equals(this.sourceAccount, that.sourceAccount) && Objects.equals(this.targetAccount, that.targetAccount);
    }

    @Override
    public String toString() {
        return String.format("Transaction [dateTime=%s, sourceAccount=%s, targetAccount=%s, amount=%s]", dateTime, sourceAccount, targetAccount, amount);
    }
}
