package com.elena.portafolio.controller;


import com.elena.portafolio.dto.PagedResponse;
import com.elena.portafolio.dto.ProjectRequest;
import com.elena.portafolio.dto.ProjectResponse;
import com.elena.portafolio.service.ProjectService;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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


    @GetMapping
    public ResponseEntity<PagedResponse<ProjectResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        Pageable pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by("displayOrder").ascending()
                        .and(Sort.by("createdAt").descending())
        );

        return ResponseEntity.ok(
                projectService.findAll(pageable)
        );
    }


    @GetMapping("/featured")
    public ResponseEntity<List<ProjectResponse>> findFeatured() {

        return ResponseEntity.ok(
                projectService.findFeatured()
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> findById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                projectService.findById(id)
        );
    }


    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            @Valid @RequestBody ProjectRequest request
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.create(request));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request
    ) {

        return ResponseEntity.ok(
                projectService.update(id, request)
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        projectService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
