package app.wallet.serviceImpl;

import app.wallet.dto.WalletDto;
import app.wallet.ennumeration.WalletOperation;
import app.wallet.exceptions.NotFoundException;
import app.wallet.model.Wallet;
import app.wallet.repository.WalletRepository;
import app.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final WithdrawService withdrawService;
    private final DepositService depositService;

    @Override
    @Transactional
    public WalletDto addOperation(WalletDto dto) {
        log.info("Операция с кошельком: ИД [{}], тип операции [{}], сумма [{}]",
                dto.getWalletId(), dto.getOperationType(), dto.getAmount());
        Wallet wallet = checkAndGet(dto.getWalletId());
        fulfillmentOperation(wallet, dto.getOperationType(), dto.getAmount());
        return toDto(walletRepository.saveAndFlush(wallet));
    }

    @Override
    @Transactional
    public WalletDto get(Long uuid) {
        Wallet wallet = checkAndGet(uuid);
        log.info("Получения информации о кошельке ИД [{}]", uuid);
        return toDto(wallet);
    }

    @Override
    @Transactional
    public WalletDto add(WalletDto dto) {
        Wallet wallet = toEntity(dto);
        log.info("Сохранен новый кошелек [{}]", dto);
        return toDto(walletRepository.saveAndFlush(wallet));
    }

    private Wallet toEntity(WalletDto dto) {
        return Wallet.builder()
                .walletId(dto.getWalletId())
                .operationType(dto.getOperationType())
                .amount(dto.getAmount())
                .build();
    }

    private WalletDto toDto(Wallet wallet) {
        return WalletDto.builder()
                .walletId(wallet.getWalletId())
                .operationType(wallet.getOperationType())
                .amount(wallet.getAmount())
                .build();
    }

    private void fulfillmentOperation(Wallet wallet, WalletOperation operation, Long amount) {
        switch (operation) {
            case DEPOSIT:
                depositService.deposit(wallet, amount);
                break;
            case WITHDRAW:
                withdrawService.withdraw(wallet, amount);
                break;
        }
    }

    private Wallet checkAndGet(Long walletId) {
        return walletRepository.findByWalletId(walletId).orElseThrow(() ->
                new NotFoundException("Кошелек с ИД " + walletId + " не найден"));
    }
}
