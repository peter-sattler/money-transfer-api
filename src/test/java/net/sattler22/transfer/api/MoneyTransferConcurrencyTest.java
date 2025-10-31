package net.sattler22.transfer.api;

import jakarta.ws.rs.core.Response.Status;
import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.domain.AccountType;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.service.TransferService.TransferResult;
import net.sattler22.transfer.util.TestData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Money Transfer Concurrency Tests
 *
 * @author Pete Sattler
 * @version November 2025
 * @since September 2019
 */
final class MoneyTransferConcurrencyTest extends MoneyTransferBaseTest {

    @Test
    void accountTransferConditionalPutTestCase() {
        final Customer bobWire = addCustomer(TestData.bobWire("123"));
        final Account sourceAccount = addAccount(bobWire, AccountType.CHECKING, new BigDecimal("500"));
        final Account targetAccount = addAccount(bobWire, AccountType.SAVINGS, new BigDecimal("100"));
        final String transferVersion = AccountTransferDto.createVersion(sourceAccount, targetAccount);
        final TransferResult normalTransfer =
                transfer(bobWire, sourceAccount, targetAccount, transferVersion, new BigDecimal("10"), Status.OK);
        assertNotNull(normalTransfer);
        final TransferResult rejectedTransfer =
                transfer(bobWire, sourceAccount, targetAccount, transferVersion, new BigDecimal("15"), Status.PRECONDITION_FAILED);
        assertNull(rejectedTransfer);  //Rejected since version on server is more recent
    }
}
