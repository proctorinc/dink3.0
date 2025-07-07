package com.dink3.institutions;

import com.dink3.jooq.tables.pojos.Institutions;
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
    
    private final PlaidDataService plaidDataService;
    
    public InstitutionController(PlaidDataService plaidDataService) {
        this.plaidDataService = plaidDataService;
    }
    
    /**
     * Get all institutions
     */
    @GetMapping
    public ResponseEntity<List<Institutions>> getInstitutions() {
        log.info("Getting all institutions");
        List<Institutions> institutions = plaidDataService.getAllInstitutions();
        return ResponseEntity.ok(institutions);
    }
    
    /**
     * Get a specific institution by ID
     */
    @GetMapping("/{institutionId}")
    public ResponseEntity<Institutions> getInstitution(@PathVariable Integer institutionId) {
        log.info("Getting institution: {}", institutionId);
        
        return plaidDataService.getInstitutionById(institutionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 