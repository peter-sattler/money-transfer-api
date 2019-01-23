package net.sattler22.transfer.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import net.sattler22.transfer.model.Account;
import net.sattler22.transfer.model.Bank;
import net.sattler22.transfer.model.Customer;
import net.sattler22.transfer.service.TransferService;
import net.sattler22.transfer.service.TransferService.TransferResult;
import net.sattler22.transfer.service.TransferServiceInMemoryImpl;

/**
 * Revolut&copy; Money Transfer Service Unit Test Harness
 *
 * @author Pete Sattler
 * @version January 2019
 */
public class TransferServiceUnitTestHarness {

    private static final Customer EVIL_HACKER1 = new Customer(Integer.MAX_VALUE, "Evil", "Hacker #1");
    private static final Customer EVIL_HACKER2 = new Customer(Integer.MAX_VALUE - 1, "Evil", "Hacker #2");
    private TransferService transferService;

    @Before
    public void setUp() throws Exception {
        final Bank bank = new Bank(1, "Show Me the Money Bank");
        bank.addCustomer(new Customer(1, "Barb", "Wire"));
        bank.addCustomer(new Customer(2, "Burt", "Rentals"));
        bank.addCustomer(new Customer(3, "Harry", "Pottery"));
        this.transferService = new TransferServiceInMemoryImpl(bank);
    }

    @Test(expected = IllegalStateException.class)
    public void testCheckBalanceNotACustomer() {
        final Account account = new Account(0, EVIL_HACKER1, BigDecimal.TEN);
        transferService.checkBalance(account);
    }

    @Test
    public void testCheckBalanceHappyPath() {
        final BigDecimal expectedBalance = new BigDecimal(100);
        final Customer owner = transferService.getBank().getCustomers().iterator().next();
        final Account account = new Account(0, owner, expectedBalance);
        final BigDecimal actualBalance = transferService.checkBalance(account);
        assertEquals(expectedBalance.compareTo(actualBalance), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransferZeroAmount() {
        final Iterator<Customer> ownerIterator = transferService.getBank().getCustomers().iterator();
        final Account sourceAccount = new Account(1, ownerIterator.next(), BigDecimal.TEN);
        final Account targetAccount = new Account(2, ownerIterator.next(), BigDecimal.TEN);
        transferService.transfer(sourceAccount, targetAccount, BigDecimal.ZERO);
    }

    @Test(expected = IllegalStateException.class)
    public void testTransferNotACustomer() {
        final Account sourceAccount = new Account(1, EVIL_HACKER1, BigDecimal.TEN);
        final Account targetAccount = new Account(2, EVIL_HACKER2, BigDecimal.TEN);
        transferService.transfer(sourceAccount, targetAccount, BigDecimal.TEN);
    }

    @Test
    public void testTransferHappyPath() {
        // Set-up accounts:
        final Iterator<Customer> ownerIterator = transferService.getBank().getCustomers().iterator();
        final BigDecimal initialSourceAccountBalance = new BigDecimal(100);
        final BigDecimal initialTargetAccountBalance = new BigDecimal(50);
        final Account sourceAccount = new Account(1, ownerIterator.next(), initialSourceAccountBalance);
        final Account targetAccount = new Account(2, ownerIterator.next(), initialTargetAccountBalance);

        // Do the transfer:
        final BigDecimal transferAmount = BigDecimal.TEN;
        final TransferResult transferResult = transferService.transfer(sourceAccount, targetAccount, transferAmount);

        // Check source account:
        final BigDecimal expectedSourceAccountBalance = initialSourceAccountBalance.subtract(transferAmount);
        final BigDecimal actualSourceAccountBalance = transferResult.getSourceAccount().getBalance();
        assertEquals(expectedSourceAccountBalance.compareTo(actualSourceAccountBalance), 0);

        // Check target account:
        final BigDecimal expectedTargetAccountBalance = initialTargetAccountBalance.add(transferAmount);
        final BigDecimal actualTargetAccountBalance = transferResult.getTargetAccount().getBalance();
        assertEquals(expectedTargetAccountBalance.compareTo(actualTargetAccountBalance), 0);
    }
}
