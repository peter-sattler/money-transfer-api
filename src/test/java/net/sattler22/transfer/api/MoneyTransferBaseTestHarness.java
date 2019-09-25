package net.sattler22.transfer.api;

import static javax.ws.rs.core.HttpHeaders.IF_UNMODIFIED_SINCE;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static net.sattler22.transfer.api.MoneyTransferConstants.API_BASE_PATH;
import static org.glassfish.jersey.test.TestProperties.CONTAINER_PORT;

import java.math.BigDecimal;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.domain.AccountType;
import net.sattler22.transfer.domain.Bank;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.dto.AccountDTO;
import net.sattler22.transfer.dto.AccountTransferDTO;
import net.sattler22.transfer.service.TransferService;
import net.sattler22.transfer.service.TransferServiceInMemoryImpl;

/**
 * Money Transfer Base Test Harness
 *
 * @author Pete Sattler
 * @version September 2019
 */
public abstract class MoneyTransferBaseTestHarness extends JerseyTest {

    protected final Bank bank = new Bank(1, "Money Transfer Test Bank");

    @Override
    protected Application configure() {
        this.set(CONTAINER_PORT, "0");  // Use a free port
        final ResourceConfig config = new ResourceConfig();
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                final TransferServiceInMemoryImpl transferServiceImpl = new TransferServiceInMemoryImpl(bank);
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
        return target(API_BASE_PATH).path("customer").request().post(Entity.json(customer));
    }

    /**
     * Delete a customer
     */
    protected Response deleteCustomerImpl(Customer customer) {
        return target(API_BASE_PATH).path("customer")
                                    .path(customer.getId()).request().delete();
    }

    /**
     * Add an account
     */
    protected Account addAccountImpl(AccountType type, String customerId, BigDecimal balance) {
        final String locationHeader = addAccountResponseImpl(type, customerId, balance).getHeaderString(LOCATION);
        final int accountNumber = MoneyTransferResource.parseAccountNumber(locationHeader);
        Response response = target(API_BASE_PATH).path("account")
                                                 .path(customerId)
                                                 .path(Integer.toString(accountNumber)).request().get();
        return response.readEntity(Account.class);
    }

    /**
     * Add an account
     */
    protected Response addAccountResponseImpl(AccountType type, String customerId, BigDecimal balance) {
        final AccountDTO accountDTO = new AccountDTO(type, customerId, balance);
        return target(API_BASE_PATH).path("account").request().post(Entity.json(accountDTO));
    }

    /**
     * Delete an account
     */
    protected Response deleteAccountImpl(Customer customer, int accountNumber) {
        return target(API_BASE_PATH).path("account")
                                    .path(customer.getId())
                                    .path(String.valueOf(accountNumber)).request().delete();
    }

    /**
     * Get all accounts for a customer
     */
    protected Response getAccounts(Customer customer) {
        return target(API_BASE_PATH).path("accounts")
                                    .path(customer.getId()).request().get();
    }

    /**
     * Get a specific account for a customer
     */
    protected Response getAccount(Customer customer, int accountNumber) {
        return target(API_BASE_PATH).path("account")
                                    .path(customer.getId())
                                    .path(String.valueOf(accountNumber)).request().get();
    }

    /**
     * Transfer funds between accounts
     */
    protected Response accountTransferImpl(AccountTransferDTO accountTransferDTO) {
        return target(API_BASE_PATH).path("/account/transfer").request()
                                    .header(IF_UNMODIFIED_SINCE, accountTransferDTO.getLastModified())
                                    .put(Entity.json(accountTransferDTO));
    }
}
