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
 * Revolut Money Transfer REST Service Bootstrap
 * <p/>
 * 
 * @implNote This bootstrap utility uses a restricted class from the <code>com.sun</code> package. Normally, this
 *           is not recommended, but in the spirit of keeping this project simple and to the point, a compromise
 *           was made. As a result, you may need to adjust your integrated development environment (IDE)
 *           accordingly. Please see this
 *           <a href="https://stackoverflow.com/questions/41099332/java-httpserver-error-access-restriction-the-type-httpserver-is-not-api">Stack Overflow</a> post
 *           for additional details.
 *
 * @author Pete Sattler
 * @version January 2019
 */
public final class MoneyTransferRestServiceBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoneyTransferRestServiceBootstrap.class);
    private static final String BASE_URI = "http://localhost:8080/";
    private static final int SHUTDOWN_DELAY_SECONDS = 5;

    public static void main(String[] args) throws IOException {
        final URI baseUri = UriBuilder.fromPath(BASE_URI).build();
        final ExecutorService threadExecutor = Executors.newSingleThreadExecutor();
        final ResourceConfig config = new ResourceConfig().register(MoneyTransferRestService.class);
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
