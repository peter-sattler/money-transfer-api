package net.sattler22.transfer.service;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static net.sattler22.transfer.domain.AccountType.CHECKING;
import static net.sattler22.transfer.domain.AccountType.SAVINGS;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.domain.Bank;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.service.TransferService.TransferResult;
import net.sattler22.transfer.util.TestDataFactory;

/**
 * Money Transfer Service In-Memory Implementation Unit Test Harness
 *
 * @author Pete Sattler
 * @version September 2019
 */
public class TransferServiceInMemoryUnitTestHarness {

    private TransferService transferService;

    @Before
    public void setUp() throws Exception {
        final Bank bank = new Bank(1, "Transfer Service In-Memory Unit Test Harness Bank");
        bank.addCustomer(TestDataFactory.getBob("111"));
        this.transferService = new TransferServiceInMemoryImpl(bank);
    }

    @Test(expected = IllegalStateException.class)
    public void deleteCustomerWithAccountsTestCase() {
        final Customer bob = transferService.getCustomers().iterator().next();
        final Account account = new Account(CHECKING, bob, ONE);
        bob.addAccount(account);
        transferService.deleteCustomer(bob);
    }

    @Test(expected = IllegalStateException.class)
    public void deleteAccountWithNonZeroBalanceTestCase() {
        final Customer bob = transferService.getCustomers().iterator().next();
        final Account account = new Account(CHECKING, bob, ONE);
        bob.addAccount(account);
        transferService.deleteAccount(account);
    }

    @Test(expected = IllegalArgumentException.class)
    public void transferZeroAmountTestCase() {
        final Customer bob = transferService.getCustomers().iterator().next();
        final Account sourceAccount = new Account(CHECKING, bob, TEN);
        final Account targetAccount = new Account(CHECKING, bob, TEN);
        bob.addAccount(sourceAccount);
        bob.addAccount(targetAccount);
        transferService.transfer(bob, sourceAccount, targetAccount, ZERO);
    }

    @Test
    public void transferHappyPathTestCase() {
        //Set-up accounts:
        final Customer bob = transferService.getCustomers().iterator().next();
        final BigDecimal initialSourceAccountBalance = new BigDecimal(100);
        final BigDecimal initialTargetAccountBalance = new BigDecimal(50);
        final Account sourceAccount = new Account(SAVINGS, bob, initialSourceAccountBalance);
        final Account targetAccount = new Account(CHECKING, bob, initialTargetAccountBalance);

        //Do the transfer:
        final BigDecimal transferAmount = TEN;
        final TransferResult transferResult = transferService.transfer(bob, sourceAccount, targetAccount, transferAmount);

        //Check source account:
        final BigDecimal expectedSourceAccountBalance = initialSourceAccountBalance.subtract(transferAmount);
        final BigDecimal actualSourceAccountBalance = transferResult.getSource().getBalance();
        assertEquals(expectedSourceAccountBalance.compareTo(actualSourceAccountBalance), 0);

        //sCheck target account:
        final BigDecimal expectedTargetAccountBalance = initialTargetAccountBalance.add(transferAmount);
        final BigDecimal actualTargetAccountBalance = transferResult.getTarget().getBalance();
        assertEquals(expectedTargetAccountBalance.compareTo(actualTargetAccountBalance), 0);
    }
}
