package net.sattler22.transfer.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sattler22.transfer.model.Account;
import net.sattler22.transfer.model.Bank;

/**
 * Revolut&copy; In-Memory Money Transfer Service Implementation
 *
 * @author Pete Sattler
 * @version January 2019
 */
public final class TransferServiceInMemoryImpl implements TransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferServiceInMemoryImpl.class);
    private final Bank bank;
    private final Object lockObject = new Object();

    /**
     * Constructs a new in-memory money transfer service
     */
    public TransferServiceInMemoryImpl(Bank bank) {
        this.bank = bank;
    }

    @Override
    public Bank getBank() {
        return bank;
    }

    @Override
    public BigDecimal checkBalance(Account account) {
        assertIsCustomer(account);
        return account.getBalance();
    }

    @Override
    public TransferResult transfer(Account sourceAccount, Account targetAccount, BigDecimal amount) {
        assertIsCustomer(sourceAccount);
        assertIsCustomer(targetAccount);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        synchronized (lockObject) {
            final Account newSourceAccount = sourceAccount.debit(amount);
            final Account newTargetAccount = targetAccount.credit(amount);
            LOGGER.info("{} transfered ${} from account [#{}] to account [#{}]", sourceAccount.getOwner(), amount,
                    sourceAccount.getNumber(), targetAccount.getNumber());
            return new TransferResult(LocalDateTime.now(), newSourceAccount, newTargetAccount);
        }
    }

    private void assertIsCustomer(Account sourceAccount) {
        if (!bank.isCustomer(sourceAccount.getOwner()))
            throw new IllegalStateException(String.format("%s is not a customer of the bank", sourceAccount.getOwner()));
    }
}
