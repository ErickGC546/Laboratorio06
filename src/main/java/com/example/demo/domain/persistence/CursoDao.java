package com.example.demo.domain.persistence;

import com.example.demo.domain.entities.Curso;
import org.springframework.data.repository.CrudRepository;

public interface CursoDao extends CrudRepository<Curso,Integer> {
}
