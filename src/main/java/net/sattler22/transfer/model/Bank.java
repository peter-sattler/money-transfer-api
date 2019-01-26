package net.sattler22.transfer.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Revolut Banking Institution Business Object
 *
 * @author Pete Sattler
 * @version January 2019
 * @implSpec This class is immutable and thread-safe
 */
public final class Bank implements Serializable {

    private static final long serialVersionUID = 8414304479231837140L;
    private final int id;
    private final String name;
    private final Set<Customer> customers = Collections.synchronizedSet(new HashSet<>());

    /**
     * Constructs a new banking institution
     */
    public Bank(int id, String name) {
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
     * @return True if the customer was added. Otherwise, returns false when the customer already exists.
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
     * @return True if the customer was deleted. Otherwise, returns false when the customer is non-existent.
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
        return String.format("Bank [id=%s, name=%s, customers=%s]", id, name, customers);
    }
}
