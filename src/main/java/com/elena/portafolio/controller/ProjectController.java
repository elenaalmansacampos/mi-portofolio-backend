package com.elena.portafolio.controller;


import com.elena.portafolio.entity.Project;
import com.elena.portafolio.service.ProjectService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/projects")
public class ProjectController {


    private final ProjectService projectService;


    public ProjectController(
            ProjectService projectService
    ) {
        this.projectService = projectService;
    }



    // Obtener todos los proyectos (público)
    @GetMapping
    public ResponseEntity<List<Project>> findAll() {

        return ResponseEntity.ok(
                projectService.findAll()
        );
    }



    // Obtener un proyecto por id (público)
    @GetMapping("/{id}")
    public ResponseEntity<Project> findById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                projectService.findById(id)
        );
    }



    // Crear proyecto (ADMIN)
    @PostMapping
    public ResponseEntity<Project> create(
            @RequestBody Project project
    ) {

        return ResponseEntity.ok(
                projectService.save(project)
        );
    }



    // Actualizar proyecto (ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<Project> update(
            @PathVariable Long id,
            @RequestBody Project project
    ) {

        return ResponseEntity.ok(
                projectService.update(id, project)
        );
    }



    // Eliminar proyecto (ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        projectService.delete(id);

        return ResponseEntity.noContent().build();
    }

}