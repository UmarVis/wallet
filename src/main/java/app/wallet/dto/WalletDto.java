package app.wallet.dto;

import app.wallet.ennumeration.WalletOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletDto {
    private Long walletId;
    private WalletOperation operationType;
    private Long amount;
}
