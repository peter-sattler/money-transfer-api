package net.sattler22.transfer.api;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static net.sattler22.transfer.domain.AccountType.CHECKING;
import static net.sattler22.transfer.domain.AccountType.SAVINGS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.domain.AccountType;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.dto.AccountTransferDTO;
import net.sattler22.transfer.service.TransferService.TransferResult;
import net.sattler22.transfer.util.TestDataFactory;

/**
 * Money Transfer Account Integration Test Harness
 *
 * @author Pete Sattler
 * @version September 2019
 */
public final class MoneyTransferAccountIntegrationTestHarness extends MoneyTransferBaseTestHarness {

    @Test
    public void fetchAccountsForCustomerHappyPathTestCase() {
        final Customer burt = TestDataFactory.getBurt("234");
        addCustomerImpl(burt);
        final int expectedAccountSize = 2;
        final BigDecimal checkingAccountBalance = ONE;
        addAccountResponseImpl(CHECKING, burt.getId(), checkingAccountBalance);
        final BigDecimal savingsAccountBalance = ZERO;
        addAccountResponseImpl(SAVINGS, burt.getId(), savingsAccountBalance);
        final Response response = getAccounts(burt);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));
        final Set<Account> actual = response.readEntity(new GenericType<Set<Account>>() {});
        assertEquals(expectedAccountSize, actual.size());
        for (Account actualAccount : actual) {
            assertEquals(burt, actualAccount.getOwner());
            final BigDecimal expectedBalance = actualAccount.getType() == CHECKING ? checkingAccountBalance : savingsAccountBalance;
            assertEquals(expectedBalance, actualAccount.getBalance());
        }
    }

    @Test
    public void fetchAccountsForCustomerNoAccountsTestCase() {
        final Customer burt = TestDataFactory.getBurt("234");
        addCustomerImpl(burt);
        final Response response = getAccounts(burt);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));
        final Set<Account> actual = response.readEntity(new GenericType<Set<Account>>() {});
        final Set<Account> expected = new HashSet<>();
        assertEquals(expected, actual);
    }

    @Test
    public void fetchAccountsForCustomerNotFoundTestCase() {
        final Customer burt = TestDataFactory.getBurt("234");
        final Response response = getAccounts(burt);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    public void fetchSingleAccountForCustomerHappyPathTestCase() {
        final Customer eileen = TestDataFactory.getEileen("789");
        addCustomerImpl(eileen);
        final AccountType expectedAccountType = CHECKING;
        final BigDecimal expectedBalance = ONE;
        final int accountNumber = addAccountImpl(expectedAccountType, eileen.getId(), expectedBalance).getNumber();
        final Response response = getAccount(eileen, accountNumber);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));
        final Account actual = response.readEntity(Account.class);
        assertEquals(expectedAccountType, actual.getType());
        assertNotNull(actual.getOwner());
        assertEquals(eileen.getId(), actual.getOwner().getId());
        assertEquals(expectedBalance, actual.getBalance());
    }

    @Test
    public void fetchSingleAccountForCustomerCustomerNotFoundTestCase() {
        final Customer burt = TestDataFactory.getBurt("456");
        addCustomerImpl(burt);
        final int accountNumber = addAccountImpl(SAVINGS, burt.getId(), ONE).getNumber();
        final Response response = getAccount(TestDataFactory.getBob("123"), accountNumber);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    public void fetchSingleAccountForCustomerAccountNotFoundTestCase() {
        final Customer burt = TestDataFactory.getBurt("456");
        addCustomerImpl(burt);
        final Response response = getAccount(burt, 0);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    public void addAccountHappyPathTestCase() {
        final Customer bob = TestDataFactory.getBob("123");
        addCustomerImpl(bob);
        final Response response = addAccountResponseImpl(CHECKING, bob.getId(), ONE);
        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
        assertNotNull(response.getHeaderString(LOCATION));
    }

    @Test
    public void addAccountOwnerNotFoundTestCase() {
        final Response response = addAccountResponseImpl(SAVINGS, "123", ZERO);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    public void deleteAccountHappyPathTestCase() {
        final Customer bob = TestDataFactory.getBob("123");
        addCustomerImpl(bob);
        final int accountNumber = addAccountImpl(SAVINGS, bob.getId(), ZERO).getNumber();
        final Response response = deleteAccountImpl(bob, accountNumber);
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    public void deleteAccountNotFoundTestCase() {
        final Customer bob = TestDataFactory.getBob("123");
        addCustomerImpl(bob);
        final Response response = deleteAccountImpl(bob, 0);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    public void deleteAccountNonZeroBalanceTestCase() {
        final Customer burt = TestDataFactory.getBurt("234");
        addCustomerImpl(burt);
        final int accountNumber = addAccountImpl(CHECKING, burt.getId(), ONE).getNumber();
        final Response response = deleteAccountImpl(burt, accountNumber);
        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    public void deleteAccountCustomerNotFoundTestCase() {
        final Response response = deleteAccountImpl(TestDataFactory.getBob("123"), 0);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    public void accountTransferHappyPathTestCase() {
        final Customer bob = TestDataFactory.getBob("123");
        addCustomerImpl(bob);
        final BigDecimal sourceAccountInitialBalance = new BigDecimal("100");
        final Account sourceAccount = addAccountImpl(CHECKING, bob.getId(), sourceAccountInitialBalance);
        final BigDecimal targetAccountInitialBalance = new BigDecimal("200");
        final Account targetAccount = addAccountImpl(SAVINGS, bob.getId(), targetAccountInitialBalance);
        final BigDecimal transferAmount = new BigDecimal("50");
        final AccountTransferDTO accountTransferDTO =
            new AccountTransferDTO(bob.getId(), sourceAccount, targetAccount, transferAmount);
        final Response response = accountTransferImpl(accountTransferDTO);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));
        final TransferResult transferResult = response.readEntity(TransferResult.class);
        final BigDecimal expectedSourceAccountBalance = sourceAccountInitialBalance.subtract(transferAmount);
        final BigDecimal expectedTargetAccountBalance = targetAccountInitialBalance.add(transferAmount);
        assertEquals(expectedSourceAccountBalance, transferResult.getSource().getBalance());
        assertEquals(expectedTargetAccountBalance, transferResult.getTarget().getBalance());
    }

    @Test
    public void accountTransferCustomerNotFoundTestCase() {
        final Customer bob = TestDataFactory.getBob("123");
        addCustomerImpl(bob);
        final Account sourceAccount = addAccountImpl(CHECKING, bob.getId(), ONE);
        final Account targetAccount = addAccountImpl(SAVINGS, bob.getId(), ZERO);
        final AccountTransferDTO accountTransferDTO = new AccountTransferDTO("0", sourceAccount, targetAccount, ONE);
        final Response response = accountTransferImpl(accountTransferDTO);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    public void accountTransferSourceAccountNotFoundTestCase() {
        final Customer bob = TestDataFactory.getBob("123");
        addCustomerImpl(bob);
        final Account sourceAccount = new Account(CHECKING, bob, ONE);
        final Account targetAccount = addAccountImpl(SAVINGS, bob.getId(), ZERO);
        final AccountTransferDTO accountTransferDTO =
            new AccountTransferDTO(bob.getId(), sourceAccount, targetAccount, new BigDecimal("50"));
        final Response response = accountTransferImpl(accountTransferDTO);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    public void accountTransferTargetAccountNotFoundTestCase() {
        final Customer bob = TestDataFactory.getBob("123");
        addCustomerImpl(bob);
        final Account sourceAccount = addAccountImpl(SAVINGS, bob.getId(), ONE);
        final Account targetAccount = new Account(CHECKING, bob, ZERO);
        final AccountTransferDTO accountTransferDTO =
            new AccountTransferDTO(bob.getId(), sourceAccount, targetAccount, new BigDecimal("50"));
        final Response response = accountTransferImpl(accountTransferDTO);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    public void accountTransferSameSourceAndTargetAccountsTestCase() {
        final Customer bob = TestDataFactory.getBob("123");
        addCustomerImpl(bob);
        final Account account = addAccountImpl(CHECKING, bob.getId(), new BigDecimal("500"));
        final AccountTransferDTO accountTransferDTO = new AccountTransferDTO(bob.getId(), account, account, new BigDecimal("100"));
        final Response response = accountTransferImpl(accountTransferDTO);
        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    public void accountTransferAmountZeroTestCase() {
        final Customer bob = TestDataFactory.getBob("123");
        addCustomerImpl(bob);
        final Account sourceAccount = addAccountImpl(CHECKING, bob.getId(), new BigDecimal("100"));
        final Account targetAccount = addAccountImpl(SAVINGS, bob.getId(), new BigDecimal("200"));
        final AccountTransferDTO accountTransferDTO = new AccountTransferDTO(bob.getId(), sourceAccount, targetAccount, ZERO);
        final Response response = accountTransferImpl(accountTransferDTO);
        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }

    @Test
    public void accountTransferInsufficentFundsTestCase() {
        final Customer bob = TestDataFactory.getBob("123");
        addCustomerImpl(bob);
        final Account sourceAccount = addAccountImpl(CHECKING, bob.getId(), new BigDecimal("100"));
        final Account targetAccount = addAccountImpl(SAVINGS, bob.getId(), new BigDecimal("200"));
        final BigDecimal transferAmount = sourceAccount.getBalance().add(new BigDecimal("1"));
        final AccountTransferDTO accountTransferDTO =
            new AccountTransferDTO(bob.getId(), sourceAccount, targetAccount, transferAmount);
        final Response response = accountTransferImpl(accountTransferDTO);
        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
    }
}
