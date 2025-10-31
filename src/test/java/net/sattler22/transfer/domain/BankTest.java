package net.sattler22.transfer.domain;

import net.sattler22.transfer.util.TestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Money Transfer Bank Business Object Unit Tests
 *
 * @author Pete Sattler
 * @version November 2025
 * @since February 2019
 */
final class BankTest {

    @Test
    void testAddCustomer() {
        final Bank bank = new Bank(10, "One Bank, Two Bank, Red Bank, Blue Bank");
        final String customerId = "OTRB-10";
        final Customer expected = TestData.bobWire(customerId);
        bank.addCustomer(expected);
        assertTrue(bank.isCustomer(expected));
        assertEquals(1, bank.customers().size());
        assertEquals(expected, bank.findCustomer(customerId).orElseThrow(IllegalStateException::new));
    }

    @Test
    void testDeleteCustomer() {
        final Bank bank = new Bank(20, "Green Bank and Ham");
        final String customerId = "GB-20";
        final Customer expected = TestData.eileenDover(customerId);
        bank.addCustomer(expected);
        assertTrue(bank.isCustomer(expected));
        assertEquals(1, bank.customers().size());
        assertEquals(expected, bank.findCustomer(customerId).orElseThrow(IllegalStateException::new));
        bank.deleteCustomer(expected);
        assertTrue(bank.customers().isEmpty());
    }
}
