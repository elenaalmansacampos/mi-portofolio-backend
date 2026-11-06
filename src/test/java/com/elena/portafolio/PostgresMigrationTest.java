package com.elena.portafolio;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("testcontainers")
class PostgresMigrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void flywayAplicaLasMigracionesSobrePostgresReal() {
        Integer applied = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = TRUE",
                Integer.class
        );

        assertTrue(applied != null && applied >= 4);

        Integer slugColumn = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns "
                + "WHERE table_name = 'projects' AND column_name = 'slug'",
                Integer.class
        );

        assertEquals(1, slugColumn);

        Integer refreshTable = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables "
                + "WHERE table_name = 'refresh_tokens'",
                Integer.class
        );

        assertEquals(1, refreshTable);

        Integer newProjects = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM projects WHERE slug IN ("
                + "'fundacion-verisure',"
                + "'servido-hackathon-stellar',"
                + "'athletica-gestion-gimnasio')",
                Integer.class
        );

        assertEquals(3, newProjects);

        Integer duplicate = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM projects "
                + "WHERE slug = 'biblioteca-feminista-13'",
                Integer.class
        );

        assertEquals(0, duplicate);
    }
}
