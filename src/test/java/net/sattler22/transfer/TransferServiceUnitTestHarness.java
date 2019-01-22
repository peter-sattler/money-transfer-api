package net.sattler22.transfer;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import net.sattler22.transfer.TransferService.TransferResult;

/**
 * Revolut&copy; Money Transfer Service Unit Test Harness
 *
 * @author Pete Sattler
 * @version January 2019
 */
public class TransferServiceUnitTestHarness {

    private static final Customer EVIL_CUSTOMER1 = new Customer("Evil", "Hacker1");
    private static final Customer EVIL_CUSTOMER2 = new Customer("Evil", "Hacker2");
    private TransferService transferService;

    @Before
    public void setUp() throws Exception {
        final Bank bank = new Bank("Show Me the Money Bank");
        bank.addCustomer(new Customer("Barb", "Wire"));
        bank.addCustomer(new Customer("Burt", "Rentals"));
        bank.addCustomer(new Customer("Harry", "Pottery"));
        this.transferService = new TransferServiceInMemoryImpl(bank);
    }

    @Test(expected = IllegalStateException.class)
    public void testCheckBalanceNotACustomer() {
        final Account account = new Account(EVIL_CUSTOMER1, BigDecimal.TEN);
        transferService.checkBalance(account);
    }

    @Test
    public void testCheckBalanceHappyPath() {
        final BigDecimal expectedBalance = new BigDecimal(100);
        for (Customer owner : transferService.getBank().getCustomers()) {
            final Account account = new Account(owner, expectedBalance);
            final BigDecimal actualBalance = transferService.checkBalance(account);
            assertEquals(expectedBalance.compareTo(actualBalance), 0);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testTransferNotACustomer() {
        final Account sourceAccount = new Account(EVIL_CUSTOMER1, BigDecimal.TEN);
        final Account targetAccount = new Account(EVIL_CUSTOMER2, BigDecimal.TEN);
        transferService.transfer(sourceAccount, targetAccount, BigDecimal.TEN);
    }

    @Test
    public void testTransferHappyPath() {
        final BigDecimal amount = BigDecimal.TEN;
        final BigDecimal initialSourceAccountBalance = new BigDecimal(100);
        final BigDecimal initialTargetAccountBalance = new BigDecimal(50);
        final BigDecimal expectedSourceAccountBalance = initialSourceAccountBalance.subtract(amount);
        final BigDecimal expectedTargetAccountBalance = initialTargetAccountBalance.add(amount);
        for (Customer owner : transferService.getBank().getCustomers()) {
            final Account sourceAccount = new Account(owner, initialSourceAccountBalance);
            final Account targetAccount = new Account(owner, initialTargetAccountBalance);
            final TransferResult transferResult = transferService.transfer(sourceAccount, targetAccount, amount);
            final BigDecimal actualSourceAccountBalance = transferResult.getSourceAccount().getBalance();
            final BigDecimal actualTargetAccountBalance = transferResult.getTargetAccount().getBalance();
            assertEquals(expectedSourceAccountBalance.compareTo(actualSourceAccountBalance), 0);
            assertEquals(expectedTargetAccountBalance.compareTo(actualTargetAccountBalance), 0);
        }
    }
}
