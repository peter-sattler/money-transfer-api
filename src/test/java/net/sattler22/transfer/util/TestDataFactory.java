package net.sattler22.transfer.util;

import static net.sattler22.transfer.model.Gender.FEMALE;
import static net.sattler22.transfer.model.Gender.MALE;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import net.sattler22.transfer.model.Address;
import net.sattler22.transfer.model.Customer;
import net.sattler22.transfer.model.Image;

/**
 * Money Transfer Test Data Factory
 *
 * @author Pete Sattler
 * @version August 2019
 */
public final class TestDataFactory {

    /**
     * Bob Wire - to keep the cows from getting out
     */
    public static Customer getBob(String customerId) {
        try {
            final Address address =
                new Address("22 Main Street", "West City", "NJ", "56789");
            final URI uri = new URI("/images/bob-wire.png");
            final List<Image> images =
                Collections.singletonList(new Image(uri, "Bob Wire Image"));
            final LocalDate birthDate = LocalDate.of(1964, 2, 27);
            return new Customer(customerId, "Bob", "Wire", MALE, address,
                                "(111) 111-1111", "bob.wire@yahoo.com", images, birthDate);
        }
        catch(URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Eileen Dover - she's always falling over
     */
    public static Customer getEileen(String customerId) {
        try {
            final Address address =
                new Address("3 Fence Street", "Redtown", "NY", "12345");
            final URI uri = new URI("/images/edover.png");
            final List<Image> images =
                Collections.singletonList(new Image(uri, "Eileen Dover Image"));
            final LocalDate birthDate = LocalDate.of(1965, 1, 15);
            return new Customer(customerId, "Eileen", "Dover", FEMALE, address,
                                "(222) 222-2222", "edover@gmail.com", images, birthDate);
        }
        catch(URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * The fabulous Burt Rentals
     */
    public static Customer getBurt(String customerId) {
        try {
            final Address address =
                new Address("5 Broken Down Lane", "Los Angeles", "CA", "90214");
            final URI uri = new URI("/images/burt-rentals.png");
            final List<Image> images =
                Collections.singletonList(new Image(uri, "Burt Renals Image"));
            final LocalDate birthDate = LocalDate.of(1953, 9, 23);
            return new Customer(customerId, "Burt", "Rentals", MALE, address,
                                "(333) 333-3333", "burt.rentalsr@hotmail.com", images, birthDate);
        }
        catch(URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
