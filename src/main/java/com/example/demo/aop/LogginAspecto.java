package com.example.demo.aop;

import com.example.demo.domain.entities.Alumno;
import com.example.demo.domain.entities.Auditoria;
import com.example.demo.domain.entities.Curso;
import com.example.demo.domain.persistence.AuditoriaDao;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.Calendar;

@Component
@Aspect
public class LogginAspecto {

    private Long tx;

    @Autowired
    private AuditoriaDao auditoriaDao;

    @Around("execution(* com.example.demo.services.*ServiceImpl.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        Long currTime = System.currentTimeMillis();
        tx = System.currentTimeMillis();
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String metodo = "tx[" + tx + "] - " + joinPoint.getSignature().getName();

        if (joinPoint.getArgs().length > 0) {
            logger.info(metodo + "() INPUT:" + Arrays.toString(joinPoint.getArgs()));
        }
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            logger.error(e.getMessage());
        }
        logger.info(metodo + "(): tiempo transcurrido " + (System.currentTimeMillis() - currTime) + " ms.");
        return result;
    }

    @After("execution(* com.example.demo.controllers.*Controller.guardar*(..)) ||" +
            "execution(* com.example.demo.controllers.*Controller.editar*(..)) ||" +
            "execution(* com.example.demo.controllers.*Controller.eliminar*(..))")
    public void auditoria(JoinPoint joinPoint) {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String metodo = joinPoint.getSignature().getName();
        Integer id = null;

        Object[] parametros = joinPoint.getArgs();
        if (parametros.length == 0) {
            logger.error("No se proporcionaron argumentos para el método.");
            return;
        }

        Object entidad = parametros[0];

        try {
            if (metodo.startsWith("guardar") && entidad instanceof Curso) {
                Curso curso = (Curso) entidad;
                id = curso.getId();
            } else if (metodo.startsWith("guardar") && entidad instanceof Alumno) {
                Alumno alumno = (Alumno) entidad;
                id = alumno.getId();
            } else if (metodo.startsWith("editar") || metodo.startsWith("eliminar")) {
                if (entidad instanceof Integer) {
                    id = (Integer) entidad;
                } else {
                    logger.error("El parámetro no es de tipo Integer para editar o eliminar.");
                    return;
                }
            } else {
                logger.error("Tipo de entidad no soportado para la auditoría.");
                return;
            }

            if (id != null) {
                String traza = "tx[" + tx + "] - " + metodo;
                logger.info(traza + "(): registrando auditoria...");
                auditoriaDao.save(new Auditoria(entidad.getClass().getSimpleName().toLowerCase(), id, Calendar.getInstance().getTime(), "usuario", metodo));
            }
        } catch (Exception e) {
            logger.error("Error al registrar la auditoría: " + e.getMessage());
        }
    }
}
