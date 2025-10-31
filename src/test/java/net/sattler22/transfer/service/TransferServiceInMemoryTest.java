package net.sattler22.transfer.service;

import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.domain.AccountType;
import net.sattler22.transfer.domain.Bank;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.service.TransferService.TransferResult;
import net.sattler22.transfer.util.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Money Transfer Service (In-Memory) Unit Tests
 *
 * @author Pete Sattler
 * @version November 2025
 * @since February 2019
 */
final class TransferServiceInMemoryTest {

    private TransferService transferService;

    @BeforeEach
    void beforeEach() {
        final Bank bank = new Bank(1, "Transfer Service Test Bank");
        bank.addCustomer(TestData.bobWire("123"));
        this.transferService = new TransferServiceInMemoryImpl(bank);
    }

    @Test
    void deleteCustomerWithAccountsTestCase() {
        final Customer bobWire = transferService.getCustomers().iterator().next();
        final Account account = new Account(bobWire, AccountType.CHECKING, BigDecimal.ONE);
        bobWire.addAccount(account);
        assertThrows(IllegalStateException.class, () ->
            transferService.deleteCustomer(bobWire)
        );
    }

    @Test
    void deleteAccountWithNonZeroBalanceTestCase() {
        final Customer bobWire = transferService.getCustomers().iterator().next();
        final Account account = new Account(bobWire, AccountType.CHECKING, BigDecimal.ONE);
        bobWire.addAccount(account);
        assertThrows(IllegalStateException.class, () ->
            transferService.deleteAccount(account)
        );
    }

    @Test
    void transferZeroAmountTestCase() {
        final Customer bobWire = transferService.getCustomers().iterator().next();
        final Account sourceAccount = new Account(bobWire, AccountType.CHECKING, BigDecimal.TEN);
        final Account targetAccount = new Account(bobWire, AccountType.CHECKING, BigDecimal.TEN);
        bobWire.addAccount(sourceAccount);
        bobWire.addAccount(targetAccount);
        assertThrows(IllegalArgumentException.class, () ->
            transferService.transfer(bobWire, sourceAccount, targetAccount, BigDecimal.ZERO)
        );
    }

    @Test
    void transferHappyPathTestCase() {
        //Set-up accounts:
        final Customer bobWire = transferService.getCustomers().iterator().next();
        final BigDecimal initialSourceAccountBalance = new BigDecimal(100);
        final BigDecimal initialTargetAccountBalance = new BigDecimal(50);
        final Account sourceAccount = new Account(bobWire, AccountType.SAVINGS, initialSourceAccountBalance);
        final Account targetAccount = new Account(bobWire, AccountType.CHECKING, initialTargetAccountBalance);
        //Do the transfer:
        final BigDecimal transferAmount = BigDecimal.TEN;
        final TransferResult transferResult =
                transferService.transfer(bobWire, sourceAccount, targetAccount, transferAmount);
        //Check source account:
        final BigDecimal expectedSourceAccountBalance = initialSourceAccountBalance.subtract(transferAmount);
        final BigDecimal actualSourceAccountBalance = transferResult.source().balance();
        assertEquals(0, expectedSourceAccountBalance.compareTo(actualSourceAccountBalance));
        //Check target account:
        final BigDecimal expectedTargetAccountBalance = initialTargetAccountBalance.add(transferAmount);
        final BigDecimal actualTargetAccountBalance = transferResult.target().balance();
        assertEquals(0, expectedTargetAccountBalance.compareTo(actualTargetAccountBalance));
    }
}
