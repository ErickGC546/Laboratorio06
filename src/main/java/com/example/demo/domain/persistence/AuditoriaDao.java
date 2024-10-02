package com.example.demo.domain.persistence;

import com.example.demo.domain.entities.Auditoria;
import org.springframework.data.repository.CrudRepository;


public interface AuditoriaDao extends CrudRepository<Auditoria, Integer> {

}

