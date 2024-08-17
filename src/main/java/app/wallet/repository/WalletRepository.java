package app.wallet.repository;

import app.wallet.model.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findByWalletId(Long id);
}
