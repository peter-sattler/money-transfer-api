package net.sattler22.transfer.util;

import static net.sattler22.transfer.model.Gender.FEMALE;
import static net.sattler22.transfer.model.Gender.MALE;

import java.time.LocalDate;

import net.sattler22.transfer.model.Address;
import net.sattler22.transfer.model.Customer;

/**
 * Money Transfer Test Data Factory
 *
 * @author Pete Sattler
 * @version July 2019
 */
public final class TestDataFactory {

    /**
     * Bob Wire - to keep the cows from getting out
     */
    public static Customer getBob(int customerId, boolean active) {
        final Address address =
            new Address("22 Main Street", "West City", "NJ", "56789");
        final LocalDate birthDate = LocalDate.of(1964, 2, 27);
        final LocalDate joinDate = LocalDate.of(2011, 11, 15);
        return new Customer(customerId, "Bob", "Wire", MALE, address,
                            "(111) 111-1111", "bobwirer@yahoo.com",
                            "wire-bob.gif", birthDate, joinDate, active);
    }

    /**
     * Eileen Dover - she's always falling over
     */
    public static Customer getEileen(int customerId, boolean active) {
        final Address address =
            new Address("3 Fence Street", "Redtown", "NY", "12345");
        final LocalDate birthDate = LocalDate.of(1965, 1, 15);
        final LocalDate joinDate = LocalDate.of(2012, 10, 6);
        return new Customer(customerId, "Eileen", "Dover", FEMALE, address,
                            "(222) 222-2222", "edover@gmail.com",
                            "dover-eileen.gif", birthDate, joinDate, active);
    }

    /**
     * The fabulous Burt Rentals
     */
    public static Customer getBurt(int customerId, boolean active) {
        final Address address =
            new Address("5 Broken Down Lane", "Los Angeles", "CA", "90214");
        final LocalDate birthDate = LocalDate.of(1953, 9, 23);
        final LocalDate joinDate = LocalDate.of(1999, 4, 2);
        return new Customer(customerId, "Burt", "Rentals", MALE, address,
                           "(333) 333-3333", "burtrentalsr@hotmail.com",
                           "rentals-burt.gif", birthDate, joinDate, active);
    }
}
