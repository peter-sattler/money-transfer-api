package net.sattler22.transfer.api;

import static javax.ws.rs.core.HttpHeaders.IF_MATCH;
import static net.sattler22.transfer.api.MoneyTransferConstants.API_BASE_PATH;

import java.net.URI;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
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
 * @version November 2019
 */
@Singleton
@Path(API_BASE_PATH)
public final class MoneyTransferResourceImpl implements MoneyTransferResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoneyTransferResourceImpl.class);
    private final CacheControl cacheControl;
    private final TransferService transferService;

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
        cacheControl.setNoCache(true);
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
        if(customers.isEmpty()) {
            final String errorMessage = "No customers found";
            LOGGER.warn(errorMessage);
            throw new WebApplicationException(errorMessage, Status.NOT_FOUND);
        }
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
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.NOT_FOUND);
        }
    }

    @Override
    public Response addCustomer(UriInfo uriInfo, Customer customer) {
        if (!transferService.addCustomer(customer)) {
            final String alreadyExistsMessage = String.format("Customer ID [%s] already exists", customer.getId());
            LOGGER.warn(alreadyExistsMessage);
            throw new WebApplicationException(alreadyExistsMessage, Status.CONFLICT);
        }
        LOGGER.info("Created {}", customer);
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
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.CONFLICT);
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.NOT_FOUND);
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
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.NOT_FOUND);
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
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.NOT_FOUND);
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
                throw new WebApplicationException(errorMessage, Status.CONFLICT);
            }
            LOGGER.info("Created {}", account);
            final URI location = uriInfo.getBaseUriBuilder().path(MoneyTransferResourceImpl.class)
                                                            .path("account")
                                                            .path(owner.getId())
                                                            .path(Integer.toString(account.getNumber())).build();
            return Response.created(location).cacheControl(cacheControl).build();
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.NOT_FOUND);
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
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.CONFLICT);
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.NOT_FOUND);
        }
    }

    @Override
    public Response transfer(HttpHeaders headers, Request request, AccountTransferDTO accountTransferDTO) {
        try {
            final Customer owner = findCustomerImpl(accountTransferDTO.getCustomerId());
            final Account sourceAccount = findAccountImpl(owner, accountTransferDTO.getSourceNumber());
            final Account targetAccount = findAccountImpl(owner, accountTransferDTO.getTargetNumber());
            final String serverTransferVersion = AccountTransferDTO.createVersion(sourceAccount, targetAccount);
            final EntityTag entityTag = new EntityTag(serverTransferVersion);
            final ResponseBuilder responseBuilder = request.evaluatePreconditions(entityTag);
            if (responseBuilder != null) {
                final String clientTransferVersion = headers.getHeaderString(IF_MATCH);
                LOGGER.warn("Client transfer version [{}] is older than server transfer version [{}]",
                    clientTransferVersion == null ? "UNKNOWN" : clientTransferVersion.replace("\"", ""), serverTransferVersion);
                return responseBuilder.build();
            }
            final TransferResult transferResult =
                transferService.transfer(owner, sourceAccount, targetAccount, accountTransferDTO.getAmount());
            return Response.ok().cacheControl(cacheControl).tag(entityTag).entity(transferResult).build();
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Status.NOT_FOUND);
        }
        catch(IllegalArgumentException e) {
            LOGGER.warn("{}", e.getMessage());
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
