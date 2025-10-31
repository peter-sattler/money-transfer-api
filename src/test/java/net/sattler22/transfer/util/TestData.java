package net.sattler22.transfer.util;

import net.sattler22.transfer.domain.Address;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.domain.Gender;
import net.sattler22.transfer.domain.Image;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Money Transfer Test Data
 *
 * @author Pete Sattler
 * @version November 2025
 * @since August 2019
 */
public final class TestData {

    /**
     * Bob Wire (to keep the cows from getting out)
     */
    public static Customer bobWire(String customerId) {
        try {
            final Address address = new Address("22 Main Street", "West City", "NJ", "56789");
            final URI uri = new URI("/images/bobWire-wire.png");
            final List<Image> images = Collections.singletonList(new Image(uri, "Bob Wire Image"));
            final LocalDate birthDate = LocalDate.of(1984, 2, 27);
            return new Customer(customerId, "Bob", "Wire", Gender.MALE, address, "(111) 111-1111", "bobWire.wire@yahoo.com", images, birthDate);
        }
        catch(URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Eileen Dover (she's always falling over)
     */
    public static Customer eileenDover(String customerId) {
        try {
            final Address address = new Address("3 Fence Street", "Red Town", "NY", "12345");
            final URI uri = new URI("/images/edover.png");
            final List<Image> images = Collections.singletonList(new Image(uri, "Eileen Dover Image"));
            final LocalDate birthDate = LocalDate.of(2005, 1, 15);
            return new Customer(customerId, "Eileen", "Dover", Gender.FEMALE, address, "(222) 222-2222", "edover@gmail.com", images, birthDate);
        }
        catch(URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * The fabulous Burt Rentals (BOM-ONE)
     */
    public static Customer burtRentals(String customerId) {
        try {
            final Address address = new Address("5 Broken Down Lane", "Los Angeles", "CA", "90214");
            final URI uri = new URI("/images/burtRentals-rentals.png");
            final List<Image> images = Collections.singletonList(new Image(uri, "Burt Rentals Image"));
            final LocalDate birthDate = LocalDate.of(1993, 9, 23);
            return new Customer(customerId, "Burt", "Rentals", Gender.MALE, address, "(333) 333-3333", "burtRentals.rentals@hotmail.com", images, birthDate);
        }
        catch(URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
