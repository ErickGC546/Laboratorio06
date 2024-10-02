package com.example.demo.controllers;

import com.example.demo.domain.entities.Alumno;
import com.example.demo.services.AlumnoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import java.util.Map;

@Controller
@SessionAttributes("alumno")
public class AlumnoController {

    @Autowired
    AlumnoService alumnoService;

    // Listar alumnos
    @GetMapping("/listarAlumnos")
    public String listarAlumnos(Model model) {
        model.addAttribute("alumnos", alumnoService.listar());
        return "listarAlumnos"; // Asegúrate de que este archivo HTML exista
    }

    // Crear nuevo alumno
    @GetMapping("/formAlumno")
    public String crear(Map<String, Object> model) {
        Alumno alumno = new Alumno();
        model.put("alumno", alumno);
        return "formAlumno"; // Asegúrate de que este archivo HTML exista
    }

    @PostMapping("/formAlumno")
    public String guardar(@Valid @ModelAttribute("alumno") Alumno alumno, BindingResult result, SessionStatus status) {
        if (result.hasErrors()) {
            return "formAlumno"; // Devuelve al formulario si hay errores
        }

        alumnoService.grabar(alumno);
        status.setComplete();
        return "redirect:/listarAlumnos"; // Redirige a la lista de alumnos
    }

    // Formulario para editar alumno
    @GetMapping("/formAlumno/{id}")
    public String editar(@PathVariable(value = "id") Integer id, Map<String, Object> model) {
        Alumno alumno = null;

        if (id > 0) {
            alumno = alumnoService.buscar(id);
            if (alumno != null) {
                model.put("alumno", alumno);
                return "formAlumno"; // Asegúrate de que este archivo HTML exista
            }
        }
        return "redirect:/listarAlumnos"; // Redirige si el alumno no existe
    }

    // Eliminar alumno
    @GetMapping("/eliminarAlumno/{id}")
    public String eliminar(@PathVariable(value = "id") Integer id) {
        if (id > 0) {
            alumnoService.eliminar(id);
        }
        return "redirect:/listarAlumnos"; // Redirige a la lista de alumnos después de eliminar
    }

    // Ver alumnos
    @GetMapping("/alumno")
    public String ver(Model model) {
        model.addAttribute("alumnos", alumnoService.listar());
        model.addAttribute("titulo", "Lista de alumnos");
        return "alumno/ver"; // Asegúrate de que esta vista exista en la carpeta correcta
    }

    @GetMapping("/alumno/ver")
    public String verAlumnos(@RequestParam(required = false) String format, Model model) {
        model.addAttribute("alumnos", alumnoService.listar());
        model.addAttribute("titulo", "Lista de alumnos");

        if ("pdf".equals(format)) {
            return "alumno/ver.pdf";
        } else if ("xlsx".equals(format)) {
            return "alumno/ver.xlsx";
        }

        return "alumno/ver";
    }


    // Manejo de errores
    @GetMapping("/error")
    public String error() {
        return "error"; // Asegúrate de que este archivo HTML exista
    }
}
