package app.wallet.serviceImpl;

import app.wallet.dto.WalletDto;
import app.wallet.ennumeration.WalletOperation;
import app.wallet.exceptions.ExceptionAmount;
import app.wallet.exceptions.NotFoundException;
import app.wallet.model.Wallet;
import app.wallet.repository.WalletRepository;
import app.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public CompletableFuture<WalletDto> addOperation(WalletDto dto) {
        log.info("Операция с кошельком: ИД [{}], тип операции [{}], сумма [{}]",
                dto.getWalletId(), dto.getOperationType(), dto.getAmount());
        return fulfillmentOperation(dto.getWalletId(), dto.getOperationType(), dto.getAmount())
                .thenApply(wallet -> toDto(walletRepository.saveAndFlush(wallet)));
    }

    @Override
    @Transactional
    public CompletableFuture<WalletDto> add(WalletDto dto) {
        Wallet wallet = toEntity(dto);
        log.info("Сохранен новый кошелек [{}]", dto);
        return CompletableFuture.supplyAsync(() -> toDto(walletRepository.saveAndFlush(wallet)));
    }


    @Override
    public WalletDto get(Long uuid) {
        Wallet wallet = checkAndGet(uuid);
        log.info("Получения информации о кошельке ИД [{}]", uuid);
        return toDto(wallet);
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

    @Async
    CompletableFuture<Wallet> fulfillmentOperation(Long id, WalletOperation operation, Long amount) {
        return CompletableFuture.supplyAsync(() -> {
            Wallet wallet = checkAndGet(id);
            switch (operation) {
                case DEPOSIT:
                    return deposit(wallet, amount);
                case WITHDRAW:
                    return withdraw(wallet, amount);
                default:
                    throw new IllegalArgumentException("Unknown operation type: " + operation);
            }
        });
    }

    private Wallet withdraw(Wallet wallet, Long amount) {
        if (amount > wallet.getAmount()) {
            log.error("Сумма для списания [{}] больше доступной суммы [{}] кошелька ИД [{}]",
                    amount, wallet.getAmount(), wallet.getWalletId());
            throw new ExceptionAmount("Сумма для списания больше доступной суммы кошелька");
        }
        long newAmount = wallet.getAmount() - amount;
        wallet.setAmount(newAmount);
        wallet.setOperationType(WalletOperation.WITHDRAW);
        walletRepository.saveAndFlush(wallet);
        log.info("Операция [{}], с кошелком ИД [{}] проведена, сумма [{}], новая сумма [{}],",
                wallet.getOperationType(), wallet.getWalletId(), amount, wallet.getAmount());
        return wallet;
    }

    private Wallet deposit(Wallet wallet, Long amount) {
        if (amount <= 0) {
            log.error("Введите сумму больше 0");
            throw new ExceptionAmount("Введите сумму больше 0");
        }
        long newAmount = wallet.getAmount() + amount;
        wallet.setAmount(newAmount);
        wallet.setOperationType(WalletOperation.DEPOSIT);
        walletRepository.saveAndFlush(wallet);
        log.info("Операция [{}], с кошелком ИД [{}] проведена, сумма [{}], новая сумма [{}],",
                wallet.getOperationType(), wallet.getWalletId(), amount, wallet.getAmount());
        return wallet;
    }

    private Wallet checkAndGet(Long walletId) {
        return walletRepository.findById(walletId).orElseThrow(() ->
                new NotFoundException("Кошелек с ИД " + walletId + " не найден"));
    }
}
