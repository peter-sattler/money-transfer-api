package net.sattler22.transfer.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.jcip.annotations.Immutable;

/**
 * Banking Institution Business Object
 *
 * @author Pete Sattler
 * @version July 2019
 */
@Immutable
@JsonIgnoreProperties({ "customers" })
public final class Bank implements Serializable {

    private static final long serialVersionUID = -7306722910569018912L;
    private final int id;
    private final String name;
    private final Set<Customer> customers = Collections.synchronizedSet(new HashSet<>());

    /**
     * Constructs a new banking institution
     */
    @JsonCreator(mode=Mode.PROPERTIES)
    public Bank(@JsonProperty("id") int id,
                @JsonProperty("name") String name) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Bank name is required");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Get all customers of the bank
     */
    public Set<Customer> getCustomers() {
        return Collections.unmodifiableSet(customers);
    }

    /**
     * Add a new customer
     *
     * @return True if the customer was added. Otherwise, returns false.
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
    public Optional<Customer> findCustomer(int id) {
        return customers.stream().filter(customer -> customer.getId() == id).findFirst();
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
        return String.format("%s [id=%s, name=%s, customers=%s]", getClass().getSimpleName(), id, name, customers);
    }
}
