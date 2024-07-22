package app.wallet.controller;

import app.wallet.dto.WalletDto;
import app.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class WalletController {
    private final WalletService walletService;

    @PostMapping("wallet")
    private WalletDto addOperation(@RequestBody WalletDto dto) {
        return walletService.addOperation(dto);
    }
}
