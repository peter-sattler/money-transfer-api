package net.sattler22.transfer.bootstrap;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import net.sattler22.transfer.model.Bank;
import net.sattler22.transfer.model.Customer;

/**
 * Bootstrap Customer Data Loader
 *
 * @author Pete Sattler
 * @version July 2019
 */
final class CustomerDataLoader extends BaseDataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerDataLoader.class);
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
        final TypeReference<List<Customer>> typeRef =
            new TypeReference<List<Customer>>() {};
        final List<Customer> customers = objectMapper.readValue(inputFile, typeRef);
        for (Customer customer : customers) {
            bank.addCustomer(customer);
            LOGGER.info("Added {}", customer);
        }
        return customers.size();
    }

    @Override
    public String toString() {
        return String.format("%s [resource=%s, bank=%s]", getClass().getSimpleName(), resource, bank);
    }
}
