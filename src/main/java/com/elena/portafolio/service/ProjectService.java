package com.elena.portafolio.service;

import com.elena.portafolio.entity.Project;

import java.util.List;


public interface ProjectService {


    List<Project> findAll();


    Project findById(Long id);


    Project save(Project project);


    Project update(Long id, Project project);


    void delete(Long id);

}