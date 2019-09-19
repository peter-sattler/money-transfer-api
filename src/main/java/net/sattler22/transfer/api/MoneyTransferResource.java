package net.sattler22.transfer.api;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
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
 * Money Transfer REST Resource
 *
 * @author Pete Sattler
 * @version September 2019
 */
@Singleton
@Path("/api/money-transfer")
@Produces(APPLICATION_JSON)
public final class MoneyTransferResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoneyTransferResource.class);
    private final TransferService transferService;

    /**
     * Constructs a new money transfer REST resource
     */
    @Inject
    public MoneyTransferResource(TransferService transferService) {
        this.transferService = Objects.requireNonNull(transferService, "Transfer service implementation is required");
        LOGGER.info("Initialized {}", this);
    }

    @GET
    @Path("/bank")
    public Response getBank() {
        final Bank bank = transferService.getBank();
        LOGGER.info("Retrieved {}", bank);
        return Response.ok().entity(bank).build();
    }

    @GET
    @Path("/customers")
    public Response getAllCustomers() {
        final Set<Customer> customers = transferService.getCustomers();
        LOGGER.info("Retrieved [{}] {}", customers.size(), customers.size() == 1 ? "customer" : "customers");
        return Response.ok().entity(new GenericEntity<Set<Customer>>(customers) {}).build();
    }

    @GET
    @Path("/customer/{id}")
    public Response findCustomer(@PathParam("id") String id) {
        try {
            final Customer customer = findCustomerImpl(id);
            LOGGER.info("Retrieved {}", customer);
            return Response.ok().entity(customer).build();
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Response.Status.NOT_FOUND);
        }
    }

    @POST
    @Path("/customer")
    @Consumes(APPLICATION_JSON)
    public Response addCustomer(@Context UriInfo uriInfo, Customer customer) {
        if (!transferService.addCustomer(customer)) {
            final String alreadyExistsMessage = String.format("Customer ID [%s] already exists", customer.getId());
            LOGGER.warn(alreadyExistsMessage);
            throw new WebApplicationException(alreadyExistsMessage, Response.Status.CONFLICT);
        }
        LOGGER.info("{} created successfully", customer);
        final URI location = uriInfo.getBaseUriBuilder().path(MoneyTransferResource.class)
                                                        .path("customer")
                                                        .path(customer.getId()).build();
        return Response.created(location).build();
    }

    @DELETE
    @Path("/customer/{id}")
    public Response deleteCustomer(@PathParam("id") String id) {
        try {
            final Customer customer = findCustomerImpl(id);
            transferService.deleteCustomer(customer);
            LOGGER.info("Deleted {}", customer);
            return Response.noContent().build();
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Path("/accounts/{customerId}")
    public Response getAllAccounts(@PathParam("customerId") String customerId) {
        try {
            final Customer owner = findCustomerImpl(customerId);
            final Set<Account> accounts = owner.getAccounts();
            LOGGER.info("Retrieved [{}] {} for {}", accounts.size(), accounts.size() == 1 ? "account" : "accounts", owner);
            return Response.ok().entity(new GenericEntity<Set<Account>>(accounts) {}).build();
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Path("/account/{customerId}/{number}")
    public Response findAccount(@PathParam("customerId") String customerId, @PathParam("number") int number) {
        try {
            final Customer owner = findCustomerImpl(customerId);
            final Account account = findAccountImpl(owner, number);
            LOGGER.info("Retrieved {}", account);
            return Response.ok().entity(account).build();
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Response.Status.NOT_FOUND);
        }
    }

    /**
     * Parse account number
     *
     * @param locationHeader The location header of the newly created account
     * @return The new account number
     */
    public static int parseAccountNumber(String locationHeader) {
        Objects.requireNonNull(locationHeader, "Location header value is required");
        final String path;
        try {
            path = new URI(locationHeader).getPath();
        }
        catch(URISyntaxException e) {
            throw new IllegalStateException(e);
        }
        return Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
    }

    @POST
    @Path("/account")
    @Consumes(APPLICATION_JSON)
    public Response addAccount(@Context UriInfo uriInfo, AccountDTO accountDTO) {
        try {
            final Customer owner = findCustomerImpl(accountDTO.getCustomerId());
            final Account account = new Account(accountDTO.getType(), owner, accountDTO.getBalance());
            if (!transferService.addAccount(account)) {
                final String errorMessage = String.format("Unable to add account #[%d]", account.getNumber());
                LOGGER.warn(errorMessage);
                throw new WebApplicationException(errorMessage, Response.Status.CONFLICT);
            }
            LOGGER.info("{} created successfully", account);
            final URI location = uriInfo.getBaseUriBuilder().path(MoneyTransferResource.class)
                                                            .path("account")
                                                            .path(owner.getId())
                                                            .path(Integer.toString(account.getNumber())).build();
            return Response.created(location).build();
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Response.Status.NOT_FOUND);
        }
    }


    @DELETE
    @Path("/account/{customerId}/{number}")
    public Response deleteAccount(@PathParam("customerId") String customerId, @PathParam("number") int number) {
        try {
            final Customer owner = findCustomerImpl(customerId);
            final Account account = findAccountImpl(owner, number);
            if (!owner.deleteAccount(account))
                throw new NotFoundException(String.format("Account #[%d] does not exist", number));
            LOGGER.info("Deleted {}", account);
            return Response.noContent().build();
        }
        catch(NotFoundException e) {
            LOGGER.warn("{}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), e.getCause(), Response.Status.NOT_FOUND);
        }
    }

    @PUT
    @Path("/account/transfer")
    @Consumes(APPLICATION_JSON)
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
        return Response.ok().entity(transferResult).build();
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
