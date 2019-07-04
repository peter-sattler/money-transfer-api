package net.sattler22.transfer.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.jcip.annotations.Immutable;

/**
 * Revolut Account Data Transfer Object (DTO)
 *
 * @author Pete Sattler
 * @version July 2019
 */
@Immutable
public final class AccountDTO implements Serializable {

    private static final long serialVersionUID = 8064618198189338330L;
    private final int number;
    private final int customerId;
    private final BigDecimal balance;

    /**
     * Constructs a new account DTO
     */
    @JsonCreator(mode=Mode.PROPERTIES)
    public AccountDTO(@JsonProperty("number") int number,
                      @JsonProperty("customerId") int customerId,
                      @JsonProperty("balance") BigDecimal balance) {
        this.number = number;
        this.customerId = customerId;
        this.balance = balance;
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
        final AccountDTO that = (AccountDTO) other;
        return this.number == that.number;
    }

    @Override
    public String toString() {
        return String.format("AccountDTO [number=%s, customerId=%s, balance=%s]", number, customerId, balance);
    }
}