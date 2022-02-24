package com.cardif.cardifsyncpoc.controller;

import com.cardif.cardifsyncpoc.domain.Audit;
import com.cardif.cardifsyncpoc.repository.AuditRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("audit/")
public class AuditController {
    private final AuditRepository auditRepository;

    public AuditController(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Audit> getAllAudits(){
        return this.auditRepository.findAll();
    }
}
