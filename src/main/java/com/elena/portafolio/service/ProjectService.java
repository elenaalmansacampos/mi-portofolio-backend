package com.elena.portafolio.service;

import com.elena.portafolio.dto.PagedResponse;
import com.elena.portafolio.dto.ProjectRequest;
import com.elena.portafolio.dto.ProjectResponse;
import com.elena.portafolio.entity.Project;
import com.elena.portafolio.exception.ResourceNotFoundException;
import com.elena.portafolio.repository.ProjectRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }


    @Transactional(readOnly = true)
    public PagedResponse<ProjectResponse> findAll(Pageable pageable) {

        Page<ProjectResponse> page =
                projectRepository.findAll(pageable).map(this::toResponse);

        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }


    @Transactional(readOnly = true)
    public List<ProjectResponse> findFeatured() {

        return projectRepository
                .findByFeaturedTrueOrderByDisplayOrderAscCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        return toResponse(findEntity(id));
    }


    @Transactional
    public ProjectResponse create(ProjectRequest request) {

        Project project = new Project(
                request.title(),
                request.description(),
                request.technologies(),
                request.imageUrl(),
                request.githubUrl(),
                request.demoUrl()
        );

        apply(request, project, true);

        return toResponse(projectRepository.save(project));
    }


    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {

        Project existing = findEntity(id);

        apply(request, existing, false);

        return toResponse(projectRepository.save(existing));
    }


    @Transactional
    public void delete(Long id) {

        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proyecto no encontrado");
        }

        projectRepository.deleteById(id);
    }


    private Project findEntity(Long id) {

        return projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Proyecto no encontrado"));
    }


    private void apply(
            ProjectRequest request,
            Project project,
            boolean creating
    ) {

        project.setTitle(request.title());
        project.setDescription(request.description());
        project.setTechnologies(request.technologies());
        project.setImageUrl(request.imageUrl());
        project.setGithubUrl(request.githubUrl());
        project.setDemoUrl(request.demoUrl());
        project.setFeatured(Boolean.TRUE.equals(request.featured()));
        project.setDisplayOrder(
                request.displayOrder() == null ? 0 : request.displayOrder()
        );

        Long currentId = creating ? null : project.getId();
        String requestedSlug = request.slug();

        if (requestedSlug != null && !requestedSlug.isBlank()) {
            project.setSlug(generateUniqueSlug(requestedSlug, currentId));
        } else if (creating
                || project.getSlug() == null
                || project.getSlug().isBlank()) {
            project.setSlug(generateUniqueSlug(request.title(), currentId));
        }
    }


    private String generateUniqueSlug(String source, Long currentId) {

        String base = slugify(source);
        String candidate = base;
        int suffix = 2;

        while (currentId == null
                ? projectRepository.existsBySlug(candidate)
                : projectRepository.existsBySlugAndIdNot(candidate, currentId)) {

            candidate = base + "-" + suffix++;
        }

        return candidate;
    }


    private String slugify(String source) {

        String slug = Normalizer.normalize(source, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");

        return slug.isBlank() ? "proyecto" : slug;
    }


    private ProjectResponse toResponse(Project project) {

        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getSlug(),
                project.getDescription(),
                project.getTechnologies(),
                project.getImageUrl(),
                project.getGithubUrl(),
                project.getDemoUrl(),
                project.isFeatured(),
                project.getDisplayOrder(),
                project.getCreatedAt()
        );
    }
}
