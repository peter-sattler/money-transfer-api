package net.sattler22.transfer.api;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static net.sattler22.transfer.api.MoneyTransferConstants.API_BASE_PATH;
import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import net.sattler22.transfer.domain.Bank;

/**
 * Money Transfer Bank Integration Test Harness
 *
 * @author Pete Sattler
 * @version September 2019
 */
public final class MoneyTransferBankIntegrationTestHarness extends MoneyTransferBaseTestHarness {

    @Test
    public void fetchBankDetailsHappyPathTestCase() {
        final Response response = target(API_BASE_PATH).path("bank").request().get();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));
        final Bank actual = response.readEntity(Bank.class);
        assertEquals(bank, actual);
    }
}
