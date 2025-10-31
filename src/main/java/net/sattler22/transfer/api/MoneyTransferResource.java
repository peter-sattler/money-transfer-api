package net.sattler22.transfer.api;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import net.sattler22.transfer.domain.Customer;

/**
 * Money Transfer REST Resource Interface
 *
 * @author Pete Sattler
 * @version November 2025
 * @since September 2019
 */
public sealed interface MoneyTransferResource permits MoneyTransferResourceImpl {

    /**
     * Fetch bank details
     */
    @GET
    @Path("/bank")
    @Produces(MediaType.APPLICATION_JSON)
    Response getBank();

    /**
     * Fetch all customers
     */
    @GET
    @Path("/customers")
    @Produces(MediaType.APPLICATION_JSON)
    Response getAllCustomers();

    /**
     * Fetch a single customer
     *
     * @param id The customer identifier
     */
    @GET
    @Path("/customer/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Response findCustomer(@PathParam("id") String id);

    /**
     * Add a customer
     *
     * @param uriInfo The URI information
     * @param customer The customer to add
     */
    @POST
    @Path("/customer")
    @Consumes(MediaType.APPLICATION_JSON)
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
    @Produces(MediaType.APPLICATION_JSON)
    Response getAllAccounts(@PathParam("customerId") String customerId);

    /**
     * Fetch a single account
     *
     * @param customerId The customer identifier
     * @param number The account number
     */
    @GET
    @Path("/account/{customerId}/{number : \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    Response findAccount(@PathParam("customerId") String customerId, @PathParam("number") int number);

    /**
     * Add an account
     *
     * @param uriInfo The URI information
     * @param accountDto  The account data transfer object (DTO)
     */
    @POST
    @Path("/account")
    @Consumes(MediaType.APPLICATION_JSON)
    Response addAccount(@Context UriInfo uriInfo, AccountDto accountDto);

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
     * @param httpHeaders The HTTP headers
     * @param request The HTTP request
     * @param accountTransferDto The account transfer data transfer object
     */
    @PUT
    @Path("/account/transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response transfer(@Context HttpHeaders httpHeaders, @Context Request request, AccountTransferDto accountTransferDto);
}
