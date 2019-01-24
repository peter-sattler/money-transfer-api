package net.sattler22.transfer.bootstrap;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpServer;

import net.sattler22.transfer.api.MoneyTransferRestService;

/**
 * Revolut&copy; Money Transfer REST Service Bootstrap
 *
 * @author Pete Sattler
 * @version January 2019
 */
@SuppressWarnings("restriction")
public final class MoneyTransferRestServiceBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoneyTransferRestServiceBootstrap.class);
    private static final String BASE_URI = "http://localhost:8080/";
    private static final int SHUTDOWN_DELAY_SECONDS = 5;

    public static void main(String[] args) throws IOException {
        final URI baseUri = UriBuilder.fromPath(BASE_URI).build();
        final ExecutorService threadExecutor = Executors.newSingleThreadExecutor();
        final ResourceConfig config = new ResourceConfig(MoneyTransferRestService.class);
        final HttpServer httpServer = JdkHttpServerFactory.createHttpServer(baseUri, config, false);
        httpServer.setExecutor(threadExecutor);
        httpServer.start();
        LOGGER.info("Money Transfer REST service started at [{}]", baseUri);
        try {
            LOGGER.info("Press [ENTER] to stop the server...");
            System.in.read();
        }
        finally {
            LOGGER.info("Shutting down Money Transfer REST service...");
            httpServer.stop(SHUTDOWN_DELAY_SECONDS);
            threadExecutor.shutdownNow();
            LOGGER.info("Money Transfer REST service shutdown complete");
        }
        System.exit(0);
    }
}
