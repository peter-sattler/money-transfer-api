package net.sattler22.transfer.bootstrap;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.ws.rs.NotFoundException;
import net.jcip.annotations.Immutable;
import net.sattler22.transfer.api.AccountDto;
import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Bootstrap Account Data Loader
 *
 * @author Pete Sattler
 * @version November 2025
 * @since August 2019
 */
@Immutable
final class AccountDataLoader extends BaseDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(AccountDataLoader.class);
    private final TransferService transferService;

    /**
     * Constructs a new account data loader
     */
    AccountDataLoader(TransferService transferService, String resourceName) {
        super(resourceName);
        this.transferService = transferService;
    }

    @Override
    int load() throws IOException {
        final File inputFile = new File(resource.getFile());
        final List<AccountDto> accountDtos = objectMapper.readValue(inputFile, new TypeReference<>() {});
        for (final AccountDto accountDto : accountDtos) {
            final Customer owner =
                    transferService.findCustomer(accountDto.customerId())
                            .orElseThrow(() -> new NotFoundException(String.format("Customer ID [%s] not found", accountDto.customerId())));
            final Account account = new Account(owner, accountDto.type(), accountDto.balance());
            owner.addAccount(account);
            logger.info("Added {}", account);
        }
        return accountDtos.size();
    }

    @Override
    public String toString() {
        return String.format("%s [resource=%s, transferService=%s]", getClass().getSimpleName(), resource, transferService);
    }
}
