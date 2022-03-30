package net.sattler22.transfer.domain;

import java.util.Objects;

/**
 * Address Business Object
 *
 * @author Pete Sattler
 * @version August 2019
 */
public record Address(String street, String city, String state, String zip) {

    public Address {
        Objects.requireNonNull(street, "Street is required");
        Objects.requireNonNull(city, "City is required");
        Objects.requireNonNull(state, "State is required");
        Objects.requireNonNull(zip, "Zip code is required");
    }
}
