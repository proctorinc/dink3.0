package com.dink3.institutions;

import com.dink3.institutions.dto.InstitutionFullDto;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    public InstitutionService(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    public List<InstitutionFullDto> getAllInstitutions(String userId) {
        return institutionRepository.findAllByUserId(userId);
    }

    public Optional<InstitutionFullDto> getInstitutionById(
        String institutionId,
        String userId
    ) {
        return institutionRepository.findByUserIdAndId(institutionId, userId);
    }
}
