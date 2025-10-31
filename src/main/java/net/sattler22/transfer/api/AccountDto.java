package net.sattler22.transfer.api;

import net.sattler22.transfer.domain.AccountType;

import java.math.BigDecimal;

/**
 * Account Data Transfer Object (DTO)
 *
 * @author Pete Sattler
 * @version November 2025
 * @since September 2019
 */
public record AccountDto(String customerId, AccountType type, BigDecimal balance) {
}
