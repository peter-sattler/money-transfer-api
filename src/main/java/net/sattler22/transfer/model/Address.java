package net.sattler22.transfer.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.jcip.annotations.Immutable;

/**
 * Address Business Object
 *
 * @author Pete Sattler
 * @version July 2019
 */
@Immutable
public final class Address implements Serializable {

    private static final long serialVersionUID = -2393623619353631538L;
    private final String street;
    private final String city;
    private final String state;
    private final String zip;

    /**
     * Constructs a new address
     *
     * @param street The number and name of the street
     * @param city The name of the city
     * @param state A valid 2 digit state abbreviation
     * @param zip A valid 5 digit ZIP code
     */
    @JsonCreator(mode=Mode.PROPERTIES)
    public Address(@JsonProperty("street") String street,
                   @JsonProperty("city") String city,
                   @JsonProperty("state") String state,
                   @JsonProperty("zip") String zip) {
        this.street = Objects.requireNonNull(street, "Street is required");
        this.city = Objects.requireNonNull(city, "City is required");
        this.state = Objects.requireNonNull(state, "State is required");
        this.zip = Objects.requireNonNull(zip, "Zip code is required");
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    @Override
    public String toString() {
        return String.format("%s [street=%s, city=%s, state=%s, zip=%s]", getClass().getSimpleName(), street, city, state, zip);
    }
}
