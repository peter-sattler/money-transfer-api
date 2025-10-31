package net.sattler22.transfer.api;

import jakarta.ws.rs.core.Response.Status;
import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.domain.AccountType;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.service.TransferService.TransferResult;
import net.sattler22.transfer.util.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Money Transfer Account Integration Tests
 *
 * @author Pete Sattler
 * @version November 2025
 * @since September 2019
 */
final class MoneyTransferAccountIntegrationTest extends MoneyTransferBaseTest {

    @Nested
    @DisplayName("Get Account")
    final class GetTest {
        @Test
        void getAllAccountsForCustomerHappyPathTestCase() {
            final Customer burtRentals = addCustomer(TestData.burtRentals("234"));
            final BigDecimal checkingBalance = BigDecimal.TWO;
            final BigDecimal savingsBalance = BigDecimal.TEN;
            addAccount(burtRentals, AccountType.CHECKING, checkingBalance);
            addAccount(burtRentals, AccountType.SAVINGS, savingsBalance);
            final Set<Account> actualAccounts = getAllAccounts(burtRentals, Status.OK);
            assertEquals(2, actualAccounts.size());
            for (final Account actualAccount : actualAccounts) {
                assertEquals(burtRentals, actualAccount.owner());
                final BigDecimal expectedBalance =
                        actualAccount.type() == AccountType.CHECKING ? checkingBalance : savingsBalance;
                assertEquals(expectedBalance, actualAccount.balance());
            }
        }

        @Test
        void getAllAccountsForCustomerNoAccountsTestCase() {
            final Customer burtRentals = addCustomer(TestData.burtRentals("234"));
            assertTrue(getAllAccounts(burtRentals, Status.OK).isEmpty());  //Returns OK instead NOT_FOUND
        }

        @Test
        void getAllAccountsForCustomerNotFoundTestCase() {
            final Set<Account> accounts = getAllAccounts(TestData.burtRentals("234"), Status.NOT_FOUND);
            assertTrue(accounts.isEmpty());
        }

        @Test
        void getSingleAccountForCustomerHappyPathTestCase() {
            final Customer eileenDover = addCustomer(TestData.eileenDover("789"));
            final AccountType expectedAccountType = AccountType.CHECKING;
            final BigDecimal expectedBalance = BigDecimal.TEN;
            final Account searchAccount = addAccount(eileenDover, expectedAccountType, expectedBalance);
            final Account actual = getAccount(searchAccount, Status.OK);
            assertNotNull(actual);
            assertEquals(expectedAccountType, actual.type());
            assertNotNull(actual.owner());
            assertEquals(eileenDover, actual.owner());
            assertEquals(expectedBalance, actual.balance());
        }

        @Test
        void getSingleAccountForCustomerNotFoundTestCase() {
            final Account newAccount = new Account(TestData.bobWire("123"), AccountType.SAVINGS, BigDecimal.TEN);
            assertNull(getAccount(newAccount, Status.NOT_FOUND));
        }

        @Test
        void getSingleAccountForAccountNotFoundTestCase() {
            final Customer burtRentals = addCustomer(TestData.burtRentals("234"));
            final Account newAccount = new Account(burtRentals, AccountType.CHECKING, BigDecimal.ZERO);
            assertNull(getAccount(newAccount, Status.NOT_FOUND));
        }
    }

    @Nested
    @DisplayName("Add Account")
    final class AddTest {
        @Test
        void addAccountHappyPathTestCase() {
            final Customer bobWire = addCustomer(TestData.bobWire("123"));
            final AccountType expectedAccountType = AccountType.CHECKING;
            final BigDecimal expectedBalance = BigDecimal.ONE;
            final Account actual = addAccount(bobWire, expectedAccountType, expectedBalance, Status.CREATED);
            assertEquals(bobWire, actual.owner());
            assertEquals(expectedAccountType, actual.type());
            assertEquals(expectedBalance, actual.balance());
        }

        @Test
        void addAccountOwnerNotFoundTestCase() {
            final Customer bobWire = TestData.bobWire("123");
            final Account actual = addAccount(bobWire, AccountType.SAVINGS, BigDecimal.ZERO, Status.NOT_FOUND);
            assertNull(actual);
        }
    }

    @Nested
    @DisplayName("Delete Account")
    final class DeleteTest {
        @Test
        void deleteAccountHappyPathTestCase() {
            final Customer bobWire = addCustomer(TestData.bobWire("123"));
            final Account account = addAccount(bobWire, AccountType.SAVINGS, BigDecimal.ZERO);
            deleteAccount(account, Status.NO_CONTENT);
            final Account actual = getAccount(account, Status.NOT_FOUND);
            assertNull(actual);
        }

        @Test
        void deleteAccountNotFoundTestCase() {
            final Customer bobWire = addCustomer(TestData.bobWire("123"));
            final Account account = new Account(bobWire, AccountType.CHECKING, BigDecimal.ZERO);
            deleteAccount(account, Status.NOT_FOUND);
            final Account actual = getAccount(account, Status.NOT_FOUND);
            assertNull(actual);
        }

