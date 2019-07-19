package net.sattler22.transfer.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import net.sattler22.transfer.model.Account;
import net.sattler22.transfer.model.Bank;
import net.sattler22.transfer.model.Customer;
import net.sattler22.transfer.service.TransferService.TransferResult;

/**
 * Money Transfer Service In-Memory Implementation Unit Test Harness
 *
 * @author Pete Sattler
 * @version July 2019
 */
public class TransferServiceInMemoryUnitTestHarness {

    private TransferService transferService;

    @Before
    public void setUp() throws Exception {
        final Bank bank = new Bank(1, "Transfer Service In-Memory Unit Test Harness Bank");
        bank.addCustomer(new Customer(1, "Barb", "Wire"));
        bank.addCustomer(new Customer(2, "Burt", "Rentals"));
        bank.addCustomer(new Customer(3, "Hairy", "Pottery"));
        this.transferService = new TransferServiceInMemoryImpl(bank);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransferZeroAmount() {
        final Customer owner = transferService.getCustomers().iterator().next();
        final Account sourceAccount = new Account(1, owner, BigDecimal.TEN);
        final Account targetAccount = new Account(2, owner, BigDecimal.TEN);
        owner.addAccount(sourceAccount);
        owner.addAccount(targetAccount);
        transferService.transfer(owner, sourceAccount, targetAccount, BigDecimal.ZERO);
    }

    @Test
    public void testTransferHappyPath() {
        // Set-up accounts:
        final Customer owner = transferService.getCustomers().iterator().next();
        final BigDecimal initialSourceAccountBalance = new BigDecimal(100);
        final BigDecimal initialTargetAccountBalance = new BigDecimal(50);
        final Account sourceAccount = new Account(1, owner, initialSourceAccountBalance);
        final Account targetAccount = new Account(2, owner, initialTargetAccountBalance);

        // Do the transfer:
        final BigDecimal transferAmount = BigDecimal.TEN;
        final TransferResult transferResult = transferService.transfer(owner, sourceAccount, targetAccount, transferAmount);

        // Check source account:
        final BigDecimal expectedSourceAccountBalance = initialSourceAccountBalance.subtract(transferAmount);
        final BigDecimal actualSourceAccountBalance = transferResult.getSource().getBalance();
        assertEquals(expectedSourceAccountBalance.compareTo(actualSourceAccountBalance), 0);

        // Check target account:
        final BigDecimal expectedTargetAccountBalance = initialTargetAccountBalance.add(transferAmount);
        final BigDecimal actualTargetAccountBalance = transferResult.getTarget().getBalance();
        assertEquals(expectedTargetAccountBalance.compareTo(actualTargetAccountBalance), 0);
    }
}
