package net.sattler22.transfer.util;

import static net.sattler22.transfer.domain.Gender.FEMALE;
import static net.sattler22.transfer.domain.Gender.MALE;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collections;

import net.sattler22.transfer.domain.Address;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.domain.Image;

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
    public static Customer bob(String customerId) {
        try {
            final var address = new Address("22 Main Street", "West City", "NJ", "56789");
            final var uri = new URI("/images/bob-wire.png");
            final var images = Collections.singletonList(new Image(uri, "Bob Wire Image"));
            final var birthDate = LocalDate.of(1964, 2, 27);
            return new Customer(customerId, "Bob", "Wire", MALE, address, "(111) 111-1111", "bob.wire@yahoo.com", images, birthDate);
        }
        catch(URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Eileen Dover - she's always falling over
     */
    public static Customer eileen(String customerId) {
        try {
            final var address = new Address("3 Fence Street", "Redtown", "NY", "12345");
            final var uri = new URI("/images/edover.png");
            final var images = Collections.singletonList(new Image(uri, "Eileen Dover Image"));
            final var birthDate = LocalDate.of(1965, 1, 15);
            return new Customer(customerId, "Eileen", "Dover", FEMALE, address, "(222) 222-2222", "edover@gmail.com", images, birthDate);
        }
        catch(URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * The fabulous Burt Rentals
     */
    public static Customer burt(String customerId) {
        try {
            final var address = new Address("5 Broken Down Lane", "Los Angeles", "CA", "90214");
            final var uri = new URI("/images/burt-rentals.png");
            final var images = Collections.singletonList(new Image(uri, "Burt Rentals Image"));
            final var birthDate = LocalDate.of(1953, 9, 23);
            return new Customer(customerId, "Burt", "Rentals", MALE, address, "(333) 333-3333", "burt.rentals@hotmail.com", images, birthDate);
        }
        catch(URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
