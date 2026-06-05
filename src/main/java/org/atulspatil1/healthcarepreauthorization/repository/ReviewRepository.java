package org.atulspatil1.healthcarepreauthorization.repository;

import org.atulspatil1.healthcarepreauthorization.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPreAuthorizationId(Long preAuthId);
}
