package net.sattler22.transfer;

import java.io.Serializable;
import java.util.Objects;

/**
 * Revolut&copy; Customer
 * 
 * @author Pete Sattler
 * @version January 2019
 * @implSpec This class is immutable and thread-safe
 */
public final class Customer implements Serializable {

    private static final long serialVersionUID = 5697946845021941295L;
    private final int id;
    private final String firstName;
    private final String lastName;

    /**
     * Constructs a new customer
     */
    public Customer(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = Objects.requireNonNull(firstName, "First name is required");
        this.lastName = Objects.requireNonNull(lastName, "Last name is required");
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        final Customer that = (Customer) other;
        return this.id == that.id;
    }

    @Override
    public String toString() {
        return String.format("Customer [id=%s, firstName=%s, lastName=%s]", id, firstName, lastName);
    }
}
