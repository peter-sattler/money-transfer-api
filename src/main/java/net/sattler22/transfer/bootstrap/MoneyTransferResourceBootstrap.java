package net.sattler22.transfer.bootstrap;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sattler22.transfer.api.MoneyTransferResource;
import net.sattler22.transfer.model.Bank;
import net.sattler22.transfer.service.TransferService;
import net.sattler22.transfer.service.TransferServiceInMemoryImpl;

/**
 * Money Transfer REST Resource Bootstrap
 *
 * @author Pete Sattler
 * @version July 2019
 */
public final class MoneyTransferResourceBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoneyTransferResourceBootstrap.class);
    private static final URI BASE_URI = URI.create("http://localhost:8080/");
    private static final int SHUTDOWN_DELAY_SECONDS = 3;

    public static void main(String[] args) {
        int status = 0;
        final ResourceConfig config = new ResourceConfig();
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                final Bank bank = new Bank(1, "Pete's World Banking Empire");
                final TransferServiceInMemoryImpl transferService = new TransferServiceInMemoryImpl(bank);
                this.bind(transferService).to(TransferService.class);
            }
        });
        config.register(MoneyTransferResource.class);
        final HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, config);
        try {
            LOGGER.info("Money Transfer REST API started at [{}]", BASE_URI);
            httpServer.start();
            LOGGER.info("Press [ENTER] to stop the server...");
            System.in.read();
        }
        catch(IOException e) {
            status++;
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            LOGGER.info("Shutting down Money Transfer REST API...");
            httpServer.shutdown(SHUTDOWN_DELAY_SECONDS, TimeUnit.SECONDS);
            LOGGER.info("Money Transfer REST API shutdown complete, ES={}", status);
            System.exit(status);
        }
    }
}
