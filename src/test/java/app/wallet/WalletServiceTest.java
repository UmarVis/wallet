package app.wallet;

import app.wallet.dto.WalletDto;
import app.wallet.ennumeration.WalletOperation;
import app.wallet.exceptions.ExceptionAmount;
import app.wallet.exceptions.NotFoundException;
import app.wallet.model.Wallet;
import app.wallet.repository.WalletRepository;
import app.wallet.serviceImpl.WalletServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    WalletRepository walletRepository;

    WalletServiceImpl service;

    @Test
    void addDepositTest() {
        service = new WalletServiceImpl(walletRepository);
        WalletDto dto = new WalletDto(1L, WalletOperation.DEPOSIT, 1000L);
        Wallet wallet = new Wallet(1L, WalletOperation.DEPOSIT, 1000L);
        Mockito.when(walletRepository.findById(any()))
                .thenReturn(Optional.of(wallet));
        Mockito.when(walletRepository.saveAndFlush(any()))
                .thenReturn(wallet);

        CompletableFuture<WalletDto> testDto = service.addOperation(dto);

        assertEquals(new WalletDto(1L, WalletOperation.DEPOSIT, 2000L), testDto);
    }

    @Test
    void addWithdrawTest() {
        service = new WalletServiceImpl(walletRepository);
        WalletDto dto = new WalletDto(1L, WalletOperation.WITHDRAW, 500L);
        Wallet wallet = new Wallet(1L, WalletOperation.WITHDRAW, 1000L);
        Mockito.when(walletRepository.findById(any()))
                .thenReturn(Optional.of(wallet));
        Mockito.when(walletRepository.saveAndFlush(any()))
                .thenReturn(wallet);

        CompletableFuture<WalletDto> testDto = service.addOperation(dto);

        assertEquals(new WalletDto(1L, WalletOperation.WITHDRAW, 500L), testDto);
    }

    @Test
    void getTest() {
        service = new WalletServiceImpl(walletRepository);
        WalletDto dtoExp = new WalletDto(1L, WalletOperation.WITHDRAW, 1000L);
        Wallet wallet = new Wallet(1L, WalletOperation.WITHDRAW, 1000L);
        Mockito.when(walletRepository.findById(any()))
                .thenReturn(Optional.of(wallet));

        WalletDto dto = service.get(1L);

        assertEquals(dtoExp, dto);
    }

    @Test
    void getExceptionTest() {
        service = new WalletServiceImpl(walletRepository);

        when(walletRepository.findById(any()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> service.get(any()));
        assertEquals("Кошелек с ИД null не найден", e.getMessage());
    }

    @Test
    void withdrawExceptionTest() {
        service = new WalletServiceImpl(walletRepository);
        WalletDto dto = new WalletDto(1L, WalletOperation.WITHDRAW, 2000L);
        Wallet wallet = new Wallet(1L, WalletOperation.WITHDRAW, 1000L);

        Mockito.when(walletRepository.findById(any()))
                .thenReturn(Optional.of(wallet));

        ExceptionAmount e = assertThrows(ExceptionAmount.class, () -> service.addOperation(dto));
        assertEquals("Сумма для списания больше доступной суммы кошелька", e.getMessage());
    }

    @Test
    void deposit0ExceptionTest() {
        service = new WalletServiceImpl(walletRepository);
        WalletDto dto = new WalletDto(1L, WalletOperation.DEPOSIT, 0L);
        Wallet wallet = new Wallet(1L, WalletOperation.DEPOSIT, 0L);

        Mockito.when(walletRepository.findById(any()))
                .thenReturn(Optional.of(wallet));

        ExceptionAmount e = assertThrows(ExceptionAmount.class, () -> service.addOperation(dto));
        assertEquals("Введите сумму больше 0", e.getMessage());
    }
}
