package net.sattler22.transfer.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import net.jcip.annotations.Immutable;
import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.domain.Bank;
import net.sattler22.transfer.domain.Customer;

/**
 * Money Transfer Service Interface
 *
 * @author Pete Sattler
 * @version April 2020
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
    Optional<Customer> findCustomer(String id);

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
     * @throws IllegalStateException If it has accounts assigned to it
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
     * @return True if the account was deleted. Otherwise, returns false.
     * @throws IllegalStateException If it contains a non-zero balance
     */
    boolean deleteAccount(Account account);

    /**
     * Transfer money between accounts of the same owner
     *
     * @param owner The account owner
     * @param source The source account
     * @param target The target account
     * @param amount The transfer amount
     * @throws IllegalArgumentException If the source and target accounts are the same, the transaction
     *                                  amount is zero or is more than the available amount
     */
    TransferResult transfer(Customer owner, Account source, Account target, BigDecimal amount);

    /**
     * Money transfer result
     */
    @Immutable
    final class TransferResult {

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private final LocalDateTime dateTime;
        private final Account source;
        private final Account target;

        /**
         * Constructs a new transfer result
         *
         * @param source The resulting source account
         * @param target The resulting target account
         */
        @JsonCreator(mode = Mode.PROPERTIES)
        public TransferResult(@JsonProperty("source") Account source,
                              @JsonProperty("target") Account target) {
            this.dateTime = LocalDateTime.now();
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
            return String.format("%s [dateTime=%s, source=%s, target=%s]",
                                 getClass().getSimpleName(), dateTime, source, target);
        }
    }
}
