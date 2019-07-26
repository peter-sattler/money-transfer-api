package net.sattler22.transfer.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import net.sattler22.transfer.util.TestDataFactory;

/**
 * Money Transfer Bank Business Object Unit Test Harness
 *
 * @author Pete Sattler
 * @version July 2019
 */
public class BankUnitTestHarness {

    private Bank bank;

    @Before
    public void setUp() throws Exception {
        this.bank = new Bank(1, "Bank Unit Test Harness");
    }

    @Test
    public void testAddCustomer() {
        final int customerId = 10;
        final Customer expected = TestDataFactory.getBob(customerId, true); 
        bank.addCustomer(expected);
        assertTrue(bank.isCustomer(expected));
        assertEquals(bank.getCustomers().size(), 1);
        assertEquals(expected, bank.findCustomer(customerId).get());
    }

    @Test
    public void testDeleteCustomer() {
        final int customerId = 20;
        final Customer expected = TestDataFactory.getEileen(customerId, false);
        bank.addCustomer(expected);
        assertTrue(bank.isCustomer(expected));
        assertEquals(bank.getCustomers().size(), 1);
        assertEquals(expected, bank.findCustomer(customerId).get());
        bank.deleteCustomer(expected);
        assertTrue(bank.getCustomers().isEmpty());
    }
}
