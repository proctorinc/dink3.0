package com.dink3.institutions.dto;

import com.dink3.jooq.tables.records.InstitutionRecord;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.plaid.client.model.Institution;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstitutionDto {

    private String id;

    @JsonIgnore
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
    public static InstitutionDto fromPlaidInstitution(
        Institution plaidInstitution,
        String plaidInstitutionId
    ) {
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
    public void updateExistingInstitution(
        com.dink3.jooq.tables.pojos.Institution existingInstitution
    ) {
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
        com.dink3.jooq.tables.pojos.Institution institution =
            new com.dink3.jooq.tables.pojos.Institution();
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

    /**
     * Convert from InstitutionRecord to InstitutionDto
     */
    public static InstitutionDto fromRecord(InstitutionRecord record) {
        return InstitutionDto.builder()
            .id(record.getId())
            .plaidInstitutionId(record.getPlaidInstitutionId())
            .name(record.getName())
            .logo(record.getLogo())
            .primaryColor(record.getPrimaryColor())
            .url(record.getUrl())
            .createdAt(record.getCreatedAt())
            .updatedAt(record.getUpdatedAt())
            .build();
    }
}