        @Test
        void deleteAccountNonZeroBalanceTestCase() {
            final Customer burtRentals = addCustomer(TestData.burtRentals("234"));
            final Account expected = addAccount(burtRentals, AccountType.CHECKING, BigDecimal.ONE);
            deleteAccount(expected, Status.CONFLICT);
            final Account actual = getAccount(expected, Status.OK);
            assertNotNull(actual);
            assertEquals(expected, actual);
        }

        @Test
        void deleteAccountCustomerNotFoundTestCase() {
            final Account account = new Account(TestData.bobWire("123"), AccountType.CHECKING, BigDecimal.TEN);
            deleteAccount(account, Status.NOT_FOUND);
            final Account actual = getAccount(account, Status.NOT_FOUND);
            assertNull(actual);
        }
    }

    @Nested
    @DisplayName("Account Transfer")
    final class TransferTest {
        @Test
        void accountTransferHappyPathTestCase() {
            final Customer bobWire = addCustomer(TestData.bobWire("123"));
            final BigDecimal sourceAccountInitialBalance = new BigDecimal("100");
            final BigDecimal targetAccountInitialBalance = new BigDecimal("200");
            final Account sourceAccount = addAccount(bobWire, AccountType.CHECKING, sourceAccountInitialBalance);
            final Account targetAccount = addAccount(bobWire, AccountType.SAVINGS, targetAccountInitialBalance);
            final BigDecimal transferAmount = new BigDecimal("50");
            final TransferResult transferResult = transfer(bobWire, sourceAccount, targetAccount, transferAmount, Status.OK);
            assertNotNull(transferResult);
            assertEquals(sourceAccountInitialBalance.subtract(transferAmount), transferResult.source().balance());
            assertEquals(targetAccountInitialBalance.add(transferAmount), transferResult.target().balance());
        }

        @Test
        void accountTransferCustomerNotFoundTestCase() {
            final Customer bobWire = TestData.bobWire("123");
            final Account sourceAccount = new Account(bobWire, AccountType.CHECKING, BigDecimal.ONE);
            final Account targetAccount = new Account(bobWire, AccountType.SAVINGS, BigDecimal.ZERO);
            final TransferResult transferResult =
                    transfer(bobWire, sourceAccount, targetAccount, BigDecimal.ONE, Status.NOT_FOUND);
            assertNull(transferResult);
        }

        @Test
        void accountTransferSourceAccountNotFoundTestCase() {
            final Customer bobWire = addCustomer(TestData.bobWire("123"));
            final Account sourceAccount = new Account(bobWire, AccountType.CHECKING, BigDecimal.ONE);
            final Account targetAccount = addAccount(bobWire, AccountType.SAVINGS, BigDecimal.ZERO);
            final TransferResult transferResult =
                    transfer(bobWire, sourceAccount, targetAccount, new BigDecimal("50"), Status.NOT_FOUND);
            assertNull(transferResult);
        }

        @Test
        void accountTransferTargetAccountNotFoundTestCase() {
            final Customer bobWire = addCustomer(TestData.bobWire("123"));
            final Account sourceAccount = addAccount(bobWire, AccountType.SAVINGS, BigDecimal.ONE);
            final Account targetAccount = new Account(bobWire, AccountType.CHECKING, BigDecimal.ZERO);
            final TransferResult transferResult =
                    transfer(bobWire, sourceAccount, targetAccount, new BigDecimal("75"), Status.NOT_FOUND);
            assertNull(transferResult);
        }

        @Test
        void accountTransferSameSourceAndTargetAccountsTestCase() {
            final Customer bobWire = addCustomer(TestData.bobWire("123"));
            final Account account = addAccount(bobWire, AccountType.CHECKING, new BigDecimal("500"));
            final TransferResult transferResult =
                transfer(bobWire, account, account, new BigDecimal("100"), Status.CONFLICT);
            assertNull(transferResult);
        }

        @Test
        void accountTransferAmountZeroTestCase() {
            final Customer bobWire = addCustomer(TestData.bobWire("123"));
            final Account sourceAccount = addAccount(bobWire, AccountType.CHECKING, new BigDecimal("100"));
            final Account targetAccount = addAccount(bobWire, AccountType.SAVINGS, new BigDecimal("200"));
            final TransferResult transferResult =
                transfer(bobWire, sourceAccount, targetAccount, BigDecimal.ZERO, Status.CONFLICT);
            assertNull(transferResult);
        }

        @Test
        void accountTransferInsufficientFundsTestCase() {
            final Customer bobWire = addCustomer(TestData.bobWire("123"));
            final Account sourceAccount = addAccount(bobWire, AccountType.CHECKING, new BigDecimal("100"));
            final Account targetAccount = addAccount(bobWire, AccountType.SAVINGS, new BigDecimal("200"));
            final BigDecimal transferAmount = sourceAccount.balance().add(BigDecimal.ONE);
            final TransferResult transferResult =
                transfer(bobWire, sourceAccount, targetAccount, transferAmount, Status.CONFLICT);
            assertNull(transferResult);
        }
    }
}
