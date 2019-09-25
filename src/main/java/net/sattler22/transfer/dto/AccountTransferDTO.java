package net.sattler22.transfer.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.jcip.annotations.Immutable;
import net.sattler22.transfer.domain.Account;

/**
 * Account Transfer Data Transfer Object (DTO)
 *
 * @author Pete Sattler
 * @version September 2019
 */
@Immutable
public final class AccountTransferDTO implements Serializable {

    private static final long serialVersionUID = 3578568675589785635L;
    private final String customerId;
    private final int sourceNumber;
    private final int targetNumber;
    private final BigDecimal amount;
    private final long lastModified;

    /**
     * Constructs a new account transfer DTO
     */
    public AccountTransferDTO(String customerId, Account sourceAccount, Account targetAccount, BigDecimal amount) {
        this(customerId, sourceAccount.getNumber(), targetAccount.getNumber(), amount,
             initLastModified(sourceAccount, targetAccount).getTime());
    }

    /**
     * Find the most recent modification date
     */
    private static Date initLastModified(Account sourceAccount, Account targetAccount) {
        if(sourceAccount.getLastModified().after(targetAccount.getLastModified()))
            return sourceAccount.getLastModified();
        return targetAccount.getLastModified();
    }

    /**
     * Reconstructs an existing account transfer DTO
     */
    @JsonCreator(mode = Mode.PROPERTIES)
    private AccountTransferDTO(@JsonProperty("customerId") String customerId,
                               @JsonProperty("sourceNumber") int sourceNumber,
                               @JsonProperty("targetNumber") int targetNumber,
                               @JsonProperty("amount") BigDecimal amount,
                               @JsonProperty("lastModified") long lastModified) {
        this.customerId = customerId;
        this.sourceNumber = sourceNumber;
        this.targetNumber = targetNumber;
        this.amount = amount;
        this.lastModified = lastModified;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getSourceNumber() {
        return sourceNumber;
    }

    public int getTargetNumber() {
        return targetNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Date getLastModified() {
        return new Date(lastModified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, sourceNumber, targetNumber, amount, lastModified);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        final AccountTransferDTO that = (AccountTransferDTO) other;
        if (this.customerId == null || that.customerId == null)
            return false;
        if (!this.customerId.equals(that.customerId))
            return false;
        if (this.sourceNumber != that.sourceNumber)
            return false;
        if (this.targetNumber != that.targetNumber)
            return false;
        if (this.amount.compareTo(that.amount) != 0)
            return false;
        return this.lastModified == that.lastModified;
    }

    @Override
    public String toString() {
        return String.format("%s [customerId=%s, sourceNumber=%s, targetNumber=%s, amount=%s, lastModified=%s]",
                             getClass().getSimpleName(), customerId, sourceNumber, targetNumber, amount, lastModified);
    }
}
