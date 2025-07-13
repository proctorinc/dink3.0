package com.dink3.institutions;

import com.dink3.institutions.dto.InstitutionFullDto;
import com.dink3.jooq.tables.pojos.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Institutions", description = "Bank Institution operations")
@RestController
@RequestMapping("/api/v1/institutions")
@SecurityRequirement(name = "bearerAuth")
public class InstitutionController {

    private static final Logger log = LoggerFactory.getLogger(
        InstitutionController.class
    );

    private final InstitutionService institutionService;

    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    /**
     * Get all institutions
     */
    @GetMapping
    public ResponseEntity<List<InstitutionFullDto>> getInstitutions(
        @AuthenticationPrincipal User user
    ) {
        log.info("Getting all institutions");
        List<InstitutionFullDto> institutions =
            institutionService.getAllInstitutions(user.getId());
        return ResponseEntity.ok(institutions);
    }

    /**
     * Get a specific institution by ID
     */
    @GetMapping("/{institutionId}")
    public ResponseEntity<InstitutionFullDto> getInstitution(
        @AuthenticationPrincipal User user,
        @PathVariable String institutionId
    ) {
        log.info("Getting institution: {}", institutionId);

        return institutionService
            .getInstitutionById(institutionId, user.getId())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
