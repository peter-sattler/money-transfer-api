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
 * @version May 2019
 */
public final class TransferServiceInMemoryImpl implements TransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferServiceInMemoryImpl.class);
    private final Bank bank;

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
        return findCustomer(customerId).map(owner -> owner.deleteAccount(new Account(number, owner))).orElse(false);
    }

    @Override
    public TransferResult transfer(Customer owner, Account source, Account target, BigDecimal amount) throws IllegalArgumentException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        //Lock both accounts before making the transfer, but always in the SAME order to avoid deadlocking:
        final Object lock1 = source.getNumber() < target.getNumber() ? source.getLock() : target.getLock();
        final Object lock2 = source.getNumber() < target.getNumber() ? target.getLock() : source.getLock();
        synchronized (lock1) {
            synchronized (lock2) {
                final Account newSource = source.debit(amount);
                final Account newTarget = target.credit(amount);
                LOGGER.info("{} transfered ${} from account #{} to account #{}",
                             source.getOwner(), amount, source.getNumber(), target.getNumber());
                return new TransferResult(LocalDateTime.now(), newSource, newTarget);
            }
        }
    }
}
