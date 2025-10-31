package net.sattler22.transfer.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.jcip.annotations.Immutable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Banking Institution Business Object
 *
 * @author Pete Sattler
 * @version November 2025
 * @since February 2019
 */
@Immutable
public final class Bank {

    private final int id;
    private final String name;

    @JsonBackReference
    private final Set<Customer> customers = Collections.synchronizedSet(new HashSet<>());

    /**
     * Constructs a new banking institution
     */
    @JsonCreator(mode = Mode.PROPERTIES)
    public Bank(@JsonProperty("id") int id, @JsonProperty("name") String name) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Bank name is required");
    }

    @JsonGetter
    public int id() {
        return id;
    }

    @JsonGetter
    public String name() {
        return name;
    }

    /**
     * Get all customers of the bank
     */
    public Set<Customer> customers() {
        return Set.copyOf(customers);
    }

    /**
     * Add a new customer
     *
     * @return True if the customer was added. Otherwise, returns false if customer was already added.
     */
    public boolean addCustomer(Customer customer) {
        return customers.add(customer);
    }

    /**
     * Bank customer existence check
     */
    public boolean isCustomer(Customer customer) {
        return customers.contains(customer);
    }

    /**
     * Delete an existing customer
     *
     * @return True if the customer was deleted. Otherwise, returns false.
     */
    public boolean deleteCustomer(Customer customer) {
        return customers.remove(customer);
    }

    /**
     * Find a specific customer
     *
     * @param id The customer identifier
     */
    public Optional<Customer> findCustomer(String id) {
        return customers.stream().filter(customer -> customer.id().equals(id)).findFirst();
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
        final Bank that = (Bank) other;
        return this.id == that.id;
    }

    @Override
    public String toString() {
        return String.format("%s [id=%s, name=%s]", getClass().getSimpleName(), id, name);
    }
}
