package com.dink3.institutions;

import com.dink3.jooq.tables.pojos.Institution;
import com.dink3.plaid.service.PlaidDataService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Institutions", description = "Institution operations")
@RestController
@RequestMapping("/api/v1/institutions")
@SecurityRequirement(name = "bearerAuth")
public class InstitutionController {
    private static final Logger log = LoggerFactory.getLogger(InstitutionController.class);
    
    private final InstitutionService institutionService;
    
    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }
    
    /**
     * Get all institutions
     */
    @GetMapping
    public ResponseEntity<List<Institution>> getInstitutions() {
        log.info("Getting all institutions");
        List<Institution> institutions = institutionService.getAllInstitutions();
        return ResponseEntity.ok(institutions);
    }
    
    /**
     * Get a specific institution by ID
     */
    @GetMapping("/{institutionId}")
    public ResponseEntity<Institution> getInstitution(@PathVariable String institutionId) {
        log.info("Getting institution: {}", institutionId);
        
        return institutionService.getInstitutionById(institutionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 