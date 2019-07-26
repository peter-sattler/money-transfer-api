package net.sattler22.transfer.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import net.jcip.annotations.Immutable;

/**
 * Customer Business Object
 *
 * @author Pete Sattler
 * @version July 2019
 */
@Immutable
@JsonIgnoreProperties({ "accounts" })
public final class Customer implements Serializable {

    private static final long serialVersionUID = -2303189692652134564L;
    private final int id;
    private final String firstName;
    private final String lastName;
    private final Gender gender;
    private final Address address;
    private final String phone;
    private final String email;
    private final String pic;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate birthDate;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate joinDate;
    private final Set<Account> accounts = Collections.synchronizedSet(new HashSet<>());
    private final boolean active;

    /**
     * Constructs a new customer
     */
    @JsonCreator(mode=Mode.PROPERTIES)
    public Customer(@JsonProperty("id") int id,
                    @JsonProperty("firstName") String firstName,
                    @JsonProperty("lastName") String lastName,
                    @JsonProperty("gender") Gender gender,
                    @JsonProperty("address") Address address,
                    @JsonProperty("phone") String phone,
                    @JsonProperty("email") String email,
                    @JsonProperty("pic") String pic,
                    @JsonProperty("birthDate") LocalDate birthDate,
                    @JsonProperty("joinDate") LocalDate joinDate,
                    @JsonProperty("active") boolean active) {
        this.id = id;
        this.firstName = Objects.requireNonNull(firstName, "First name is required");
        this.lastName = Objects.requireNonNull(lastName, "Last name is required");
        this.gender = Objects.requireNonNull(gender, "Gender is required");
        this.address = Objects.requireNonNull(address, "Address is required");
        this.phone = Objects.requireNonNull(phone, "Phone is required");
        this.email = Objects.requireNonNull(email, "Email is required");
        this.pic = pic;
        this.birthDate = Objects.requireNonNull(birthDate, "Date of birth is required");
        this.joinDate = Objects.requireNonNull(joinDate, "Join date is required");
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public Address getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPic() {
        return pic;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    /**
     * Add a new account
     *
     * @return True if the account was added. Otherwise, returns false.
     */
    public boolean addAccount(Account account) {
        return accounts.add(account);
    }

    /**
     * Delete an existing account
     *
     * @return True if the account was deleted. Otherwise, returns false.
     */
    public boolean deleteAccount(Account account) {
        return accounts.remove(account);
    }

    /**
     * Get all of the customer's accounts
     */
    public Set<Account> getAccounts() {
        return Collections.unmodifiableSet(accounts);
    }

    /**
     * Find a specific account
     *
     * @param number The account number
     */
    public Optional<Account> findAccount(int number) {
        return accounts.stream().filter(account -> account.getNumber() == number).findFirst();
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        final Customer that = (Customer) other;
        return this.id == that.id;
    }

    @Override
    public String toString() {
        return String.format("%s [id=%s, firstName=%s, lastName=%s, active=%s]",
                              getClass().getSimpleName(), id, firstName, lastName, active);
    }
}
