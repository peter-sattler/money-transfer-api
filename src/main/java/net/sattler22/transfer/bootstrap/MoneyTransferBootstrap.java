package net.sattler22.transfer.bootstrap;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sattler22.transfer.api.MoneyTransferResourceImpl;
import net.sattler22.transfer.domain.Bank;
import net.sattler22.transfer.service.TransferService;
import net.sattler22.transfer.service.TransferServiceInMemoryImpl;
import net.sattler22.transfer.util.PropertyFileUtils;

/**
 * Money Transfer REST Server Bootstrap
 *
 * @author Pete Sattler
 * @version July 2019
 */
public final class MoneyTransferBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(MoneyTransferBootstrap.class);

    /**
     * Handle the Money Transfer REST API server life cycle
     */
    public static void main(String[] args) {
        var status = 0;
        try {
            final var bootstrapConfig = getBootstrapConfig();
            final var bank = new Bank(1, "Pete's World Banking Empire");
            final var transferService = new TransferServiceInMemoryImpl(bank);
            final var jerseyConfig = getJerseyConfig(transferService);
            logger.info("Money Transfer REST API started at [{}{}]", bootstrapConfig.baseUri(), bootstrapConfig.basePath);
            startHttpServer(bootstrapConfig, jerseyConfig, bank, transferService);
        }
        catch(IOException e) {
            status++;
            logger.error(e.getMessage(), e);
        }
        finally {
            logger.info("Money Transfer REST API shutdown complete, ES={}", status);
            System.exit(status);
        }
    }

    private static void startHttpServer(BootstrapConfig bootstrapConfig, ResourceConfig jerseyConfig, Bank bank, TransferService transferService) throws IOException {
        final var httpServer = GrizzlyHttpServerFactory.createHttpServer(bootstrapConfig.baseUri(), jerseyConfig);
        try {
            httpServer.start();
            loadCustomers(bank, bootstrapConfig.customerDataFile());
            loadAccounts(transferService, bootstrapConfig.accountDataFile());
            logger.info("Press [ENTER] to stop the server...");
            System.in.read();
        }
        finally {
            logger.info("Shutting down Money Transfer REST API...");
            httpServer.shutdown(bootstrapConfig.shutdownDelaySecs(), TimeUnit.SECONDS);
        }
    }


    private static BootstrapConfig getBootstrapConfig() throws IOException {
        final var appProps = PropertyFileUtils.readResourceProperties(MoneyTransferBootstrap.class, "app.properties");
        final var basePath = appProps.getProperty("base.path");
        final var bootstrapProps = PropertyFileUtils.readResourceProperties(MoneyTransferBootstrap.class, "bootstrap.properties");
        final var baseUri = URI.create(bootstrapProps.getProperty("base.uri"));
        final var accountDataFile = bootstrapProps.getProperty("account.data.file");
        final var customerDataFile = bootstrapProps.getProperty("customer.data.file");
        final var shutdownDelaySecs = Integer.parseInt(bootstrapProps.getProperty("shutdown.delay.secs"));
        return new BootstrapConfig(baseUri, basePath, accountDataFile, customerDataFile, shutdownDelaySecs);
    }

    private record BootstrapConfig(URI baseUri, String basePath, String accountDataFile, String customerDataFile, int shutdownDelaySecs) {
    }

    private static ResourceConfig getJerseyConfig(TransferService transferService) {
        final var jerseyConfig = new ResourceConfig();
        jerseyConfig.register(new AbstractBinder() {
            @Override
            protected void configure() {
                this.bind(transferService).to(TransferService.class);
            }
        });
        jerseyConfig.register(CorsFilter.class);
        jerseyConfig.register(MoneyTransferResourceImpl.class);
        return jerseyConfig;
    }

    private static void loadCustomers(Bank bank, String resourceName) throws IOException {
        final var dataLoader = new CustomerDataLoader(bank, resourceName);
        final var nbrCustomers = dataLoader.load();
        logger.info("Loaded [{}] customers", nbrCustomers);
    }

    private static void loadAccounts(TransferService transferService, String resourceName) throws IOException {
        final var dataLoader = new AccountDataLoader(transferService, resourceName);
        final var nbrAccounts = dataLoader.load();
        logger.info("Loaded [{}] accounts", nbrAccounts);
    }
}
