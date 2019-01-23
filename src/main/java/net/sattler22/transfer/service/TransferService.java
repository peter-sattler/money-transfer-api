package net.sattler22.transfer.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import net.sattler22.transfer.model.Account;
import net.sattler22.transfer.model.Bank;

/**
 * Revolut&copy; Money Transfer Service Interface
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
     * Check the balance of an account
     */
    BigDecimal checkBalance(Account account);

    /**
     * Transfer money between accounts
     */
    TransferResult transfer(Account sourceAccount, Account targetAccount, BigDecimal amount);

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
            return String.format("TransferResult [dateTime=%s, sourceAccount=%s, targetAccount=%s]", dateTime, sourceAccount,
                    targetAccount);
        }
    }
}
