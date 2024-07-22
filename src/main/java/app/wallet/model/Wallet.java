package app.wallet.model;

import app.wallet.ennumeration.WalletOperation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "wallet")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String walletId;
    @Column(name = "operation_type")
    @Enumerated(EnumType.STRING)
    private WalletOperation operationType;
    @Column(name = "amount")
    private Long amount;
}
