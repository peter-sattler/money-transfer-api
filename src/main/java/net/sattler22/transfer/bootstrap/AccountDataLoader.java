package net.sattler22.transfer.bootstrap;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import net.sattler22.transfer.domain.Account;
import net.sattler22.transfer.domain.Customer;
import net.sattler22.transfer.dto.AccountDTO;
import net.sattler22.transfer.service.TransferService;

/**
 * Bootstrap Account Data Loader
 *
 * @author Pete Sattler
 * @version September 2019
 */
final class AccountDataLoader extends BaseDataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDataLoader.class);
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
        final TypeReference<List<AccountDTO>> typeRef =
            new TypeReference<List<AccountDTO>>() {};
        final List<AccountDTO> accounts = objectMapper.readValue(inputFile, typeRef);
        for(AccountDTO accountDTO : accounts) {
            final Customer owner = transferService.findCustomer(accountDTO.getCustomerId()).get();
            final Account account = new Account(accountDTO.getType(), owner, accountDTO.getBalance());
            owner.addAccount(account);
            LOGGER.info("Added {}", account);
        }
        return accounts.size();
    }

    @Override
    public String toString() {
        return String.format("%s [resource=%s, transferService=%s]",
                             getClass().getSimpleName(), resource, transferService);
    }
}

