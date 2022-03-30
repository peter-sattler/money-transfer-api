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
 * @version February 2019
 */
public record TransferServiceInMemoryImpl(Bank bank) implements TransferService {

    private static final Logger logger = LoggerFactory.getLogger(TransferServiceInMemoryImpl.class);

    @Override
    public Bank getBank() {
        return bank;
    }

    @Override
    public Set<Customer> getCustomers() {
        return bank.customers();
    }

    @Override
    public boolean addCustomer(Customer customer) {
        return bank.addCustomer(customer);
    }

    @Override
    public boolean deleteCustomer(Customer customer) {
        final var nbrAccounts = customer.accounts().size();
        if(nbrAccounts > 0)
            throw new IllegalStateException(String.format("%s cannot be deleted because it has %d account%s assigned to it",
                                            customer, nbrAccounts, nbrAccounts == 1 ? "" : "s"));
        return bank.deleteCustomer(customer);
    }

    @Override
    public Optional<Customer> findCustomer(String id) {
        return bank.findCustomer(id);
    }

    @Override
    public boolean addAccount(Account account) {
        return account.owner().addAccount(account);
    }

    @Override
    public boolean deleteAccount(Account account) {
        if(account.balance().compareTo(ZERO) > 0)
            throw new IllegalStateException(String.format("Account #%d cannot be deleted because it contains a non-zero balance", account.number()));
        final var owner = account.owner();
        return owner.deleteAccount(account);
    }

    @Override
    public TransferResult transfer(Customer owner, Account source, Account target, BigDecimal amount) {
        if(source.number() == target.number())
            throw new IllegalArgumentException("Source and target accounts must be different");
        if (amount == null || amount.compareTo(ZERO) <= 0)
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        if (amount.compareTo(source.balance()) > 0)
            throw new IllegalArgumentException("Transfer amount exceeds the amount of available funds");
        //Lock both accounts before making the transfer, but always in the SAME order to avoid deadlocking:
        final var lock1 = source.number() < target.number() ? source.lock() : target.lock();
        final var lock2 = source.number() < target.number() ? target.lock() : source.lock();
        final TransferResult transferResult;
        synchronized (lock1) {
            synchronized (lock2) {
                logger.info("Source before transfer: {}", source);
                logger.info("Target before transfer {}", target);
                source.debit(amount);
                target.credit(amount);
                transferResult = new TransferResult(source, target);
                logger.info("After transfer of ${}, {}", amount, transferResult);
            }
        }
        return transferResult;
    }

    @Override
    public String toString() {
        return String.format("%s [bank=%s]", getClass().getSimpleName(), bank);
    }
}
