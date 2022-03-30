package net.sattler22.transfer.api;

import static jakarta.ws.rs.core.HttpHeaders.IF_MATCH;
import static jakarta.ws.rs.core.HttpHeaders.LOCATION;
import static org.glassfish.jersey.test.TestProperties.CONTAINER_PORT;

import java.io.IOException;
import java.math.BigDecimal;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.domain.AccountType;
import net.sattler22.transfer.domain.Bank;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.service.TransferService;
import net.sattler22.transfer.service.TransferServiceInMemoryImpl;
import net.sattler22.transfer.util.PropertyFileUtils;

/**
 * Money Transfer Base Test Harness
 *
 * @author Pete Sattler
 * @version September 2019
 */
abstract class MoneyTransferBaseTestHarness extends JerseyTest {

    protected final String basePath;
    protected final Bank bank;

    protected MoneyTransferBaseTestHarness() {
        try {
            final var appProps = PropertyFileUtils.readResourceProperties(getClass(), "app.properties");
            this.basePath = appProps.getProperty("base.path");
        }
        catch(IOException e) {
            throw new IllegalStateException(e);
        }
        this.bank = new Bank(1, "Money Transfer Test Bank");
    }

    @Override
    protected Application configure() {
        this.set(CONTAINER_PORT, "0");  //Use a free port
        final var config = new ResourceConfig();
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                final var transferServiceImpl = new TransferServiceInMemoryImpl(bank);
                this.bind(transferServiceImpl).to(TransferService.class);
            }
        });
        config.register(MoneyTransferResourceImpl.class);
        return config;
    }

    /**
     * Add a customer
     */
    protected Response addCustomerImpl(Customer customer) {
        return target(basePath).path("customer").request().post(Entity.json(customer));
    }

    /**
     * Delete a customer
     */
    protected Response deleteCustomerImpl(Customer customer) {
        return target(basePath).path("customer")
                               .path(customer.id()).request().delete();
    }

    /**
     * Add an account
     */
    protected Account addAccountImpl(AccountType type, String customerId, BigDecimal balance) {
        final var locationHeader = addAccountResponseImpl(type, customerId, balance).getHeaderString(LOCATION);
        final var accountNumber = MoneyTransferResource.parseAccountNumber(locationHeader);
        final var response = target(basePath).path("account")
                                             .path(customerId)
                                             .path(Integer.toString(accountNumber)).request().get();
        return response.readEntity(Account.class);
    }

    /**
     * Add an account
     */
    protected Response addAccountResponseImpl(AccountType type, String customerId, BigDecimal balance) {
        final var accountDto = new AccountDto(type, customerId, balance);
        return target(basePath).path("account").request().post(Entity.json(accountDto));
    }

    /**
     * Delete an account
     */
    protected Response deleteAccountImpl(Customer customer, int accountNumber) {
        return target(basePath).path("account")
                               .path(customer.id())
                               .path(String.valueOf(accountNumber)).request().delete();
    }

    /**
     * Get all accounts for a customer
     */
    protected Response getAccounts(Customer customer) {
        return target(basePath).path("accounts")
                               .path(customer.id()).request().get();
    }

    /**
     * Get a specific account for a customer
     */
    protected Response getAccount(Customer customer, int accountNumber) {
        return target(basePath).path("account")
                               .path(customer.id())
                               .path(String.valueOf(accountNumber)).request().get();
    }

    /**
     * Transfer funds between accounts
     */
    protected Response accountTransferImpl(String transferVersion, AccountTransferDto accountTransferDto) {
        return target(basePath).path("/account/transfer").request()
                               .header(IF_MATCH, String.format("\"%s\"", transferVersion))
                               .put(Entity.json(accountTransferDto));
    }
}
