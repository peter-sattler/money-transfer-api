package net.sattler22.transfer.service;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static net.sattler22.transfer.domain.AccountType.CHECKING;
import static net.sattler22.transfer.domain.AccountType.SAVINGS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.domain.Bank;
import net.sattler22.transfer.util.TestDataFactory;

/**
 * Money Transfer Service In-Memory Implementation Unit Test Harness
 *
 * @author Pete Sattler
 * @version February 2019
 */
final class TransferServiceInMemoryUnitTest {

    private TransferService transferService;

    @BeforeEach
    void beforeEach() {
        final var bank = new Bank(1, "Transfer Service In-Memory Unit Test Harness Bank");
        bank.addCustomer(TestDataFactory.bob("111"));
        this.transferService = new TransferServiceInMemoryImpl(bank);
    }

    @Test
    void deleteCustomerWithAccountsTestCase() {
        final var bob = transferService.getCustomers().iterator().next();
        final var account = new Account(CHECKING, bob, ONE);
        bob.addAccount(account);
        assertThrows(IllegalStateException.class, () -> {
            transferService.deleteCustomer(bob);
        });
    }

    @Test
    void deleteAccountWithNonZeroBalanceTestCase() {
        final var bob = transferService.getCustomers().iterator().next();
        final var account = new Account(CHECKING, bob, ONE);
        bob.addAccount(account);
        assertThrows(IllegalStateException.class, () -> {
            transferService.deleteAccount(account);
        });
    }

    @Test
    void transferZeroAmountTestCase() {
        final var bob = transferService.getCustomers().iterator().next();
        final var sourceAccount = new Account(CHECKING, bob, TEN);
        final var targetAccount = new Account(CHECKING, bob, TEN);
        bob.addAccount(sourceAccount);
        bob.addAccount(targetAccount);
        assertThrows(IllegalArgumentException.class, () -> {
            transferService.transfer(bob, sourceAccount, targetAccount, ZERO);
        });
    }

    @Test
    void transferHappyPathTestCase() {
        //Set-up accounts:
        final var bob = transferService.getCustomers().iterator().next();
        final var initialSourceAccountBalance = new BigDecimal(100);
        final var initialTargetAccountBalance = new BigDecimal(50);
        final var sourceAccount = new Account(SAVINGS, bob, initialSourceAccountBalance);
        final var targetAccount = new Account(CHECKING, bob, initialTargetAccountBalance);
        //Do the transfer:
        final var transferAmount = TEN;
        final var transferResult = transferService.transfer(bob, sourceAccount, targetAccount, transferAmount);
        //Check source account:
        final var expectedSourceAccountBalance = initialSourceAccountBalance.subtract(transferAmount);
        final var actualSourceAccountBalance = transferResult.source().balance();
        assertEquals(0, expectedSourceAccountBalance.compareTo(actualSourceAccountBalance));
        //Check target account:
        final var expectedTargetAccountBalance = initialTargetAccountBalance.add(transferAmount);
        final var actualTargetAccountBalance = transferResult.target().balance();
        assertEquals(0, expectedTargetAccountBalance.compareTo(actualTargetAccountBalance));
    }
}
