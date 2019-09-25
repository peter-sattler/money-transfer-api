package net.sattler22.transfer.api;

import static java.math.BigDecimal.ONE;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static net.sattler22.transfer.api.MoneyTransferConstants.API_BASE_PATH;
import static net.sattler22.transfer.domain.AccountType.CHECKING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.util.TestDataFactory;

/**
 * Money Transfer Customer Integration Test Harness
 *
 * @author Pete Sattler
 * @version September 2019
 */
public final class MoneyTransferCustomerIntegrationTestHarness extends MoneyTransferBaseTestHarness {

    @Test
    public void fetchAllCustomersHappyPathTestCase() {
        final Customer bob = TestDataFactory.getBob("123");
        addCustomerImpl(bob);
        final Customer burt = TestDataFactory.getBurt("234");
        addCustomerImpl(burt);
        final Response response = target(API_BASE_PATH).path("customers").request().get();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));
        final Set<Customer> actual = response.readEntity(new GenericType<Set<Customer>>() {});
        final Set<Customer> expected = new HashSet<>();
        expected.add(bob);
        expected.add(burt);
        assertEquals(expected, actual);
    }

    @Test
    public void fetchOneCustomerHappyPathTestCase() {
        final Customer bob = TestDataFactory.getBob("123");
        addCustomerImpl(bob);
        final Response response = target(API_BASE_PATH).path("customer").path(bob.getId()).request().get();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));
        final Customer actual = response.readEntity(Customer.class);
        assertEquals(bob, actual);
    }

    @Test
    public void fetchOneCustomerNotFoundTestCase() {
        final Response response = target(API_BASE_PATH).path("customer").path("0").request().get();
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    public void addCustomerHappyPathTestCase() {
        final Response response = addCustomerImpl(TestDataFactory.getBob("123"));
        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    public void addCustomerAlreadyExistsTestCase() {
        final Customer bob = TestDataFactory.getBob("123");
        addCustomerImpl(bob);
        final Response response = addCustomerImpl(bob);
        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    public void deleteCustomerHappyPathTestCase() {
        final Customer bob = TestDataFactory.getBob("123");
        addCustomerImpl(bob);
        final Response response = deleteCustomerImpl(bob);
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void deleteCustomerNotFoundTestCase() {
        final Response response = deleteCustomerImpl(TestDataFactory.getBob("123"));
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void deleteCustomerHasAccountsTestCase() {
        final Customer burt = TestDataFactory.getBurt("234");
        addCustomerImpl(burt);
        addAccountResponseImpl(CHECKING, burt.getId(), ONE);
        final Response response = deleteCustomerImpl(burt);
        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }
}
