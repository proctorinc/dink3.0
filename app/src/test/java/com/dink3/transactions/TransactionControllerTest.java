package com.dink3.transactions;

import com.dink3.plaid.service.PlaidDataService;
import com.dink3.jooq.tables.pojos.Transaction;
import com.dink3.jooq.tables.pojos.User;
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
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlaidDataService plaidDataService;

    @Test
    @WithMockUser
    @DisplayName("Should return transactions for authenticated user")
    void getTransactions_authenticatedUser_returnsTransactions() throws Exception {
        User user = new User();
        user.setId("user-1");
        List<Transaction> transactions = List.of(new Transaction());
        when(plaidDataService.getUserTransactions(any(User.class))).thenReturn(transactions);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transactions").principal(() -> "user-1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 401 if user is not authenticated for getTransactions")
    void getTransactions_unauthenticated_returns401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transactions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return transaction by ID for authenticated user")
    void getTransaction_authenticatedUser_returnsTransaction() throws Exception {
        User user = new User();
        user.setId("user-1");
        Transaction transaction = new Transaction();
        when(plaidDataService.getTransactionById(Mockito.eq("txn-1"), any(User.class))).thenReturn(Optional.of(transaction));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transactions/txn-1").principal(() -> "user-1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 404 if transaction not found")
    void getTransaction_notFound_returns404() throws Exception {
        when(plaidDataService.getTransactionById(Mockito.eq("txn-404"), any(User.class))).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transactions/txn-404").principal(() -> "user-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 401 if user is not authenticated for getTransaction")
    void getTransaction_unauthenticated_returns401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transactions/txn-1"))
                .andExpect(status().isUnauthorized());
    }
} 