package com.cardif.cardifsyncpoc.repository;

import com.cardif.cardifsyncpoc.domain.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {
}
