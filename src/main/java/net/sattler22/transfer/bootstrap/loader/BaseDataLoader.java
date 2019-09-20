package net.sattler22.transfer.bootstrap.loader;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Bootstrap Base Data Loader
 *
 * @author Pete Sattler
 * @version September 2019
 */
public abstract class BaseDataLoader {

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
     * Loads data
     *
     * @return The number of rows loaded
     * @throws IOException If unable to load the data
     */
    protected abstract int load() throws IOException;
}
