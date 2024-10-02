package com.example.demo.domain.persistence;

import com.example.demo.domain.entities.Alumno;
import org.springframework.data.repository.CrudRepository;

public interface AlumnoDao extends CrudRepository<Alumno, Integer> {
}
