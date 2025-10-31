package net.sattler22.transfer.bootstrap;

import com.fasterxml.jackson.core.type.TypeReference;
import net.jcip.annotations.Immutable;
import net.sattler22.transfer.domain.Bank;
import net.sattler22.transfer.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Bootstrap Customer Data Loader
 *
 * @author Pete Sattler
 * @version November 2025
 * @version August 2019
 */
@Immutable
final class CustomerDataLoader extends BaseDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(CustomerDataLoader.class);
    private final Bank bank;

    /**
     * Constructs a new customer data loader
     */
    CustomerDataLoader(Bank bank, String resourceName) {
        super(resourceName);
        this.bank = bank;
    }

    @Override
    int load() throws IOException {
        final File inputFile = new File(resource.getFile());
        final List<Customer> customers = objectMapper.readValue(inputFile, new TypeReference<>() {});
        for (final Customer customer : customers) {
            bank.addCustomer(customer);
            logger.info("Added {}", customer);
        }
        return customers.size();
    }

    @Override
    public String toString() {
        return String.format("%s [resource=%s, bank=%s]", getClass().getSimpleName(), resource, bank);
    }
}
