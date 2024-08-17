package app.wallet.serviceImpl;

import app.wallet.ennumeration.WalletOperation;
import app.wallet.exceptions.ExceptionAmount;
import app.wallet.model.Wallet;
import app.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class WithdrawService {

    private final WalletRepository walletRepository;

    @Retryable(maxAttempts = 5)
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void withdraw(Wallet wallet, Long amount) {
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
}
