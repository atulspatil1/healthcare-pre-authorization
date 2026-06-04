package org.atulspatil1.healthcarepreauthorization.repository;

import org.atulspatil1.healthcarepreauthorization.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    List<Provider> findByCity(String city);
}
