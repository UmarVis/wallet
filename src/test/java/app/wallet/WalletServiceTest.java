package app.wallet;

import app.wallet.dto.WalletDto;
import app.wallet.ennumeration.WalletOperation;
import app.wallet.repository.WalletRepository;
import app.wallet.serviceImpl.WalletServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    WalletRepository walletRepository;

    @InjectMocks
    WalletServiceImpl service;

    @Test
    void addTest() {
        WalletDto dto = new WalletDto("1", WalletOperation.DEPOSIT, 1000L);
        Mockito.when(walletRepository.saveAndFlush(any())).thenReturn(Optional.of(dto));

        WalletDto testDto = service.addOperation(dto);

        assertEquals(dto, testDto);
        verify(walletRepository).save(any());
    }
}
