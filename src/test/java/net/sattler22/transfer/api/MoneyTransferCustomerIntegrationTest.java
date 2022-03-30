package net.sattler22.transfer.api;

import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.math.BigDecimal.ONE;
import static net.sattler22.transfer.domain.AccountType.CHECKING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response.Status;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.util.TestDataFactory;

/**
 * Money Transfer Customer Integration Test Harness
 *
 * @author Pete Sattler
 * @version September 2019
 */
final class MoneyTransferCustomerIntegrationTest extends MoneyTransferBaseTestHarness {

    @Test
    void fetchAllCustomersHappyPathTestCase() {
        final var bob = TestDataFactory.bob("123");
        addCustomerImpl(bob);
        final var burt = TestDataFactory.burt("234");
        addCustomerImpl(burt);
        final var response = target(basePath).path("customers").request().get();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));
        final Set<Customer> actual = response.readEntity(new GenericType<Set<Customer>>() {});
        final Set<Customer> expected = new HashSet<>();
        expected.add(bob);
        expected.add(burt);
        assertEquals(expected, actual);
    }

    @Test
    void fetchOneCustomerHappyPathTestCase() {
        final var bob = TestDataFactory.bob("123");
        addCustomerImpl(bob);
        final var response = target(basePath).path("customer").path(bob.id()).request().get();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));
        final var actual = response.readEntity(Customer.class);
        assertEquals(bob, actual);
    }

    @Test
    void fetchOneCustomerNotFoundTestCase() {
        final var response = target(basePath).path("customer").path("0").request().get();
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    void addCustomerHappyPathTestCase() {
        final var response = addCustomerImpl(TestDataFactory.bob("123"));
        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    void addCustomerAlreadyExistsTestCase() {
        final var bob = TestDataFactory.bob("123");
        addCustomerImpl(bob);
        final var response = addCustomerImpl(bob);
        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    void deleteCustomerHappyPathTestCase() {
        final var bob = TestDataFactory.bob("123");
        addCustomerImpl(bob);
        final var response = deleteCustomerImpl(bob);
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    void deleteCustomerNotFoundTestCase() {
        final var response = deleteCustomerImpl(TestDataFactory.bob("123"));
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void deleteCustomerHasAccountsTestCase() {
        final var burt = TestDataFactory.burt("234");
        addCustomerImpl(burt);
        addAccountResponseImpl(CHECKING, burt.id(), ONE);
        final var response = deleteCustomerImpl(burt);
        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }
}
