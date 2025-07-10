package com.dink3.user;

import com.dink3.jooq.tables.pojos.User;
import com.dink3.user.dto.UserProfileDto;
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
import java.util.Optional;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser
    @DisplayName("Should return user profile for authenticated user")
    void getMe_authenticatedUser_returnsProfile() throws Exception {
        User user = new User();
        user.setId("user-1");
        UserProfileDto profile = new UserProfileDto();
        when(userService.getUserProfile(anyString())).thenReturn(Optional.of(profile));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/me").principal(() -> "user-1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 401 if user is not authenticated for /me")
    void getMe_unauthenticated_returns401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 404 if user profile not found")
    void getMe_profileNotFound_returns404() throws Exception {
        when(userService.getUserProfile(anyString())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/me").principal(() -> "user-1"))
                .andExpect(status().isNotFound());
    }
} 