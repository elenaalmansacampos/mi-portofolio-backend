package com.elena.portafolio;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiErrorHandlingTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void loginSinPasswordDevuelve400ConDetalles() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"Elena"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Datos inválidos"))
                .andExpect(jsonPath("$.details.password").exists());
    }


    @Test
    void contactoConEmailInvalidoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Elena","email":"no-es-email","message":"Mensaje de prueba"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.email").exists());
    }


    @Test
    void proyectoInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/api/projects/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Proyecto no encontrado"));
    }


    @Test
    void crearProyectoSinTituloDevuelve400() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"Sin título"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.title").exists());
    }


    @Test
    void eliminarProyectoInexistenteDevuelve404() throws Exception {
        mockMvc.perform(
                        org.springframework.test.web.servlet.request
                                .MockMvcRequestBuilders.delete("/api/projects/999999")
                                .with(user("admin").roles("ADMIN"))
                )
                .andExpect(status().isNotFound());
    }
}
