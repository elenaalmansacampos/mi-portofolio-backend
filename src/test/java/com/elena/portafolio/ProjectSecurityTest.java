package com.elena.portafolio;

import com.elena.portafolio.repository.ProjectRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProjectSecurityTest {

    private static final String BODY = """
            {"title":"Proyecto test","description":"desc","technologies":"Java"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void cleanProjects() {
        projectRepository.deleteAll();
    }

    @Test
    void getProyectosEsPublico() throws Exception {
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk());
    }

    @Test
    void crearProyectoSinTokenDevuelve401() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crearProyectoConRolUserDevuelve403() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearProyectoConRolAdminDevuelve201() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Proyecto test"))
                .andExpect(jsonPath("$.slug").value("proyecto-test"));
    }

    @Test
    void eliminarProyectoSinTokenDevuelve401() throws Exception {
        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void elIdEnviadoPorElClienteEsIgnorado() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":999,"title":"Sin id forzado"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(org.hamcrest.Matchers.not(999)));
    }
}
