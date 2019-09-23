package net.sattler22.transfer.api;

import java.net.URI;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.domain.Bank;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.dto.AccountDTO;
import net.sattler22.transfer.dto.AccountTransferDTO;
import net.sattler22.transfer.service.TransferService;
import net.sattler22.transfer.service.TransferService.TransferResult;

/**
 * Money Transfer REST Resource Implementation
 *
 * @author Pete Sattler
 * @version September 2019
 */
@Singleton
@Path("/api/money-transfer")
public final class MoneyTransferResourceImpl implements MoneyTransferResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoneyTransferResourceImpl.class);
    private static final int CACHE_MAX_AGE_SECONDS = 5 * 60;
    private final TransferService transferService;
    private final CacheControl cacheControl;

    /**
     * Constructs a new money transfer REST resource implementation
     */
    @Inject
    public MoneyTransferResourceImpl(TransferService transferService) {
        this.transferService = Objects.requireNonNull(transferService, "Transfer service implementation is required");
        this.cacheControl = initCacheControl();
        LOGGER.info("Initialized {}", this);
    }

    private static CacheControl initCacheControl() {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(CACHE_MAX_AGE_SECONDS);
        cacheControl.setPrivate(true);  //Client cache only
        cacheControl.setNoStore(true);  //Do not store to disk
        return cacheControl;
    }

    @Override
    public Response getBank() {
        final Bank bank = transferService.getBank();
        LOGGER.info("Retrieved {}", bank);
        return Response.ok().cacheControl(cacheControl).entity(bank).build();
    }

    @Override
    public Response getAllCustomers() {
        final Set<Customer> customers = transferService.getCustomers();
        LOGGER.info("Retrieved [{}] {}", customers.size(), customers.size() == 1 ? "customer" : "customers");
        return Response.ok().cacheControl(cacheControl).entity(new GenericEntity<Set<Customer>>(customers) {}).build();
    }

    @Override
    public Response findCustomer(String id) {
        try {
            final Customer customer = findCustomerImpl(id);
            LOGGER.info("Retrieved {}", customer);
            return Response.ok().cacheControl(cacheControl).entity(customer).build();
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Response.Status.NOT_FOUND);
        }
    }

    @Override
    public Response addCustomer(UriInfo uriInfo, Customer customer) {
        if (!transferService.addCustomer(customer)) {
            final String alreadyExistsMessage = String.format("Customer ID [%s] already exists", customer.getId());
            LOGGER.warn(alreadyExistsMessage);
            throw new WebApplicationException(alreadyExistsMessage, Response.Status.CONFLICT);
        }
        LOGGER.info("{} created successfully", customer);
        final URI location = uriInfo.getBaseUriBuilder().path(MoneyTransferResourceImpl.class)
                                                        .path("customer")
                                                        .path(customer.getId()).build();
        return Response.created(location).cacheControl(cacheControl).build();
    }

    @Override
    public Response deleteCustomer(String id) {
        try {
            final Customer customer = findCustomerImpl(id);
            transferService.deleteCustomer(customer);
            LOGGER.info("Deleted {}", customer);
            return Response.noContent().cacheControl(cacheControl).build();
        }
        catch(IllegalStateException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Response.Status.CONFLICT);
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Response.Status.NOT_FOUND);
        }
    }

    @Override
    public Response getAllAccounts(String customerId) {
        try {
            final Customer owner = findCustomerImpl(customerId);
            final Set<Account> accounts = owner.getAccounts();
            LOGGER.info("Retrieved [{}] {} for {}", accounts.size(), accounts.size() == 1 ? "account" : "accounts", owner);
            return Response.ok().cacheControl(cacheControl).entity(new GenericEntity<Set<Account>>(accounts) {}).build();
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Response.Status.NOT_FOUND);
        }
    }

    @Override
    public Response findAccount(String customerId, int number) {
        try {
            final Customer owner = findCustomerImpl(customerId);
            final Account account = findAccountImpl(owner, number);
            LOGGER.info("Retrieved {}", account);
            return Response.ok().cacheControl(cacheControl).entity(account).build();
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Response.Status.NOT_FOUND);
        }
    }

    @Override
    public Response addAccount(UriInfo uriInfo, AccountDTO accountDTO) {
        try {
            final Customer owner = findCustomerImpl(accountDTO.getCustomerId());
            final Account account = new Account(accountDTO.getType(), owner, accountDTO.getBalance());
            if (!transferService.addAccount(account)) {
                final String errorMessage = String.format("Unable to add account #[%d]", account.getNumber());
                LOGGER.warn(errorMessage);
                throw new WebApplicationException(errorMessage, Response.Status.CONFLICT);
            }
            LOGGER.info("{} created successfully", account);
            final URI location = uriInfo.getBaseUriBuilder().path(MoneyTransferResourceImpl.class)
                                                            .path("account")
                                                            .path(owner.getId())
                                                            .path(Integer.toString(account.getNumber())).build();
            return Response.created(location).cacheControl(cacheControl).build();
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Response.Status.NOT_FOUND);
        }
    }

    @Override
    public Response deleteAccount(String customerId, int number) {
        try {
            final Customer owner = findCustomerImpl(customerId);
            final Account account = findAccountImpl(owner, number);
            if (!transferService.deleteAccount(account))
                throw new NotFoundException(String.format("Account #[%d] does not exist", number));
            LOGGER.info("Deleted {}", account);
            return Response.noContent().cacheControl(cacheControl).build();
        }
        catch(IllegalStateException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Response.Status.CONFLICT);
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Response.Status.NOT_FOUND);
        }
    }

    @Override
    public Response transfer(AccountTransferDTO accountTransferDTO) {
        final TransferResult transferResult;
        try {
            final Customer owner = findCustomerImpl(accountTransferDTO.getCustomerId());
            final Account sourceAccount = findAccountImpl(owner, accountTransferDTO.getSourceNumber());
            final Account targetAccount = findAccountImpl(owner, accountTransferDTO.getTargetNumber());
            transferResult = transferService.transfer(owner, sourceAccount, targetAccount, accountTransferDTO.getAmount());
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Response.Status.NOT_FOUND);
        }
        catch(IllegalArgumentException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Response.Status.CONFLICT);
        }
        return Response.ok().cacheControl(cacheControl).entity(transferResult).build();
    }

    private Customer findCustomerImpl(String id) throws NotFoundException {
        return transferService.findCustomer(id)
                              .orElseThrow(() -> new NotFoundException(String.format("Customer ID [%s] not found", id)));
    }

    private static Account findAccountImpl(Customer owner, int number) throws NotFoundException {
        return Account.find(owner, number)
                      .orElseThrow(() -> new NotFoundException(String.format("Customer ID [%s], account #[%d] not found", owner, number)));
    }

    @Override
    public String toString() {
        return String.format("%s [transferService=%s]", getClass().getSimpleName(), transferService);
    }
}
