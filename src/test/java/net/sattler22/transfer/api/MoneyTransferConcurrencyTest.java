package net.sattler22.transfer.api;

import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static net.sattler22.transfer.domain.AccountType.CHECKING;
import static net.sattler22.transfer.domain.AccountType.SAVINGS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response.Status;
import net.sattler22.transfer.util.TestDataFactory;

/**
 * Money Transfer Concurrency Test Harness
 *
 * @author Pete Sattler
 * @version September 2019
 */
final class MoneyTransferConcurrencyTest extends MoneyTransferBaseTestHarness {

    @Test
    void accountTransferConditionalPutTestCase() {
        final var bob = TestDataFactory.bob("123");
        addCustomerImpl(bob);
        final var sourceAccount = addAccountImpl(CHECKING, bob.id(), new BigDecimal("500"));
        final var targetAccount = addAccountImpl(SAVINGS, bob.id(), new BigDecimal("100"));
        final var accountTransferDto = new AccountTransferDto(bob.id(), sourceAccount.number(), targetAccount.number(), new BigDecimal("10"));
        //Normal transfer:
        final var transferVersion = AccountTransferDto.createVersion(sourceAccount, targetAccount);
        final var response1 = accountTransferImpl(transferVersion, accountTransferDto);
        assertEquals(Status.OK.getStatusCode(), response1.getStatus());
        assertEquals(APPLICATION_JSON, response1.getHeaderString(CONTENT_TYPE));
        //Transfer rejected because version on server is more recent:
        final var response2 = accountTransferImpl(transferVersion, accountTransferDto);
        assertEquals(Status.PRECONDITION_FAILED.getStatusCode(), response2.getStatus());
        assertNull(response2.getHeaderString(CONTENT_TYPE));
    }
}
