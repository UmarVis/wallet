package app.wallet.serviceImpl;

import app.wallet.ennumeration.WalletOperation;
import app.wallet.exceptions.ExceptionAmount;
import app.wallet.model.Wallet;
import app.wallet.repository.WalletRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class DepositService {
    private final WalletRepository walletRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Retryable(maxAttempts = 5)
    public void deposit(Wallet wallet, Long amount) {
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
}
