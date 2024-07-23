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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public WalletDto addOperation(WalletDto dto) {
        log.info("Операция с кошельком: ИД [{}], тип операции [{}], сумма [{}]",
                dto.getWalletId(), dto.getOperationType(), dto.getAmount());
        Wallet wallet = checkAndGet(dto.getWalletId());
        fulfillmentOperation(dto.getWalletId(), dto.getOperationType(), dto.getAmount());
        return toDto(walletRepository.saveAndFlush(wallet));
    }

    @Override
    public WalletDto get(String uuid) {
        Wallet wallet = checkAndGet(uuid);
        log.info("Получения информации о кошельке ИД [{}]", uuid);
        return toDto(wallet);
    }

    private WalletDto toDto(Wallet wallet) {
        return WalletDto.builder()
                .walletId(wallet.getWalletId())
                .operationType(wallet.getOperationType())
                .amount(wallet.getAmount())
                .build();
    }

    private void fulfillmentOperation(String id, WalletOperation operation, Long amount) {
        switch (operation) {
            case DEPOSIT:
                deposit(id, amount);
                break;
            case WITHDRAW:
                withdraw(id, amount);
                break;
        }
    }

    private void withdraw(String id, Long amount) {
        Wallet wallet = checkAndGet(id);
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
    }

    private void deposit(String id, Long amount) {
        Wallet wallet = checkAndGet(id);
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
    }

    private Wallet checkAndGet(String walletId) {
        return walletRepository.findById(walletId).orElseThrow(() ->
                new NotFoundException("Кошелек с ИД " + walletId + " не найден"));
    }
}
