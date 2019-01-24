package net.sattler22.transfer.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sattler22.transfer.model.Bank;
import net.sattler22.transfer.service.TransferService;
import net.sattler22.transfer.service.TransferServiceInMemoryImpl;

/**
 * Revolut&copy; Money Transfer REST Service
 *
 * @author Pete Sattler
 * @version January 2019
 */
@Path("/money-transfer/api")
public final class MoneyTransferRestService {

    private final TransferService transferService;

    /**
     * Constructs a new money transfer REST service
     */
    public MoneyTransferRestService() {
        final Bank bank = new Bank(1, "Revolut Bank of the World");
        this.transferService = new TransferServiceInMemoryImpl(bank);
    }

    @GET
    @Path("healthcheck")
    @Produces(MediaType.TEXT_PLAIN)
    public Response healthCheck() {
        return Response.status(Response.Status.OK).entity("UP").build();
    }

    @GET
    @Path("bank")
    @Produces(MediaType.APPLICATION_JSON)
    public Bank getBank() {
        return transferService.getBank();
    }
}
