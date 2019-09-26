package net.sattler22.transfer.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import net.jcip.annotations.Immutable;

/**
 * Customer Business Object
 *
 * @author Pete Sattler
 * @version September 2019
 */
@Immutable
public final class Customer implements Serializable {

    private static final long serialVersionUID = -2303189692652134564L;
    private final String id;
    private final String firstName;
    private final String lastName;
    private final Gender gender;
    private final Address address;
    private final String phone;
    private final String email;
    private final List<Image> images;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate birthDate;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private final LocalDateTime joinedDate;
    @JsonBackReference
    private final Set<Account> accounts = Collections.synchronizedSet(new HashSet<>());

    /**
     * Constructs a new customer
     */
    @JsonCreator(mode=Mode.PROPERTIES)
    public Customer(@JsonProperty("id") String id,
                    @JsonProperty("firstName") String firstName,
                    @JsonProperty("lastName") String lastName,
                    @JsonProperty("gender") Gender gender,
                    @JsonProperty("address") Address address,
                    @JsonProperty("phone") String phone,
                    @JsonProperty("email") String email,
                    @JsonProperty("images") List<Image> images,
                    @JsonProperty("birthDate") LocalDate birthDate) {
        this.id = Objects.requireNonNull(id, "Customer ID is required");
        this.firstName = Objects.requireNonNull(firstName, "First name is required");
        this.lastName = Objects.requireNonNull(lastName, "Last name is required");
        this.gender = Objects.requireNonNull(gender, "Gender is required");
        this.address = Objects.requireNonNull(address, "Address is required");
        this.phone = Objects.requireNonNull(phone, "Phone is required");
        this.email = email;
        this.images = (images != null) ? images : Collections.emptyList() ;
        this.birthDate = Objects.requireNonNull(birthDate, "Date of birth is required");
        this.joinedDate = LocalDateTime.now();
    }

    public String getId() {
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

    public List<Image> getImages() {
        return Collections.unmodifiableList(images);
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public LocalDateTime getJoinedDate() {
        return joinedDate;
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

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
        return this.id.equals(that.id);
    }

    @Override
    public String toString() {
        return String.format("%s [id=%s, firstName=%s, lastName=%s]",
                              getClass().getSimpleName(), id, firstName, lastName);
    }
}
