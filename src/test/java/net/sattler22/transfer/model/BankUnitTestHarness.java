package net.sattler22.transfer.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import net.sattler22.transfer.domain.Bank;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.util.TestDataFactory;

/**
 * Money Transfer Bank Business Object Unit Test Harness
 *
 * @author Pete Sattler
 * @version September 2019
 */
public class BankUnitTestHarness {

    private Bank bank;

    @Before
    public void setUp() throws Exception {
        this.bank = new Bank(1, "Bank Unit Test Harness");
    }

    @Test
    public void testAddCustomer() {
        final String customerId = "AAA-10";
        final Customer expected = TestDataFactory.getBob(customerId);
        bank.addCustomer(expected);
        assertTrue(bank.isCustomer(expected));
        assertEquals(bank.getCustomers().size(), 1);
        assertEquals(expected, bank.findCustomer(customerId).get());
    }

    @Test
    public void testDeleteCustomer() {
        final String customerId = "BBB-20";
        final Customer expected = TestDataFactory.getEileen(customerId);
        bank.addCustomer(expected);
        assertTrue(bank.isCustomer(expected));
        assertEquals(bank.getCustomers().size(), 1);
        assertEquals(expected, bank.findCustomer(customerId).get());
        bank.deleteCustomer(expected);
        assertTrue(bank.getCustomers().isEmpty());
    }
}
