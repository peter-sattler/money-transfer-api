package net.sattler22.transfer.api;

import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response.Status;
import net.sattler22.transfer.domain.Bank;

/**
 * Money Transfer Bank Integration Test Harness
 *
 * @author Pete Sattler
 * @version September 2019
 */
final class MoneyTransferBankIntegrationTest extends MoneyTransferBaseTestHarness {

    @Test
    void fetchBankDetailsHappyPathTestCase() {
        final var response = target(basePath).path("bank").request().get();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));
        final var actual = response.readEntity(Bank.class);
        assertEquals(bank, actual);
    }
}
