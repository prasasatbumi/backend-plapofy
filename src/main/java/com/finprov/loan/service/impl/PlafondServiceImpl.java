package com.finprov.loan.service.impl;

import com.finprov.loan.entity.Plafond;
import com.finprov.loan.entity.ProductInterest;
import com.finprov.loan.repository.PlafondRepository;
import com.finprov.loan.service.PlafondService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlafondServiceImpl implements PlafondService {

    private final PlafondRepository plafondRepository;

    @Override
    public List<Plafond> findAll() {
        return plafondRepository.findAllByDeletedFalse();
    }

    @Override
    @Transactional
    public Plafond update(Long id, Plafond plafondDetails) {
        Plafond existing = plafondRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plafond not found: " + id));

        if (plafondDetails.getName() != null)
            existing.setName(plafondDetails.getName());
        if (plafondDetails.getDescription() != null)
            existing.setDescription(plafondDetails.getDescription());
        if (plafondDetails.getMinAmount() != null)
            existing.setMinAmount(plafondDetails.getMinAmount());
        if (plafondDetails.getMaxAmount() != null)
            existing.setMaxAmount(plafondDetails.getMaxAmount());

        // Update interests if provided
        if (plafondDetails.getInterests() != null) {
            // Clear existing interests (orphanRemoval will handle deletion)
            existing.getInterests().clear();
            plafondRepository.flush(); // FORCE DELETE NOW

            // Add new interests
            for (ProductInterest interest : plafondDetails.getInterests()) {
                // FORCE INSERT: Create a new instance or set ID to null
                // Be careful not to mutate the passed object if it's used elsewhere, but here
                // it's fine.
                // Better: Create new object to be safe from detached entity issues.
                ProductInterest newInterest = ProductInterest.builder()
                        .id(null) // Explicitly null to force generation
                        .tenor(interest.getTenor())
                        .interestRate(interest.getInterestRate())
                        .plafond(existing)
                        .build();
                existing.getInterests().add(newInterest);
            }
        }

        return plafondRepository.save(existing);
    }

    @Override
    @Transactional
    public Plafond create(Plafond plafond) {
        // Ensure bidirectional relationship for interests if they exist
        if (plafond.getInterests() != null) {
            for (ProductInterest interest : plafond.getInterests()) {
                interest.setPlafond(plafond);
            }
        }
        return plafondRepository.save(plafond);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Plafond p = plafondRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plafond not found: " + id));
        p.setDeleted(true);
        plafondRepository.save(p);
    }
}
