package net.sattler22.transfer.service;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.domain.Bank;
import net.sattler22.transfer.domain.Customer;

/**
 * Money Transfer Service In-Memory Implementation
 *
 * @author Pete Sattler
 * @version September 2019
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
    public boolean deleteCustomer(Customer customer) throws IllegalStateException {
        final int nbrAccounts = customer.getAccounts().size();
        if(nbrAccounts > 0) {
            final String nbrAccountsWord = nbrAccounts == 1 ? "account" : "accounts";
            throw new IllegalStateException(String.format("%s cannot be deleted because it has %d %s assigned to it", customer, nbrAccounts, nbrAccountsWord));
        }
        return bank.deleteCustomer(customer);
    }

    @Override
    public Optional<Customer> findCustomer(String id) {
        return bank.findCustomer(id);
    }

    @Override
    public boolean addAccount(Account account) {
        return account.getOwner().addAccount(account);
    }

    @Override
    public boolean deleteAccount(Account account) throws IllegalStateException {
        if(account.getBalance().compareTo(ZERO) > 0)
            throw new IllegalStateException(String.format("Account #%d cannot be deleted because it contains a non-zero balance", account.getNumber()));
        final Customer owner = account.getOwner();
        return owner.deleteAccount(account);
    }

    @Override
    public TransferResult transfer(Customer owner, Account source, Account target, BigDecimal amount) throws IllegalArgumentException {
        if(source.getNumber() == target.getNumber())
            throw new IllegalArgumentException("Source and target accounts must be different");
        if (amount == null || amount.compareTo(ZERO) <= 0)
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        if (amount.compareTo(source.getBalance()) > 0)
            throw new IllegalArgumentException("Transfer amount exceeds the amount of available funds");
        //Lock both accounts before making the transfer, but always in the SAME order to avoid deadlocking:
        final Object lock1 = source.getNumber() < target.getNumber() ? source.getLock() : target.getLock();
        final Object lock2 = source.getNumber() < target.getNumber() ? target.getLock() : source.getLock();
        synchronized (lock1) {
            synchronized (lock2) {
                source.debit(amount);
                target.credit(amount);
                LOGGER.info("{} transferred ${} from account #{} to account #{}",
                            source.getOwner(), amount, source.getNumber(), target.getNumber());
                return new TransferResult(source, target);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s [bank=%s]", getClass().getSimpleName(), bank);
    }
}
