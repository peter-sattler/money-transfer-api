package net.sattler22.transfer.dto;

import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.jcip.annotations.Immutable;
import net.sattler22.transfer.domain.AccountType;

/**
 * Account Data Transfer Object (DTO)
 *
 * @author Pete Sattler
 * @version April 2020
 */
@Immutable
public final class AccountDTO {

    private final AccountType type;
    private final String customerId;
    private final BigDecimal balance;

    /**
     * Constructs a new account DTO
     */
    @JsonCreator(mode=Mode.PROPERTIES)
    public AccountDTO(@JsonProperty("type") AccountType type,
                      @JsonProperty("customerId") String customerId,
                      @JsonProperty("balance") BigDecimal balance) {
        this.type = type;
        this.customerId = customerId;
        this.balance = balance;
    }

    public AccountType getType() {
        return type;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, customerId, balance);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        final AccountDTO that = (AccountDTO) other;
        if(!Objects.equals(type, that.type))
            return false;
        if(!Objects.equals(customerId, that.customerId))
            return false;
        return Objects.equals(balance, that.balance);
    }

    @Override
    public String toString() {
        return String.format("%s [type=%s, customerId=%s, balance=%s]",
                             getClass().getSimpleName(), type, customerId, balance);
    }
}
