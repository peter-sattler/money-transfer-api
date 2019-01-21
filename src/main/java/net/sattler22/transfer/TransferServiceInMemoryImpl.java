package net.sattler22.transfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Revolut&copy; Money Transfer Service In-Memory Implementation
 * 
 * @author Pete Sattler
 * @version January 2019
 */
public final class TransferServiceInMemoryImpl implements TransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferServiceInMemoryImpl.class);
    private final Object lockObject = new Object();

    @Override
    public BigDecimal checkBalance(Account account) {
        return account.getBalance();
    }

    @Override
    public TransferResult transfer(Account sourceAccount, Account targetAccount, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        synchronized (lockObject) {
            final Account newSourceAccount = sourceAccount.debit(amount);
            final Account newTargetAccount = targetAccount.credit(amount);
            LOGGER.info("Transfered [${}] from account [#{}] to account [#{}]", amount, sourceAccount.getNumber(), targetAccount.getNumber());
            return new TransferResult(LocalDateTime.now(), newSourceAccount, newTargetAccount);
        }
    }
}
