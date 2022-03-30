package net.sattler22.transfer.api;

import java.math.BigDecimal;

import net.sattler22.transfer.domain.AccountType;

/**
 * Account Data Transfer Object (DTO)
 *
 * @author Pete Sattler
 * @version September 2019
 */
public record AccountDto(AccountType type, String customerId, BigDecimal balance) {
}
