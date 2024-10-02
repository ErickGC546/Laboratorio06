package com.example.demo.services;

import com.example.demo.domain.entities.Alumno;

import java.util.List;

public interface AlumnoService {

    public void grabar(Alumno alumno);
    public void eliminar(int id);
    public Alumno buscar(Integer id);
    public List<Alumno> listar();
}
