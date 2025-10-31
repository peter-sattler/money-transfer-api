package net.sattler22.transfer.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.domain.AccountType;
import net.sattler22.transfer.domain.Bank;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.service.TransferService;
import net.sattler22.transfer.service.TransferServiceInMemoryImpl;
import net.sattler22.transfer.util.PropertyFileUtils;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Money Transfer Test Base Class
 *
 * @author Pete Sattler
 * @version November 2025
 * @since September 2019
 */
abstract sealed class MoneyTransferBaseTest extends JerseyTest
        permits MoneyTransferBankIntegrationTest, MoneyTransferCustomerIntegrationTest,
                MoneyTransferAccountIntegrationTest, MoneyTransferConcurrencyTest  {

    protected final String basePath;
    protected final Bank bank;

    protected MoneyTransferBaseTest() {
        try {
            final Properties appProps =
                    PropertyFileUtils.readResourceProperties(getClass(), "app.properties");
            this.basePath = appProps.getProperty("base.path");
        }
        catch(IOException e) {
            throw new IllegalStateException(e);
        }
        this.bank = new Bank(ThreadLocalRandom.current().nextInt(1, 1_000), "Money Transfer Test Bank");
    }

    @Override
    protected Application configure() {
        this.set(TestProperties.CONTAINER_PORT, "0");  //Use a free port
        final ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new AbstractBinder() {
            @Override
            protected void configure() {
                this.bind(new TransferServiceInMemoryImpl(bank)).to(TransferService.class);
            }
        });
        resourceConfig.register(MoneyTransferResourceImpl.class);
        return resourceConfig;
    }

    protected Customer getCustomer(Customer customer, Status status ) {
        return getCustomer(customer.id(), status);
    }

    private Customer getCustomer(String customerId, Status status ) {
        final Invocation.Builder request = target(basePath)
                .path("customer")
                .path(customerId)
                .request();
        try (final Response response = request.get()) {
            assertContentType(response.getHeaderString(HttpHeaders.CONTENT_TYPE), status);
            assertEquals(status.getStatusCode(), response.getStatus());
            return response.readEntity(Customer.class);
        }
    }

    protected Set<Customer> getAllCustomers() {
        final Status status = Status.OK;
        final Invocation.Builder request = target(basePath)
                .path("customers")
                .request();
        try (final Response response = request.get()) {
            assertContentType(response.getHeaderString(HttpHeaders.CONTENT_TYPE), status);
            assertEquals(status.getStatusCode(), response.getStatus());
            final Set<Customer> result = response.readEntity(new GenericType<>() {});
            return result != null ? result : Collections.emptySet();
        }
    }

    protected Customer addCustomer(Customer customer) {
        return addCustomer(customer, Status.CREATED);
    }

    protected Customer addCustomer(Customer customer, Status status) {
        final Invocation.Builder request = target(basePath)
                .path("customer")
                .request();
        try (final Response response = request.post(Entity.json(customer))) {
            assertContentType(response.getHeaderString(HttpHeaders.CONTENT_TYPE), status);
            assertEquals(status.getStatusCode(), response.getStatus());
            if (status == Status.CREATED) {
                final String location = response.getHeaderString(HttpHeaders.LOCATION);
                assertNotNull(location);
                final String customerId = parseLocation(location);
                return getCustomer(customerId, Status.OK);
            }
        }
        return null;
    }

    protected void deleteCustomer(Customer customer, Status status) {
        final Invocation.Builder request = target(basePath)
                .path("customer")
                .path(customer.id())
                .request();
        try (final Response response = request.delete()) {
            assertContentType(response.getHeaderString(HttpHeaders.CONTENT_TYPE), status);
            assertEquals(status.getStatusCode(), response.getStatus());
        }
    }

    protected Account getAccount(Account account, Status status) {
        return getAccount(account.owner().id(), account.number(), status);
    }

    private Account getAccount(String customerId, int accountNumber, Status status) {
        final Invocation.Builder request = target(basePath)
                .path("account")
                .path(customerId)
                .path(String.valueOf(accountNumber))
                .request();
        try (final Response response = request.get()) {
            assertContentType(response.getHeaderString(HttpHeaders.CONTENT_TYPE), status);
            assertEquals(status.getStatusCode(), response.getStatus());
            return response.readEntity(Account.class);
        }
    }

    protected Set<Account> getAllAccounts(Customer customer, Status status) {
        final Invocation.Builder request = target(basePath)
                .path("accounts")
                .path(customer.id())
                .request();
        try (final Response response = request.get()) {
            assertContentType(response.getHeaderString(HttpHeaders.CONTENT_TYPE), status);
            assertEquals(status.getStatusCode(), response.getStatus());
            final Set<Account> result = response.readEntity(new GenericType<>() {});
            return result != null ? result : Collections.emptySet();
        }
    }

    protected Account addAccount(Customer customer, AccountType type, BigDecimal balance) {
        return addAccount(customer, type, balance, Status.CREATED);
    }

    protected Account addAccount(Customer customer, AccountType type, BigDecimal balance, Status status) {
        final Invocation.Builder request = target(basePath)
                .path("account")
                .request();
        final AccountDto accountDto = new AccountDto(customer.id(), type, balance);
        try (final Response response = request.post(Entity.json(accountDto))) {
            assertContentType(response.getHeaderString(HttpHeaders.CONTENT_TYPE), status);
            assertEquals(status.getStatusCode(), response.getStatus());
            if (status == Status.CREATED) {
                final String location = response.getHeaderString(HttpHeaders.LOCATION);
                assertNotNull(location);
                final int accountNumber = Integer.parseInt(parseLocation(location));
                return getAccount(customer.id(), accountNumber, Status.OK);
            }
        }
        return null;
    }

    protected void deleteAccount(Account account, Status status) {
        final Invocation.Builder request =  target(basePath).path("account")
                .path(account.owner().id())
                .path(String.valueOf(account.number()))
                .request();
        try (final Response response = request.delete()) {
            assertContentType(response.getHeaderString(HttpHeaders.CONTENT_TYPE), status);
            assertEquals(status.getStatusCode(), response.getStatus());
        }
    }

    protected TransferService.TransferResult transfer(Customer customer, Account source, Account target,
                                                      BigDecimal amount, Status status) {
        return transfer(customer, source, target, AccountTransferDto.createVersion(source, target), amount, status);
    }

    protected TransferService.TransferResult transfer(Customer customer, Account source, Account target,
                                                      String transferVersion, BigDecimal amount, Status status) {
        final Invocation.Builder request = target(basePath)
                .path("/account/transfer")
                .request()
                .header(HttpHeaders.IF_MATCH, "\"%s\"".formatted(transferVersion));
        final AccountTransferDto accountTransferDto =
                new AccountTransferDto(customer.id(), source.number(), target.number(), amount);
        try (final Response response = request.put(Entity.json(accountTransferDto))) {
            assertContentType(response.getHeaderString(HttpHeaders.CONTENT_TYPE), status);
            assertEquals(status.getStatusCode(), response.getStatus());
            return response.readEntity(TransferService.TransferResult.class);
        }
    }

    private static String parseLocation(@NotBlank String location) {
        final String[] segments = location.split("/");
        return segments[segments.length - 1];
    }

    private static void assertContentType(String contentType, Status status) {
        if (status == Status.OK)
            assertEquals(MediaType.APPLICATION_JSON, contentType);
        else
            assertNull(contentType);
    }
}
