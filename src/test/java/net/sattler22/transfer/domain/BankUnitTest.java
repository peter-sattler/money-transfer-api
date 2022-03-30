package net.sattler22.transfer.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sattler22.transfer.util.TestDataFactory;

/**
 * Money Transfer Bank Business Object Unit Test Harness
 *
 * @author Pete Sattler
 * @version February 2019
 */
final class BankUnitTest {

    private Bank bank;

    @BeforeEach
    void beforeEach() {
        this.bank = new Bank(1, "Bank Unit Test Harness");
    }

    @Test
    void testAddCustomer() {
        final var customerId = "AAA-10";
        final var expected = TestDataFactory.bob(customerId);
        bank.addCustomer(expected);
        assertTrue(bank.isCustomer(expected));
        assertEquals(1, bank.customers().size());
        assertEquals(expected, bank.findCustomer(customerId).get());
    }

    @Test
    void testDeleteCustomer() {
        final var customerId = "BBB-20";
        final var expected = TestDataFactory.eileen(customerId);
        bank.addCustomer(expected);
        assertTrue(bank.isCustomer(expected));
        assertEquals(1, bank.customers().size());
        assertEquals(expected, bank.findCustomer(customerId).get());
        bank.deleteCustomer(expected);
        assertTrue(bank.customers().isEmpty());
    }
}
