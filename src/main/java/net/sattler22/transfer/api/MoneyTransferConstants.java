package net.sattler22.transfer.api;

/**
 * Money Transfer Constants
 *
 * @author Pete Sattler
 * @version September 2019
 */
public final class MoneyTransferConstants {

    private MoneyTransferConstants() {
        throw new AssertionError("Cannot be instantiated");
    }

    /**
     * API base path
     */
    public static final String API_BASE_PATH = "/api/money-transfer";
}
