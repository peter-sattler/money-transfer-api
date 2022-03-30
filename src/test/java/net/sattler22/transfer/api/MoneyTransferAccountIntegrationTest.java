package net.sattler22.transfer.api;

import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.HttpHeaders.LOCATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static net.sattler22.transfer.domain.AccountType.CHECKING;
import static net.sattler22.transfer.domain.AccountType.SAVINGS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response.Status;
import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.service.TransferService.TransferResult;
import net.sattler22.transfer.util.TestDataFactory;

/**
 * Money Transfer Account Integration Test Harness
 *
 * @author Pete Sattler
 * @version September 2019
 */
final class MoneyTransferAccountIntegrationTest extends MoneyTransferBaseTestHarness {

    @Test
    void fetchAccountsForCustomerHappyPathTestCase() {
        final var burt = TestDataFactory.burt("234");
        addCustomerImpl(burt);
        final var expectedAccountSize = 2;
        final var checkingAccountBalance = ONE;
        addAccountResponseImpl(CHECKING, burt.id(), checkingAccountBalance);
        final var savingsAccountBalance = ZERO;
        addAccountResponseImpl(SAVINGS, burt.id(), savingsAccountBalance);
        final var response = getAccounts(burt);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));
        final var actual = response.readEntity(new GenericType<Set<Account>>() {});
        assertEquals(expectedAccountSize, actual.size());
        for (final var actualAccount : actual) {
            assertEquals(burt, actualAccount.owner());
            final var expectedBalance = actualAccount.type() == CHECKING ? checkingAccountBalance : savingsAccountBalance;
            assertEquals(expectedBalance, actualAccount.balance());
        }
    }

    @Test
    void fetchAccountsForCustomerNoAccountsTestCase() {
        final var burt = TestDataFactory.burt("234");
        addCustomerImpl(burt);
        final var response = getAccounts(burt);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));
        final var actual = response.readEntity(new GenericType<Set<Account>>() {});
        final Set<Account> expected = new HashSet<>();
        assertEquals(expected, actual);
    }

    @Test
    void fetchAccountsForCustomerNotFoundTestCase() {
        final var burt = TestDataFactory.burt("234");
        final var response = getAccounts(burt);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    void fetchSingleAccountForCustomerHappyPathTestCase() {
        final var eileen = TestDataFactory.eileen("789");
        addCustomerImpl(eileen);
        final var expectedAccountType = CHECKING;
        final var expectedBalance = ONE;
        final var accountNumber = addAccountImpl(expectedAccountType, eileen.id(), expectedBalance).number();
        final var response = getAccount(eileen, accountNumber);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));
        final var actual = response.readEntity(Account.class);
        assertEquals(expectedAccountType, actual.type());
        assertNotNull(actual.owner());
        assertEquals(eileen.id(), actual.owner().id());
        assertEquals(expectedBalance, actual.balance());
    }

    @Test
    void fetchSingleAccountForCustomerCustomerNotFoundTestCase() {
        final var burt = TestDataFactory.burt("456");
        addCustomerImpl(burt);
        final var accountNumber = addAccountImpl(SAVINGS, burt.id(), ONE).number();
        final var response = getAccount(TestDataFactory.bob("123"), accountNumber);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    void fetchSingleAccountForCustomerAccountNotFoundTestCase() {
        final var burt = TestDataFactory.burt("456");
        addCustomerImpl(burt);
        final var response = getAccount(burt, 0);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    void addAccountHappyPathTestCase() {
        final var bob = TestDataFactory.bob("123");
        addCustomerImpl(bob);
        final var response = addAccountResponseImpl(CHECKING, bob.id(), ONE);
        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
        assertNotNull(response.getHeaderString(LOCATION));
    }

    @Test
    void addAccountOwnerNotFoundTestCase() {
        final var response = addAccountResponseImpl(SAVINGS, "123", ZERO);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    void deleteAccountHappyPathTestCase() {
        final var bob = TestDataFactory.bob("123");
        addCustomerImpl(bob);
        final var accountNumber = addAccountImpl(SAVINGS, bob.id(), ZERO).number();
        final var response = deleteAccountImpl(bob, accountNumber);
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    void deleteAccountNotFoundTestCase() {
        final var bob = TestDataFactory.bob("123");
        addCustomerImpl(bob);
        final var response = deleteAccountImpl(bob, 0);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    void deleteAccountNonZeroBalanceTestCase() {
        final var burt = TestDataFactory.burt("234");
        addCustomerImpl(burt);
        final var accountNumber = addAccountImpl(CHECKING, burt.id(), ONE).number();
        final var response = deleteAccountImpl(burt, accountNumber);
        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    void deleteAccountCustomerNotFoundTestCase() {
        final var response = deleteAccountImpl(TestDataFactory.bob("123"), 0);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    void accountTransferHappyPathTestCase() {
        final var bob = TestDataFactory.bob("123");
        addCustomerImpl(bob);
        final var sourceAccountInitialBalance = new BigDecimal("100");
        final var sourceAccount = addAccountImpl(CHECKING, bob.id(), sourceAccountInitialBalance);
        final var targetAccountInitialBalance = new BigDecimal("200");
        final var targetAccount = addAccountImpl(SAVINGS, bob.id(), targetAccountInitialBalance);
        final var transferAmount = new BigDecimal("50");
        final var accountTransferDto = new AccountTransferDto(bob.id(), sourceAccount.number(), targetAccount.number(), transferAmount);
        final var transferVersion = AccountTransferDto.createVersion(sourceAccount, targetAccount);
        final var response = accountTransferImpl(transferVersion, accountTransferDto);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));
        final var transferResult = response.readEntity(TransferResult.class);
        final var expectedSourceAccountBalance = sourceAccountInitialBalance.subtract(transferAmount);
        final var expectedTargetAccountBalance = targetAccountInitialBalance.add(transferAmount);
        assertEquals(expectedSourceAccountBalance, transferResult.source().balance());
        assertEquals(expectedTargetAccountBalance, transferResult.target().balance());
    }

    @Test
    void accountTransferCustomerNotFoundTestCase() {
        final var bob = TestDataFactory.bob("123");
        addCustomerImpl(bob);
        final var sourceAccount = addAccountImpl(CHECKING, bob.id(), ONE);
        final var targetAccount = addAccountImpl(SAVINGS, bob.id(), ZERO);
        final var accountTransferDto = new AccountTransferDto("0", sourceAccount.number(), targetAccount.number(), ONE);
        final var transferVersion = AccountTransferDto.createVersion(sourceAccount, targetAccount);
        final var response = accountTransferImpl(transferVersion, accountTransferDto);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    void accountTransferSourceAccountNotFoundTestCase() {
        final var bob = TestDataFactory.bob("123");
        addCustomerImpl(bob);
        final var sourceAccount = new Account(CHECKING, bob, ONE);
        final var targetAccount = addAccountImpl(SAVINGS, bob.id(), ZERO);
        final var accountTransferDto = new AccountTransferDto(bob.id(), sourceAccount.number(), targetAccount.number(), new BigDecimal("50"));
        final var transferVersion = AccountTransferDto.createVersion(sourceAccount, targetAccount);
        final var response = accountTransferImpl(transferVersion, accountTransferDto);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    void accountTransferTargetAccountNotFoundTestCase() {
        final var bob = TestDataFactory.bob("123");
        addCustomerImpl(bob);
        final var sourceAccount = addAccountImpl(SAVINGS, bob.id(), ONE);
        final var targetAccount = new Account(CHECKING, bob, ZERO);
        final var accountTransferDto = new AccountTransferDto(bob.id(), sourceAccount.number(), targetAccount.number(), new BigDecimal("50"));
        final var transferVersion = AccountTransferDto.createVersion(sourceAccount, targetAccount);
        final var response = accountTransferImpl(transferVersion, accountTransferDto);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    void accountTransferSameSourceAndTargetAccountsTestCase() {
        final var bob = TestDataFactory.bob("123");
        addCustomerImpl(bob);
        final var account = addAccountImpl(CHECKING, bob.id(), new BigDecimal("500"));
        final var accountTransferDto = new AccountTransferDto(bob.id(), account.number(), account.number(), new BigDecimal("100"));
        final var transferVersion = AccountTransferDto.createVersion(account, account);
        final var response = accountTransferImpl(transferVersion, accountTransferDto);
        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    void accountTransferAmountZeroTestCase() {
        final var bob = TestDataFactory.bob("123");
        addCustomerImpl(bob);
        final var sourceAccount = addAccountImpl(CHECKING, bob.id(), new BigDecimal("100"));
        final var targetAccount = addAccountImpl(SAVINGS, bob.id(), new BigDecimal("200"));
        final var accountTransferDto = new AccountTransferDto(bob.id(), sourceAccount.number(), targetAccount.number(), ZERO);
        final var transferVersion = AccountTransferDto.createVersion(sourceAccount, targetAccount);
        final var response = accountTransferImpl(transferVersion, accountTransferDto);
        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    void accountTransferInsufficentFundsTestCase() {
        final var bob = TestDataFactory.bob("123");
        addCustomerImpl(bob);
        final var sourceAccount = addAccountImpl(CHECKING, bob.id(), new BigDecimal("100"));
        final var targetAccount = addAccountImpl(SAVINGS, bob.id(), new BigDecimal("200"));
        final var transferAmount = sourceAccount.balance().add(new BigDecimal("1"));
        final var accountTransferDto = new AccountTransferDto(bob.id(), sourceAccount.number(), targetAccount.number(), transferAmount);
        final var transferVersion = AccountTransferDto.createVersion(sourceAccount, targetAccount);
        final var response = accountTransferImpl(transferVersion, accountTransferDto);
        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }
}
