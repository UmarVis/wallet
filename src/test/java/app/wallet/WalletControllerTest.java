package app.wallet;

import app.wallet.controller.WalletController;
import app.wallet.dto.WalletDto;
import app.wallet.ennumeration.WalletOperation;
import app.wallet.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class WalletControllerTest {

    MockMvc mockMvc;

    @Mock
    WalletService walletService;

    @InjectMocks
    WalletController walletController;

    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();
        mapper = new ObjectMapper();
    }

    @Test
    void getByUuidTest() throws Exception {
        WalletDto dto = new WalletDto(1L, WalletOperation.DEPOSIT, 1000L);
        when(walletService.get(1L)).thenReturn(dto);
        mockMvc.perform(get("/api/v1/wallets/{uuid}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value("1"))
                .andExpect(jsonPath("$.operationType").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(1000L));
    }

    @Test
    void addOperationTest() throws Exception {
        WalletDto dto = new WalletDto(1L, WalletOperation.DEPOSIT, 1000L);
        String json = mapper.writeValueAsString(dto);
        when(walletService.addOperation(dto)).thenReturn(dto);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value("1"))
                .andExpect(jsonPath("$.operationType").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(1000L));

    }
}
