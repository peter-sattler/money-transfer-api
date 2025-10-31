package net.sattler22.transfer.api;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import net.sattler22.transfer.domain.Bank;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Money Transfer Bank Integration Tests
 *
 * @author Pete Sattler
 * @version November 2025
 * @since September 2019
 */
final class MoneyTransferBankIntegrationTest extends MoneyTransferBaseTest {

    @Test
    void getBankDetailsHappyPathTestCase() {
        final Invocation.Builder getBankRequest = target(basePath)
                .path("bank")
                .request();
        try (final Response getBankResponse = getBankRequest.get()) {
            final Bank actual = getBankResponse.readEntity(Bank.class);
            assertEquals(MediaType.APPLICATION_JSON, getBankResponse.getHeaderString(HttpHeaders.CONTENT_TYPE));
            assertEquals(Status.OK.getStatusCode(), getBankResponse.getStatus());
            assertEquals(bank, actual);
        }
    }
}
