package com.elena.portafolio.service;

import com.elena.portafolio.entity.Project;
import com.elena.portafolio.repository.ProjectRepository;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
    }

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public Project update(Long id, Project project) {
        Project existing = findById(id);

        existing.setTitle(project.getTitle());
        existing.setDescription(project.getDescription());
        existing.setTechnologies(project.getTechnologies());
        existing.setImageUrl(project.getImageUrl());
        existing.setGithubUrl(project.getGithubUrl());
        existing.setDemoUrl(project.getDemoUrl());

        return projectRepository.save(existing);
    }

    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

}
