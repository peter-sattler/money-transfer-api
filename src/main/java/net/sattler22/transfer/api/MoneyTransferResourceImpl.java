package net.sattler22.transfer.api;

import static jakarta.ws.rs.core.HttpHeaders.IF_MATCH;

import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import net.jcip.annotations.Immutable;
import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.service.TransferService;

/**
 * Money Transfer REST Resource Implementation
 *
 * @author Pete Sattler
 * @version September 2019
 */
@Immutable
@Singleton
@Path("/api/v1/money-transfer")
public final class MoneyTransferResourceImpl implements MoneyTransferResource {

    private static final Logger logger = LoggerFactory.getLogger(MoneyTransferResourceImpl.class);
    private static final String NO_CUSTOMERS_FOUND_ERROR_MESSAGE = "No customers found";
    private static final String RETRIEVED_LOG_MESSAGE_TEMPLATE = "Retrieved {}";
    private final CacheControl cacheControl;
    private final TransferService transferService;

    /**
     * Constructs a new money transfer REST resource implementation
     */
    @Inject
    public MoneyTransferResourceImpl(TransferService transferService) {
        this.transferService = Objects.requireNonNull(transferService, "Transfer service implementation is required");
        this.cacheControl = initCacheControl();
        logger.info("Initialized {}", this);
    }

    private static CacheControl initCacheControl() {
        final var cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        return cacheControl;
    }

    @Override
    public Response getBank() {
        final var bank = transferService.getBank();
        logger.info(RETRIEVED_LOG_MESSAGE_TEMPLATE, bank);
        return Response.ok().cacheControl(cacheControl).entity(bank).build();
    }

    @Override
    public Response getAllCustomers() {
        final var customers = transferService.getCustomers();
        if(customers.isEmpty()) {
            logger.warn(NO_CUSTOMERS_FOUND_ERROR_MESSAGE);
            throw new WebApplicationException(NO_CUSTOMERS_FOUND_ERROR_MESSAGE, Status.NOT_FOUND);
        }
        logger.info("Retrieved [{}] {}", customers.size(), customers.size() == 1 ? "customer" : "customers");
        return Response.ok().cacheControl(cacheControl).entity(new GenericEntity<Set<Customer>>(customers) {}).build();
    }

    @Override
    public Response findCustomer(String id) {
        try {
            final var customer = findCustomerImpl(id);
            logger.info(RETRIEVED_LOG_MESSAGE_TEMPLATE, customer);
            return Response.ok().cacheControl(cacheControl).entity(customer).build();
        }
        catch(NotFoundException e) {
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.NOT_FOUND);
        }
    }

    @Override
    public Response addCustomer(UriInfo uriInfo, Customer customer) {
        if (!transferService.addCustomer(customer)) {
            final var alreadyExistsMessage = String.format("Customer ID [%s] already exists", customer.id());
            logger.warn(alreadyExistsMessage);
            throw new WebApplicationException(alreadyExistsMessage, Status.CONFLICT);
        }
        logger.info("Created {}", customer);
        final var location = uriInfo.getBaseUriBuilder().path(MoneyTransferResourceImpl.class)
                                                        .path("customer")
                                                        .path(customer.id())
                                                        .build();
        return Response.created(location).cacheControl(cacheControl).build();
    }

    @Override
    public Response deleteCustomer(String id) {
        try {
            final var customer = findCustomerImpl(id);
            transferService.deleteCustomer(customer);
            logger.info("Deleted {}", customer);
            return Response.noContent().cacheControl(cacheControl).build();
        }
        catch(IllegalStateException e) {
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.CONFLICT);
        }
        catch(NotFoundException e) {
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.NOT_FOUND);
        }
    }

    @Override
    public Response getAllAccounts(String customerId) {
        try {
            final var owner = findCustomerImpl(customerId);
            final var accounts = owner.accounts();
            logger.info("Retrieved [{}] {} for {}", accounts.size(), accounts.size() == 1 ? "account" : "accounts", owner);
            return Response.ok().cacheControl(cacheControl).entity(new GenericEntity<Set<Account>>(accounts) {}).build();
        }
        catch(NotFoundException e) {
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.NOT_FOUND);
        }
    }

    @Override
    public Response findAccount(String customerId, int number) {
        try {
            final var owner = findCustomerImpl(customerId);
            final var account = findAccountImpl(owner, number);
            logger.info(RETRIEVED_LOG_MESSAGE_TEMPLATE, account);
            return Response.ok().cacheControl(cacheControl).entity(account).build();
        }
        catch(NotFoundException e) {
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.NOT_FOUND);
        }
    }

    @Override
    public Response addAccount(UriInfo uriInfo, AccountDto accountDto) {
        try {
            final var owner = findCustomerImpl(accountDto.customerId());
            final var account = new Account(accountDto.type(), owner, accountDto.balance());
            if (!transferService.addAccount(account)) {
                final var errorMessage = String.format("Unable to add account #[%d]", account.number());
                logger.warn(errorMessage);
                throw new WebApplicationException(errorMessage, Status.CONFLICT);
            }
            logger.info("Created {}", account);
            final var location = uriInfo.getBaseUriBuilder().path(MoneyTransferResourceImpl.class)
                                                            .path("account")
                                                            .path(owner.id())
                                                            .path(Integer.toString(account.number()))
                                                            .build();
            return Response.created(location).cacheControl(cacheControl).build();
        }
        catch(NotFoundException e) {
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.NOT_FOUND);
        }
    }

    @Override
    public Response deleteAccount(String customerId, int number) {
        try {
            final var owner = findCustomerImpl(customerId);
            final var account = findAccountImpl(owner, number);
            if (!transferService.deleteAccount(account))
                throw new NotFoundException(String.format("Account #[%d] does not exist", number));
            logger.info("Deleted {}", account);
            return Response.noContent().cacheControl(cacheControl).build();
        }
        catch(IllegalStateException e) {
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.CONFLICT);
        }
        catch(NotFoundException e) {
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.NOT_FOUND);
        }
    }

    @Override
    public Response transfer(HttpHeaders httpHeaders, Request request, AccountTransferDto accountTransferDto) {
        try {
            final var owner = findCustomerImpl(accountTransferDto.customerId());
            final var sourceAccount = findAccountImpl(owner, accountTransferDto.sourceNumber());
            final var targetAccount = findAccountImpl(owner, accountTransferDto.targetNumber());
            final var serverTransferVersion = AccountTransferDto.createVersion(sourceAccount, targetAccount);
            final var entityTag = new EntityTag(serverTransferVersion);
            final var responseBuilder = request.evaluatePreconditions(entityTag);
            if (responseBuilder != null) {
                final var clientTransferVersion = httpHeaders.getHeaderString(IF_MATCH);
                logger.warn("Client transfer version [{}] is older than server transfer version [{}]",
                             clientTransferVersion == null ? "UNKNOWN" : clientTransferVersion.replace("\"", ""), serverTransferVersion);
                return responseBuilder.build();
            }
            final var transferResult = transferService.transfer(owner, sourceAccount, targetAccount, accountTransferDto.amount());
            return Response.ok().cacheControl(cacheControl).tag(entityTag).entity(transferResult).build();
        }
        catch(NotFoundException e) {
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.NOT_FOUND);
        }
        catch(IllegalArgumentException e) {
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.CONFLICT);
        }
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
        return String.format("%s [cacheControl=[%s], transferService=%s]", getClass().getSimpleName(), cacheControl, transferService);
    }
}
