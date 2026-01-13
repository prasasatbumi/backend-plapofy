package com.finprov.loan.service;

import com.finprov.loan.entity.Plafond;
import java.util.List;

public interface PlafondService {
    List<Plafond> findAll();

    Plafond update(Long id, Plafond plafond);

    Plafond create(Plafond plafond);

    void delete(Long id);
}
