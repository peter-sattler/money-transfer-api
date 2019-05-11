package net.sattler22.transfer.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sattler22.transfer.model.Account;
import net.sattler22.transfer.model.Bank;
import net.sattler22.transfer.model.Customer;

/**
 * Revolut Money Transfer Service In-Memory Implementation
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
    public Set<Customer> getCustomers() {
        return bank.getCustomers();
    }

    @Override
    public boolean addCustomer(Customer customer) {
        return bank.addCustomer(customer);
    }

    @Override
    public boolean isCustomer(Customer customer) {
        return bank.isCustomer(customer);
    }

    @Override
    public boolean deleteCustomer(Customer customer) {
        return bank.deleteCustomer(customer);
    }

    @Override
    public Optional<Customer> findCustomer(int id) {
        return bank.findCustomer(id);
    }

    @Override
    public boolean addAccount(Account account) {
        return account.getOwner().addAccount(account);
    }

    @Override
    public boolean deleteAccount(int customerId, int number) {
        Optional<Customer> owner = findCustomer(customerId);
        if (owner.isPresent()) {
            final Account account = new Account(number, owner.get());
            return owner.get().deleteAccount(account);
        }
        return false;
    }

    @Override
    public TransferResult transfer(Customer owner, Account sourceAccount, Account targetAccount, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        synchronized (lockObject) {
            final Account newSourceAccount = sourceAccount.debit(amount);
            final Account newTargetAccount = targetAccount.credit(amount);
            LOGGER.info("{} transfered ${} from account #{} to account #{}", sourceAccount.getOwner(), amount, sourceAccount.getNumber(), targetAccount.getNumber());
            return new TransferResult(LocalDateTime.now(), newSourceAccount, newTargetAccount);
        }
    }
}
