package net.sattler22.transfer.dto;

import java.io.Serializable;
import java.math.BigDecimal;
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

    private static final long serialVersionUID = -2226466401398216991L;
    private final String customerId;
    private final int sourceNumber;
    private final int targetNumber;
    private final BigDecimal amount;

    /**
     * Constructs a new account transfer DTO
     */
    @JsonCreator(mode = Mode.PROPERTIES)
    public AccountTransferDTO(@JsonProperty("customerId") String customerId,
                              @JsonProperty("sourceNumber") int sourceNumber,
                              @JsonProperty("targetNumber") int targetNumber,
                              @JsonProperty("amount") BigDecimal amount) {
        if (customerId == null || customerId.trim().isEmpty())
            throw new IllegalArgumentException("Customer ID is required");
        this.customerId = customerId.trim();
        this.sourceNumber = sourceNumber;
        this.targetNumber = targetNumber;
        this.amount = Objects.requireNonNull(amount, "Transfer amount is required");
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

    /**
     * Create a unique account transfer version
     *
     * @param sourceAccount The underlying source account
     * @param targetAccount The underlying target account
     *
     * @return An account transfer version string in the format:
     *        {sourceAccount#-sourceAccountVersion#-targetAccount#-targetAccountVersion#}
     */
    public static String createVersion(Account sourceAccount, Account targetAccount) {
        return String.format("%d-%d-%d-%d", sourceAccount.getNumber(), sourceAccount.getVersion(),
                                            targetAccount.getNumber(), targetAccount.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, sourceNumber, targetNumber, amount);
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
        if (!this.customerId.equals(that.customerId))
            return false;
        if (this.sourceNumber != that.sourceNumber)
            return false;
        if (this.targetNumber != that.targetNumber)
            return false;
        return this.amount.compareTo(that.amount) == 0;
    }

    @Override
    public String toString() {
        return String.format("%s [customerId=%s, sourceNumber=%s, targetNumber=%s, amount=%s]",
                             getClass().getSimpleName(), customerId, sourceNumber, targetNumber, amount);
    }
}
