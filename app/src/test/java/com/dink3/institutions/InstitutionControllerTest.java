package com.dink3.institutions;

import com.dink3.plaid.service.PlaidDataService;
import com.dink3.jooq.tables.pojos.Institution;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstitutionController.class)
class InstitutionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlaidDataService plaidDataService;

    @Test
    @DisplayName("Should return all institutions")
    void getInstitutions_returnsInstitutions() throws Exception {
        List<Institution> institutions = List.of(new Institution());
        when(plaidDataService.getAllInstitutions()).thenReturn(institutions);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/institutions"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return institution by ID")
    void getInstitution_returnsInstitution() throws Exception {
        Institution institution = new Institution();
        when(plaidDataService.getInstitutionById("inst-1")).thenReturn(Optional.of(institution));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/institutions/inst-1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 404 if institution not found")
    void getInstitution_notFound_returns404() throws Exception {
        when(plaidDataService.getInstitutionById("inst-404")).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/institutions/inst-404"))
                .andExpect(status().isNotFound());
    }
} 