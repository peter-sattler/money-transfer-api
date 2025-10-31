package net.sattler22.transfer.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

/**
 * Bootstrap Base Data Loader
 *
 * @implSpec All subclasses are required to be thread-safe
 * @author Pete Sattler
 * @version August 2019
 */
abstract sealed class BaseDataLoader permits AccountDataLoader, CustomerDataLoader {

    protected final URL resource;
    protected final ObjectMapper objectMapper;

    /**
     * Constructs a new base data loader
     */
    protected BaseDataLoader(String resourceName) {
        this.resource = getClass().getClassLoader().getResource(resourceName);
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Loads the data
     *
     * @return The number of rows loaded
     * @throws IOException If unable to load the data
     */
    abstract int load() throws IOException;
}
