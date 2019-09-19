package net.sattler22.transfer.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.jcip.annotations.Immutable;

/**
 * Account Transfer Data Transfer Object (DTO)
 *
 * @author Pete Sattler
 * @version August 2019
 */
@Immutable
public final class AccountTransferDTO implements Serializable {

    private static final long serialVersionUID = 1961253095079262545L;
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
        this.customerId = customerId;
        this.sourceNumber = sourceNumber;
        this.targetNumber = targetNumber;
        this.amount = amount;
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
        if (this.customerId == null || that.customerId == null)
            return false;
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
