package com.elena.portafolio;

import com.elena.portafolio.entity.Project;
import com.elena.portafolio.repository.ProjectRepository;
import com.elena.portafolio.service.ProjectService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    private Project createProject(String title) {
        return new Project(
                title,
                "Descripción de prueba",
                "React, Spring Boot",
                "http://imagen.com",
                "http://github.com",
                "http://demo.com"
        );
    }

    @Test
    void guardarYBuscarProyecto() {
        Project saved = projectService.save(createProject("Proyecto test"));

        assertNotNull(saved.getId());
        assertEquals("Proyecto test", saved.getTitle());

        Project found = projectService.findById(saved.getId());
        assertEquals("Proyecto test", found.getTitle());
    }

    @Test
    void listarTodosLosProyectos() {
        projectRepository.deleteAll();
        projectService.save(createProject("Proyecto 1"));
        projectService.save(createProject("Proyecto 2"));

        List<Project> projects = projectService.findAll();

        assertEquals(2, projects.size());
    }

    @Test
    void buscarProyectoInexistenteLanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
                projectService.findById(9999L)
        );
    }

    @Test
    void actualizarProyecto() {
        Project saved = projectService.save(createProject("Antes"));

        saved.setTitle("Después");
        saved.setDescription("Nueva descripción");

        Project updated = projectService.update(saved.getId(), saved);

        assertEquals("Después", updated.getTitle());
        assertEquals("Nueva descripción", updated.getDescription());
    }

    @Test
    void eliminarProyecto() {
        Project saved = projectService.save(createProject("A eliminar"));

        projectService.delete(saved.getId());

        assertThrows(RuntimeException.class, () ->
                projectService.findById(saved.getId())
        );
    }
}
