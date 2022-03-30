package net.sattler22.transfer.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Property File Utilities
 *
 * @author Pete Sattler
 * @version March 2022
 */
public class PropertyFileUtils {

    private PropertyFileUtils() {
        throw new AssertionError("Cannot be instantiated");
    }

    /**
     * Read a properties file from a resource folder
     *
     * @param <T> The resource class type
     * @param resourceClass The resource class
     * @param resourceName The resource name
     * @return All properties for the resource name
     * @throws IOException If unable to find the resource or there is an error reading from it
     */
    public static <T> Properties readResourceProperties(Class<T> resourceClass, String resourceName) throws IOException {
        try (final var resourceInputStream = resourceClass.getClassLoader().getResourceAsStream(resourceName)) {
            if (resourceInputStream == null)
                throw new FileNotFoundException(String.format("Properties file [%s] not found", resourceName));
            final var properties = new Properties();
            properties.load(resourceInputStream);
            return properties;
        }
    }
}
