package org.atulspatil1.healthcarepreauthorization.repository;

import org.atulspatil1.healthcarepreauthorization.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByPreAuthorizationId(Long preAuthId);
}
