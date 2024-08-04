package app.wallet.service;

import app.wallet.dto.WalletDto;

import java.util.concurrent.CompletableFuture;

public interface WalletService {
    CompletableFuture<WalletDto> addOperation(WalletDto dto);

    WalletDto get(Long uuid);

    CompletableFuture<WalletDto> add(WalletDto dto);
}
