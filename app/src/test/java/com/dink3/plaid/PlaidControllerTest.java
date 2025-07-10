package com.dink3.plaid;

import com.dink3.jooq.tables.pojos.User;
import com.dink3.plaid.service.PlaidDataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlaidController.class)
class PlaidControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlaidService plaidService;
    @MockBean
    private PlaidDataService plaidDataService;

    @Test
    @WithMockUser
    @DisplayName("Should create link token for authenticated user")
    void createLinkToken_authenticatedUser_returnsToken() throws Exception {
        User user = new User();
        user.setId("user-1");
        when(plaidService.createLinkToken(any(User.class))).thenReturn("token-123");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/plaid/link-token").principal(() -> "user-1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 401 if user is not authenticated for createLinkToken")
    void createLinkToken_unauthenticated_returns401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/plaid/link-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("Should exchange public token for access token")
    void exchangePublicToken_authenticatedUser_success() throws Exception {
        User user = new User();
        user.setId("user-1");
        ExchangeTokenRequest req = new ExchangeTokenRequest();
        req.publicToken = "public-token";
        when(plaidService.exchangePublicToken(Mockito.eq("public-token"), any(User.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/plaid/exchange-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"publicToken\":\"public-token\"}")
                .principal(() -> "user-1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 if public token is missing")
    void exchangePublicToken_missingToken_returns400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/plaid/exchange-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .principal(() -> "user-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 500 if exchange fails")
    void exchangePublicToken_exchangeFails_returns500() throws Exception {
        when(plaidService.exchangePublicToken(Mockito.eq("public-token"), any(User.class))).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/plaid/exchange-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"publicToken\":\"public-token\"}")
                .principal(() -> "user-1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    @DisplayName("Should trigger sync for authenticated user")
    void syncData_authenticatedUser_success() throws Exception {
        User user = new User();
        user.setId("user-1");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/plaid/sync").principal(() -> "user-1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 401 if user is not authenticated for syncData")
    void syncData_unauthenticated_returns401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/plaid/sync"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should handle webhook")
    void handleWebhook_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/plaid/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"event\":\"test\"}"))
                .andExpect(status().isOk());
    }
} 