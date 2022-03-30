package net.sattler22.transfer.api;

import java.math.BigDecimal;
import java.util.Objects;

import net.sattler22.transfer.domain.Account;

/**
 * Account Transfer Data Transfer Object (DTO)
 *
 * @author Pete Sattler
 * @version September 2019
 */
public record AccountTransferDto(String customerId, int sourceNumber, int targetNumber, BigDecimal amount) {

    /**
     * Constructs a new account transfer DTO
     */
    public AccountTransferDto(String customerId, int sourceNumber, int targetNumber, BigDecimal amount) {
        if (customerId == null || customerId.trim().isEmpty())
            throw new IllegalArgumentException("Customer ID is required");
        this.customerId = customerId.trim();
        this.sourceNumber = sourceNumber;
        this.targetNumber = targetNumber;
        this.amount = Objects.requireNonNull(amount, "Transfer amount is required");
    }

    /**
     * Create a unique account transfer version
     *
     * @param sourceAccount The underlying source account
     * @param targetAccount The underlying target account
     *
     * @return An account transfer version string in the format: {sourceAccount#-sourceAccountVersion#-targetAccount#-targetAccountVersion#}
     */
    public static String createVersion(Account sourceAccount, Account targetAccount) {
        return String.format("%d-%d-%d-%d", sourceAccount.number(), sourceAccount.version(), targetAccount.number(), targetAccount.version());
    }
}
