package net.sattler22.transfer.api;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sattler22.transfer.dto.AccountDTO;
import net.sattler22.transfer.dto.AccountTransferDTO;
import net.sattler22.transfer.model.Account;
import net.sattler22.transfer.model.Bank;
import net.sattler22.transfer.model.Customer;
import net.sattler22.transfer.service.TransferService;
import net.sattler22.transfer.service.TransferService.TransferResult;
import net.sattler22.transfer.service.TransferServiceInMemoryImpl;

/**
 * Revolut Money Transfer REST Service
 *
 * @author Pete Sattler
 * @version May 2019
 */
@Singleton
@Path("/api/money-transfer")
@Produces(APPLICATION_JSON)
public final class MoneyTransferRestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoneyTransferRestService.class);
    private final TransferService transferService;

    /**
     * Constructs a new money transfer REST resource
     */
    public MoneyTransferRestService() {
        final Bank bank = new Bank(1, "Revolut World Banking Empire");
        this.transferService = new TransferServiceInMemoryImpl(bank);
        LOGGER.info("Money transfer resource initialization complete");
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
        return Response.ok().entity(customers).build();
    }

    @GET
    @Path("/customer/{id}")
    public Response findCustomer(@PathParam("id") int id) {
        final Customer customer = findCustomerHelper(id);
        LOGGER.info("Retrieved {}", customer);
        return Response.ok().entity(customer).build();
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
        final URI location = uriInfo.getBaseUriBuilder().path(MoneyTransferRestService.class)
                                                        .path("customer")
                                                        .path(Integer.toString(customer.getId())).build();
        return Response.created(location).entity(customer).build();
    }

    @DELETE
    @Path("/customer/{id}")
    public Response deleteCustomer(@PathParam("id") int id) {
        final Customer customer = findCustomerHelper(id);
        transferService.deleteCustomer(customer);
        LOGGER.info("Deleted {}", customer);
        return Response.noContent().build();
    }

    @POST
    @Path("/account")
    @Consumes(APPLICATION_JSON)
    public Response addAccount(@Context UriInfo uriInfo, AccountDTO accountDTO) {
        final Customer owner = findCustomerHelper(accountDTO.getCustomerId());
        final Account account = new Account(accountDTO.getNumber(), owner, accountDTO.getBalance());
        if (!transferService.addAccount(account)) {
            final String alreadyExistsMessage = String.format("Account #%d already exists", account.getNumber());
            LOGGER.warn(alreadyExistsMessage);
            throw new WebApplicationException(alreadyExistsMessage, Response.Status.CONFLICT);
        }
        LOGGER.info("{} created successfully", account);
        final URI location = uriInfo.getBaseUriBuilder().path(MoneyTransferRestService.class)
                                                        .path("customer")
                                                        .path(Integer.toString(owner.getId())).build();
        return Response.created(location).entity(owner).build();
    }

    @DELETE
    @Path("/account/{customerId}/{number}")
    public Response deleteAccount(@PathParam("customerId") int customerId, @PathParam("number") int number) {
        final Customer owner = findCustomerHelper(customerId);
        final Account account = new Account(number, owner);
        if (!owner.deleteAccount(account)) {
            final String doesNotExist = String.format("Account #%d does not exist", number);
            LOGGER.warn(doesNotExist);
            throw new WebApplicationException(doesNotExist, Response.Status.NOT_FOUND);
        }
        LOGGER.info("Deleted {}", account);
        return Response.noContent().build();
    }

    @PUT
    @Path("/account/transfer")
    @Consumes(APPLICATION_JSON)
    public Response transfer(AccountTransferDTO accountTransferDTO) {
        final Customer owner = findCustomerHelper(accountTransferDTO.getCustomerId());
        final Account sourceAccount = findAccountHelper(owner, accountTransferDTO.getSourceNumber());
        final Account targetAccount = findAccountHelper(owner, accountTransferDTO.getTargetNumber());
        final TransferResult transferResult;
        try {
            transferResult = transferService.transfer(owner, sourceAccount, targetAccount, accountTransferDTO.getAmount());
        }
        catch (IllegalArgumentException e) {
            final String doesNotExist = e.getMessage();
            LOGGER.warn("{}", doesNotExist);
            throw new WebApplicationException(doesNotExist, e.getCause(), Response.Status.CONFLICT);
        }
        return Response.ok().entity(transferResult).build();
    }

    private Customer findCustomerHelper(int id) throws NotFoundException {
        final Optional<Customer> customer = transferService.findCustomer(id);
        if (!customer.isPresent()) {
            final String notFoundMessage = String.format("Customer ID #%d not found", id);
            LOGGER.warn(notFoundMessage);
            throw new NotFoundException(notFoundMessage);
        }
        return customer.get();
    }

    private static Account findAccountHelper(Customer owner, int number) throws NotFoundException {
        final Optional<Account> account = owner.findAccount(number);
        if (!account.isPresent()) {
            final String notFoundMessage = String.format("Account #%d not found", number);
            LOGGER.warn(notFoundMessage);
            throw new NotFoundException(notFoundMessage);
        }
        return account.get();
    }
}
