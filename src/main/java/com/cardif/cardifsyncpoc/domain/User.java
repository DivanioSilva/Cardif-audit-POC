package com.cardif.cardifsyncpoc.domain;

import com.cardif.cardifsyncpoc.repository.AuditRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personIdGenerator")
    @SequenceGenerator(name = "personIdGenerator", sequenceName = "personIdSequenceName", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private Integer age;
    private String email;

    @PrePersist
    public void onPrePersist(){

    }

    @PreUpdate
    public void onPreUpdate(){

    }

    @PreRemove
    public void onPreDelete(){

    }
}
