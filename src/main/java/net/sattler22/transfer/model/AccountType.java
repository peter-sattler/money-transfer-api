package net.sattler22.transfer.model;

/**
 * Account Type Business Object
 *
 * @author Pete Sattler
 * @version July 2019
 */
public enum AccountType {

    CHECKING(1, "Checking Account"), 
    SAVINGS(2, "Savings Account");

    private final int typeId;
    private final String description;

    private AccountType(int typeId, String description) {
        this.typeId = typeId;
        this.description = description;
    }

    public int getTypeId() {
        return typeId;
    }

    public static AccountType findAccountType(int code) {
        for(final AccountType accountType : AccountType.values()) {
            if(accountType.getTypeId() == code)
                return accountType;
        }
        throw new IllegalArgumentException(String.format("No account type found for code [%s]", code));
    }

    public String getDescription() {
        return description;
    }
}
