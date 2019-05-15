package net.sattler22.transfer.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import net.jcip.annotations.Immutable;
import net.sattler22.transfer.model.Account;
import net.sattler22.transfer.model.Bank;
import net.sattler22.transfer.model.Customer;

/**
 * Revolut Money Transfer Service Interface
 *
 * @author Pete Sattler
 * @version May 2019
 */
public interface TransferService {

    /**
     * Get the banking institution
     */
    Bank getBank();

    /**
     * Get all customers of the bank
     */
    Set<Customer> getCustomers();

    /**
     * Find a specific customer
     *
     * @param id The customer identifier
     */
    Optional<Customer> findCustomer(int id);

    /**
     * Add a new customer
     *
     * @return True if the customer was added. Otherwise, return false.
     */
    boolean addCustomer(Customer customer);

    /**
     * Delete an existing customer
     *
     * @return True if the customer was deleted. Otherwise, returns false.
     */
    boolean deleteCustomer(Customer customer);

    /**
     * Add a new account
     *
     * @return True if the account was added. Otherwise, returns false.
     */
    boolean addAccount(Account account);

    /**
     * Delete an existing account
     *
     * @param customerId The customer identifier
     * @param number The account number
     * @return True if the account was deleted. Otherwise, returns false.
     */
    boolean deleteAccount(int customerId, int number);

    /**
     * Transfer money between accounts of the same owner
     *
     * @param owner The account owner
     * @param source The source account
     * @param target The target account
     * @param amount The transfer amount
     * @throws IllegalArgumentException If the transaction amount is not greater than zero
     */
    TransferResult transfer(Customer owner, Account source, Account target, BigDecimal amount) throws IllegalArgumentException;

    /**
     * Money transfer result
     */
    @Immutable
    final class TransferResult implements Serializable {

        private static final long serialVersionUID = -9218940161080465179L;
        private final LocalDateTime dateTime;
        private final Account source;
        private final Account target;

        /**
         * Constructs a new transfer result
         *
         * @param dateTime The date and time of the transfer (or the current date/time if null)
         * @param source The resulting source account
         * @param target The resulting target account
         */
        public TransferResult(LocalDateTime dateTime, Account source, Account target) {
            this.dateTime = (dateTime == null) ? LocalDateTime.now() : dateTime;
            this.source = Objects.requireNonNull(source, "Source account is required");
            this.target = Objects.requireNonNull(target, "Target account is required");
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public Account getSource() {
            return source;
        }

        public Account getTarget() {
            return target;
        }

        @Override
        public String toString() {
            return String.format("TransferResult [dateTime=%s, source=%s, target=%s]", dateTime, source, target);
        }
    }
}
