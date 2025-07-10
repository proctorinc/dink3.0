package com.dink3.accounts;

import com.dink3.plaid.service.PlaidDataService;
import com.dink3.jooq.tables.pojos.Account;
import com.dink3.jooq.tables.pojos.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlaidDataService plaidDataService;

    @Test
    @WithMockUser
    @DisplayName("Should return accounts for authenticated user")
    void getAccounts_authenticatedUser_returnsAccounts() throws Exception {
        User user = new User();
        user.setId("user-1");
        List<Account> accounts = List.of(new Account());
        when(plaidDataService.getUserAccounts(any(User.class))).thenReturn(accounts);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts").principal(() -> "user-1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 401 if user is not authenticated for getAccounts")
    void getAccounts_unauthenticated_returns401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return account by ID for authenticated user")
    void getAccount_authenticatedUser_returnsAccount() throws Exception {
        User user = new User();
        user.setId("user-1");
        Account account = new Account();
        when(plaidDataService.getAccountById(Mockito.eq("acc-1"), any(User.class))).thenReturn(Optional.of(account));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/acc-1").principal(() -> "user-1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 404 if account not found")
    void getAccount_notFound_returns404() throws Exception {
        when(plaidDataService.getAccountById(Mockito.eq("acc-404"), any(User.class))).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/acc-404").principal(() -> "user-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 401 if user is not authenticated for getAccount")
    void getAccount_unauthenticated_returns401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/acc-1"))
                .andExpect(status().isUnauthorized());
    }
} 