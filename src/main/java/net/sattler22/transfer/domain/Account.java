package net.sattler22.transfer.domain;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.jcip.annotations.ThreadSafe;

/**
 * Account Business Object
 *
 * @author Pete Sattler
 * @version February 2019
 */
@ThreadSafe
public final class Account {

    private static final AtomicInteger numberCounter = new AtomicInteger();
    private final int number;
    private final AccountType type;
    @JsonManagedReference
    private final Customer owner;
    private volatile BigDecimal balance;
    private final AtomicLong version;
    @JsonIgnore
    private final Object lock = new Object();

    /**
     * Constructs a new account
     */
    public Account(AccountType type, Customer owner, BigDecimal balance) {
        this(numberCounter.incrementAndGet(), type, owner, balance, 0L);
    }

    /**
     * Reconstructs an existing account
     */
    @JsonCreator(mode = Mode.PROPERTIES)
    private Account(@JsonProperty("number") int number, @JsonProperty("type") AccountType type,
                    @JsonProperty("owner") Customer owner, @JsonProperty("balance") BigDecimal balance,
                    @JsonProperty("version") long version) {
        this.number = number;
        this.type = Objects.requireNonNull(type, "Account type is required");
        this.owner = Objects.requireNonNull(owner, "Account owner is required");
        this.balance = (balance != null) ? balance : ZERO;
        this.version = new AtomicLong(version);
    }

    /**
     * Credit funds to the account
     */
    public void credit(BigDecimal amount) {
        if (amount.compareTo(ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");
        synchronized (lock) {
            this.balance = balance.add(amount);
            this.version.incrementAndGet();
        }
    }

    /**
     * Debit funds from the account
     */
    public void debit(BigDecimal amount) {
        synchronized (lock) {
            final BigDecimal newBalance = balance.subtract(amount);
            if (newBalance.compareTo(ZERO) < 0)
                throw new IllegalStateException("Transfer would lead to an overdrawn account");
            this.balance = newBalance;
            this.version.incrementAndGet();
        }
    }

    @JsonGetter
    public int number() {
        return number;
    }

    @JsonGetter
    public AccountType type() {
        return type;
    }

    @JsonGetter
    public Customer owner() {
        return owner;
    }

    @JsonGetter
    public BigDecimal balance() {
        return balance;
    }

    @JsonGetter
    public long version() {
        return version.get();
    }

    public Object lock() {
        return lock;
    }

    public static Optional<Account> find(Customer owner, int number) {
        return owner.findAccount(number);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(number);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        final var that = (Account) other;
        return this.number == that.number;
    }

    @Override
    public String toString() {
        return String.format("%s [number=%s, type=%s, owner=%s, balance=%s, version=%s]", getClass().getSimpleName(), number, type, owner, balance, version);
    }
}
