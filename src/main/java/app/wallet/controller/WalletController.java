package app.wallet.controller;

import app.wallet.dto.WalletDto;
import app.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class WalletController {
    private final WalletService walletService;

    @PostMapping("add")
    private WalletDto add(@RequestBody WalletDto dto) {
        return walletService.add(dto);
    }

    @PostMapping("wallet")
    private WalletDto addOperation(@RequestBody WalletDto dto) {
        return walletService.addOperation(dto);
    }

    @GetMapping("wallets/{uuid}")
    private WalletDto get(@PathVariable Long uuid) {
        return walletService.get(uuid);
    }
}
