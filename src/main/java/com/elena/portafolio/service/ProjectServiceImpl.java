package com.elena.portafolio.service;


import com.elena.portafolio.entity.Project;
import com.elena.portafolio.repository.ProjectRepository;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProjectServiceImpl implements ProjectService {


    private final ProjectRepository projectRepository;


    public ProjectServiceImpl(
            ProjectRepository projectRepository
    ) {
        this.projectRepository = projectRepository;
    }



    @Override
    public List<Project> findAll() {

        return projectRepository.findAll();

    }



    @Override
    public Project findById(Long id) {

        return projectRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Proyecto no encontrado")
                );

    }



    @Override
    public Project save(Project project) {

        return projectRepository.save(project);

    }



    @Override
    public Project update(
            Long id,
            Project project
    ) {


        Project existing = findById(id);


        existing.setTitle(project.getTitle());
        existing.setDescription(project.getDescription());
        existing.setTechnologies(project.getTechnologies());
        existing.setImageUrl(project.getImageUrl());
        existing.setGithubUrl(project.getGithubUrl());
        existing.setDemoUrl(project.getDemoUrl());


        return projectRepository.save(existing);

    }



    @Override
    public void delete(Long id) {

        projectRepository.deleteById(id);

    }

}