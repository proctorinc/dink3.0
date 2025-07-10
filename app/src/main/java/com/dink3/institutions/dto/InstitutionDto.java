package com.dink3.institutions.dto;

import com.plaid.client.model.Institution;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InstitutionDto {
    private String id;
    private String plaidInstitutionId;
    private String name;
    private String logo;
    private String primaryColor;
    private String url;
    private String createdAt;
    private String updatedAt;
    
    /**
     * Convert from Plaid Institution to InstitutionDto
     */
    public static InstitutionDto fromPlaidInstitution(Institution plaidInstitution, String plaidInstitutionId) {
        return InstitutionDto.builder()
                .plaidInstitutionId(plaidInstitutionId)
                .name(plaidInstitution.getName())
                .logo(plaidInstitution.getLogo())
                .primaryColor(plaidInstitution.getPrimaryColor())
                .url(plaidInstitution.getUrl())
                .build();
    }
    
    /**
     * Update an existing Institution entity with Plaid data (preserves ID)
     */
    public void updateExistingInstitution(com.dink3.jooq.tables.pojos.Institution existingInstitution) {
        existingInstitution.setName(this.name);
        existingInstitution.setLogo(this.logo);
        existingInstitution.setPrimaryColor(this.primaryColor);
        existingInstitution.setUrl(this.url);
        existingInstitution.setUpdatedAt(LocalDateTime.now().toString());
    }
    
    /**
     * Convert to Institution entity (for new institutions)
     */
    public com.dink3.jooq.tables.pojos.Institution toInstitution() {
        com.dink3.jooq.tables.pojos.Institution institution = new com.dink3.jooq.tables.pojos.Institution();
        institution.setId(this.id);
        institution.setPlaidInstitutionId(this.plaidInstitutionId);
        institution.setName(this.name);
        institution.setLogo(this.logo);
        institution.setPrimaryColor(this.primaryColor);
        institution.setUrl(this.url);
        institution.setCreatedAt(this.createdAt);
        institution.setUpdatedAt(this.updatedAt);
        return institution;
    }
} 