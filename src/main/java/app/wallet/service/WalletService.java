package app.wallet.service;

import app.wallet.dto.WalletDto;

public interface WalletService {
    WalletDto addOperation(WalletDto dto);

    WalletDto get(String uuid);
}
