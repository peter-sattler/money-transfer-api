package net.sattler22.transfer.api;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.dto.AccountDTO;
import net.sattler22.transfer.dto.AccountTransferDTO;

/**
 * Money Transfer REST Resource Interface
 *
 * @author Pete Sattler
 * @version September 2019
 */
public interface MoneyTransferResource {

    /**
     * Fetch bank details
     */
    @GET
    @Path("/bank")
    @Produces(APPLICATION_JSON)
    Response getBank();

    /**
     * Fetch all customers
     */
    @GET
    @Path("/customers")
    @Produces(APPLICATION_JSON)
    Response getAllCustomers();

    /**
     * Fetch a single customer
     *
     * @param id The customer identifier
     */
    @GET
    @Path("/customer/{id}")
    @Produces(APPLICATION_JSON)
    Response findCustomer(@PathParam("id") String id);

    /**
     * Add a customer
     *
     * @param uriInfo The URI information
     * @param customer The customer to add
     */
    @POST
    @Path("/customer")
    @Consumes(APPLICATION_JSON)
    Response addCustomer(@Context UriInfo uriInfo, Customer customer);

    /**
     * Delete a customer
     *
     * @param id The customer identifier
     */
    @DELETE
    @Path("/customer/{id}")
    Response deleteCustomer(@PathParam("id") String id);

    /**
     * Fetch all accounts
     *
     * @param customerId The customer identifier
     */
    @GET
    @Path("/accounts/{customerId}")
    @Produces(APPLICATION_JSON)
    Response getAllAccounts(@PathParam("customerId") String customerId);

    /**
     * Fetch a single account
     *
     * @param customerId The customer identifier
     * @param number The account number
     */
    @GET
    @Path("/account/{customerId}/{number : \\d+}")
    @Produces(APPLICATION_JSON)
    Response findAccount(@PathParam("customerId") String customerId, @PathParam("number") int number);

    /**
     * Add an account
     *
     * @param uriInfo The URI information
     * @param accountDTO  The account data transfer object (DTO)
     */
    @POST
    @Path("/account")
    @Consumes(APPLICATION_JSON)
    Response addAccount(@Context UriInfo uriInfo, AccountDTO accountDTO);

    /**
     * Parse newly created account number
     *
     * @param locationHeader The location header of the newly created account
     * @return The new account number
     */
    static int parseAccountNumber(String locationHeader) {
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

    /**
     * Delete an account
     *
     * @param customerId The customer identifier
     * @param number The account number
     */
    @DELETE
    @Path("/account/{customerId}/{number : \\d+}")
    Response deleteAccount(@PathParam("customerId") String customerId, @PathParam("number") int number);

    /**
     * Account transfer
     *
     * @param request The HTTP request
     * @param accountTransferDTO The account transfer data transfer object
     */
    @PUT
    @Path("/account/transfer")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Response transfer(@Context Request request, AccountTransferDTO accountTransferDTO);
}
