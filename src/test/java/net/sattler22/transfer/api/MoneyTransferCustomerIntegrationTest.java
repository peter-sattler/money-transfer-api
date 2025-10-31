package net.sattler22.transfer.api;

import jakarta.ws.rs.core.Response.Status;
import net.sattler22.transfer.domain.AccountType;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.util.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Money Transfer Customer Integration Tests
 *
 * @author Pete Sattler
 * @version November 2025
 * @since September 2019
 */
final class MoneyTransferCustomerIntegrationTest extends MoneyTransferBaseTest {

    @Nested
    @DisplayName("Get Customer")
    final class GetTest {
        @Test
        void getAllCustomersHappyPathTestCase() {
            final Customer bobWire = addCustomer(TestData.bobWire("123"));
            final Customer burtRentals = addCustomer(TestData.burtRentals("234"));
            final Customer eileenDover = addCustomer(TestData.eileenDover("789"));
            final Set<Customer> actual = getAllCustomers();
            assertEquals(3, actual.size());
            assertEquals(Set.of(bobWire, burtRentals, eileenDover), actual);
        }

        @Test
        void getOneCustomerHappyPathTestCase() {
            final Customer bobWire = addCustomer(TestData.bobWire("123"));
            final Customer actual = getCustomer(bobWire, Status.OK);
            assertNotNull(actual);
            assertEquals(bobWire, actual);
        }

        @Test
        void getOneCustomerNotFoundTestCase() {
            final Customer bobWire = TestData.bobWire("123");
            final Customer actual = getCustomer(bobWire, Status.NOT_FOUND);
            assertNull(actual);
        }
    }

    @Nested
    @DisplayName("Add Customer")
    final class AddTest {
        @Test
        void addCustomerHappyPathTestCase() {
            final Customer bobWire = TestData.bobWire("123");
            final Customer actual = addCustomer(bobWire);
            assertEquals(bobWire, actual);
        }

        @Test
        void addCustomerAlreadyExistsTestCase() {
            final Customer bobWire = TestData.bobWire("123");
            final Customer actual = addCustomer(bobWire);
            addCustomer(bobWire, Status.CONFLICT);
            assertEquals(bobWire, actual);
        }
    }

    @Nested
    @DisplayName("Delete Customer")
    final class DeleteTest {
        @Test
        void deleteCustomerHappyPathTestCase() {
            final Customer bobWire = addCustomer(TestData.bobWire("123"));
            deleteCustomer(bobWire, Status.NO_CONTENT);
            final Customer actual = getCustomer(bobWire, Status.NOT_FOUND);
            assertNull(actual);
        }

        @Test
        void deleteCustomerNotFoundTestCase() {
            final Customer bobWire = TestData.bobWire("123");
            deleteCustomer(bobWire, Status.NOT_FOUND);
            final Customer actual = getCustomer(bobWire, Status.NOT_FOUND);
            assertNull(actual);
        }

        @Test
        void deleteCustomerHasAccountsTestCase() {
            final Customer burtRentals = addCustomer(TestData.burtRentals("234"));
            addAccount(burtRentals, AccountType.CHECKING, BigDecimal.ONE);
            deleteCustomer(burtRentals, Status.CONFLICT);
            final Customer actual = getCustomer(burtRentals, Status.OK);
            assertNotNull(actual);
        }
    }
}
