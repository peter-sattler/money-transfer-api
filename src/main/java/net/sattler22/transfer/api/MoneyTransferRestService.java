package net.sattler22.transfer.api;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpServer;

import net.sattler22.transfer.model.Bank;
import net.sattler22.transfer.service.TransferService;
import net.sattler22.transfer.service.TransferServiceInMemoryImpl;

/**
 * Revolut&copy; Money Transfer REST Service
 *
 * @author Pete Sattler
 * @version January 2019
 */
@Path("/api")
@SuppressWarnings("restriction")
public final class MoneyTransferRestService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MoneyTransferRestService.class);
    private final static String HOSTNAME = "localhost";
    private final static int PORT = 8080;
    private static final int SHUTDOWN_DELAY_SECONDS = 10;
    private final URI baseUri;
    private final ExecutorService threadExecutor;
    private final HttpServer httpServer;
    private final TransferService transferService;

    /**
     * Constructs a new money transfer REST service
     */
    public MoneyTransferRestService(String hostname, int port, TransferService transferService) {
        final String urlTemplate = new StringBuilder().append("http://").append(hostname).append("/money-transfer").toString();
        final ResourceConfig config = new ResourceConfig(MoneyTransferRestService.class);
        this.baseUri = UriBuilder.fromUri(urlTemplate).port(port).build();
        this.httpServer = JdkHttpServerFactory.createHttpServer(baseUri, config, false);
        this.threadExecutor = Executors.newSingleThreadExecutor();
        this.httpServer.setExecutor(threadExecutor);
        this.transferService = transferService;
    }

    /**
     * Starts the REST service
     */
    private void start() {
        httpServer.start();
        LOGGER.info("REST service started at [{}]", baseUri);
    }

    @GET
    @Path("healthcheck")
    // @Produces(MediaType.TEXT_PLAIN)
    // TODO: NOT WORKING WITH TEXT!!!
    // TODO: NOT WORKING WITH JSON!!!
    public String healthCheck() {
        // return Response.status(Response.Status.OK).entity("good").build();
        return "good";
    }

    /**
     * Shutdown the REST service
     */
    private void stop(int secondsDelay) {
        httpServer.stop(secondsDelay);
        threadExecutor.shutdownNow();
        LOGGER.info("REST service shutdown complete");
    }

    // TODO: SHUTDOWN HOOK NOT WORKING !!!
    public static void main(String[] args) {
        final Bank bank = new Bank(1, "Show Me the Money Bank");
        final TransferServiceInMemoryImpl transferService = new TransferServiceInMemoryImpl(bank);
        final MoneyTransferRestService restService = new MoneyTransferRestService(HOSTNAME, PORT, transferService);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            restService.stop(SHUTDOWN_DELAY_SECONDS);
        }));
        restService.start();
    }
}
