package net.sattler22.transfer.api;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static net.sattler22.transfer.domain.AccountType.CHECKING;
import static net.sattler22.transfer.domain.AccountType.SAVINGS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.dto.AccountTransferDTO;
import net.sattler22.transfer.util.TestDataFactory;

/**
 * Money Transfer Concurrency Test Harness
 *
 * @author Pete Sattler
 * @version September 2019
 */
public final class MoneyTransferConcurrencyTestHarness extends MoneyTransferBaseTestHarness {

    @Test
    public void accountTransferConditionalPutTestCase() {
        final Customer bob = TestDataFactory.getBob("123");
        addCustomerImpl(bob);
        final Account sourceAccount = addAccountImpl(CHECKING, bob.getId(), new BigDecimal("500"));
        final Account targetAccount = addAccountImpl(SAVINGS, bob.getId(), new BigDecimal("100"));
        final AccountTransferDTO accountTransferDTO =
            new AccountTransferDTO(bob.getId(), sourceAccount.getNumber(), targetAccount.getNumber(), new BigDecimal("10"));
        //Normal transfer::
        final String transferVersion = AccountTransferDTO.createVersion(sourceAccount, targetAccount);
        final Response response1 = accountTransferImpl(transferVersion, accountTransferDTO);
        assertEquals(Status.OK.getStatusCode(), response1.getStatus());
        assertEquals(APPLICATION_JSON, response1.getHeaderString(CONTENT_TYPE));
        //Transfer rejected because version on server is more recent:
        final Response response2 = accountTransferImpl(transferVersion, accountTransferDTO);
        assertEquals(Status.PRECONDITION_FAILED.getStatusCode(), response2.getStatus());
        assertNull(response2.getHeaderString(CONTENT_TYPE));
    }
}
