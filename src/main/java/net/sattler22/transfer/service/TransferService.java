package net.sattler22.transfer.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import net.sattler22.transfer.model.Account;
import net.sattler22.transfer.model.Bank;
import net.sattler22.transfer.model.Customer;

/**
 * Revolut Money Transfer Service Interface
 *
 * @author Pete Sattler
 * @version January 2019
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
     * Add a new customer
     */
    boolean addCustomer(Customer customer);

    /**
     * Bank customer existence check
     */
    boolean isCustomer(Customer customer);

    /**
     * Delete an existing customer
     * 
     * @return True if the customer was deleted. Otherwise, returns false when the customer is non-existent.
     */
    boolean deleteCustomer(Customer customer);

    /**
     * Find a specific customer
     * 
     * @param id The customer identifier
     */
    Optional<Customer> findCustomer(int id);

    /**
     * Add a new account
     * 
     * @return True if the account was added. Otherwise, returns false when the account already exists.
     */
    boolean addAccount(Account account);

    /**
     * Delete an existing account
     * 
     * @return True if the account was deleted. Otherwise, returns false when the account is non-existent.
     */
    boolean deleteAccount(int customerId, int number);

    /**
     * Transfer money between accounts of the same owner
     * 
     * @param customerId The account owner's identifier
     * @param sourceNumber The source account number
     * @param targetNumber The target account number
     * @throws IllegalArgumentException If the transaction amount is <= 0
     */
    TransferResult transfer(Customer owner, Account sourceAccount, Account targetAccount, BigDecimal amount);

    /**
     * Results of a money transfer
     *
     * @implSpec This class is immutable and thread-safe
     */
    final class TransferResult implements Serializable {

        private static final long serialVersionUID = -9218940161080465179L;
        private final LocalDateTime dateTime;
        private final Account sourceAccount;
        private final Account targetAccount;

        /**
         * Constructs a new transfer result
         *
         * @param dateTime The date and time of the transfer (current date/time if null)
         * @param sourceAccount The resulting source account
         * @param targetAccount The resulting target account
         */
        public TransferResult(LocalDateTime dateTime, Account sourceAccount, Account targetAccount) {
            this.dateTime = (dateTime == null) ? LocalDateTime.now() : dateTime;
            this.sourceAccount = Objects.requireNonNull(sourceAccount, "Source account is required");
            this.targetAccount = Objects.requireNonNull(targetAccount, "Target account is required");
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public Account getSourceAccount() {
            return sourceAccount;
        }

        public Account getTargetAccount() {
            return targetAccount;
        }

        @Override
        public String toString() {
            return String.format("TransferResult [dateTime=%s, sourceAccount=%s, targetAccount=%s]", dateTime, sourceAccount, targetAccount);
        }
    }
}
