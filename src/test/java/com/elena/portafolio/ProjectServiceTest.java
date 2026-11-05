package com.elena.portafolio;

import com.elena.portafolio.dto.PagedResponse;
import com.elena.portafolio.dto.ProjectRequest;
import com.elena.portafolio.dto.ProjectResponse;
import com.elena.portafolio.exception.ResourceNotFoundException;
import com.elena.portafolio.repository.ProjectRepository;
import com.elena.portafolio.service.ProjectService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @BeforeEach
    void cleanProjects() {
        projectRepository.deleteAll();
    }

    private ProjectRequest request(
            String title,
            boolean featured,
            int displayOrder
    ) {
        return new ProjectRequest(
                title,
                null,
                "Descripción de prueba",
                "React, Spring Boot",
                "http://imagen.com",
                "http://github.com",
                "http://demo.com",
                featured,
                displayOrder
        );
    }

    @Test
    void crearYBuscarProyectoGeneraSlug() {
        ProjectResponse created =
                projectService.create(request("Proyecto test", true, 3));

        assertNotNull(created.id());
        assertEquals("Proyecto test", created.title());
        assertEquals("proyecto-test", created.slug());
        assertTrue(created.featured());
        assertEquals(3, created.displayOrder());

        ProjectResponse found = projectService.findById(created.id());
        assertEquals("Proyecto test", found.title());
    }

    @Test
    void listarProyectosPaginadosYOrdenados() {
        projectService.create(request("Proyecto 1", false, 2));
        projectService.create(request("Proyecto 2", false, 1));

        PagedResponse<ProjectResponse> page = projectService.findAll(
                PageRequest.of(
                        0,
                        1,
                        Sort.by("displayOrder").ascending()
                )
        );

        assertEquals(2, page.totalElements());
        assertEquals(2, page.totalPages());
        assertEquals(1, page.content().size());
        assertEquals("Proyecto 2", page.content().get(0).title());
    }

    @Test
    void listarSoloProyectosDestacados() {
        projectService.create(request("Destacado", true, 0));
        projectService.create(request("Normal", false, 0));

        List<ProjectResponse> featured = projectService.findFeatured();

        assertEquals(1, featured.size());
        assertEquals("Destacado", featured.get(0).title());
    }

    @Test
    void slugDuplicadoRecibeSufijo() {
        ProjectResponse first =
                projectService.create(request("Mi Proyecto", false, 0));
        ProjectResponse second =
                projectService.create(request("Mi Proyecto", false, 0));

        assertEquals("mi-proyecto", first.slug());
        assertEquals("mi-proyecto-2", second.slug());
    }

    @Test
    void buscarProyectoInexistenteLanza404() {
        assertThrows(ResourceNotFoundException.class, () ->
                projectService.findById(9999L)
        );
    }

    @Test
    void actualizarProyecto() {
        ProjectResponse created =
                projectService.create(request("Antes", false, 0));

        ProjectResponse updated = projectService.update(
                created.id(),
                request("Después", true, 5)
        );

        assertEquals("Después", updated.title());
        assertEquals("antes", updated.slug());
        assertTrue(updated.featured());
        assertEquals(5, updated.displayOrder());
    }

    @Test
    void eliminarProyecto() {
        ProjectResponse created =
                projectService.create(request("A eliminar", false, 0));

        projectService.delete(created.id());

        assertThrows(ResourceNotFoundException.class, () ->
                projectService.findById(created.id())
        );
    }
}
