package com.jumarkot.ingestion.repository;

import com.jumarkot.ingestion.domain.IngestedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IngestedEventRepository extends JpaRepository<IngestedEvent, UUID> {
    Optional<IngestedEvent> findByIdempotencyKey(String idempotencyKey);
    boolean existsByIdempotencyKey(String idempotencyKey);
}
