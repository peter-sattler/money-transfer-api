package net.sattler22.transfer.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Revolut Money Transfer Bank Business Object Unit Test Harness
 *
 * @author Pete Sattler
 * @version January 2019
 */
public class BankUnitTestHarness {

    private Bank bank;

    @Before
    public void setUp() throws Exception {
        this.bank = new Bank(1, "Bank Unit Test Harness");
    }

    @Test
    public void testAddCustomer() {
        final int customerId = 1;
        final Customer expected = new Customer(customerId, "Bob", "Wire");
        bank.addCustomer(expected);
        assertTrue(bank.isCustomer(expected));
        assertEquals(bank.getCustomers().size(), 1);
        assertEquals(expected, bank.findCustomer(customerId).get());
    }

    @Test
    public void testDeleteCustomer() {
        final int customerId = 1;
        final Customer expected = new Customer(customerId, "Eileen", "Dover");
        bank.addCustomer(expected);
        assertTrue(bank.isCustomer(expected));
        assertEquals(bank.getCustomers().size(), 1);
        assertEquals(expected, bank.findCustomer(customerId).get());
        bank.deleteCustomer(expected);
        assertTrue(bank.getCustomers().isEmpty());
    }
}
