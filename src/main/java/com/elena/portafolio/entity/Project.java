package com.elena.portafolio.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "projects")
public class Project {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String title;


    @Column(unique = true, length = 160)
    private String slug;


    @Column(columnDefinition = "TEXT")
    private String description;


    private String technologies;


    private String imageUrl;


    private String githubUrl;


    private String demoUrl;


    @Column(nullable = false)
    private boolean featured = false;


    @Column(nullable = false)
    private int displayOrder = 0;


    private LocalDateTime createdAt;


    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }


    public Project() {
    }


    public Project(
            String title,
            String description,
            String technologies,
            String imageUrl,
            String githubUrl,
            String demoUrl
    ) {

        this.title = title;
        this.description = description;
        this.technologies = technologies;
        this.imageUrl = imageUrl;
        this.githubUrl = githubUrl;
        this.demoUrl = demoUrl;
        this.createdAt = LocalDateTime.now();
    }



    public Long getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }


    public String getSlug() {
        return slug;
    }


    public String getDescription() {
        return description;
    }


    public String getTechnologies() {
        return technologies;
    }


    public String getImageUrl() {
        return imageUrl;
    }


    public String getGithubUrl() {
        return githubUrl;
    }


    public String getDemoUrl() {
        return demoUrl;
    }


    public boolean isFeatured() {
        return featured;
    }


    public int getDisplayOrder() {
        return displayOrder;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }



    public void setTitle(String title) {
        this.title = title;
    }


    public void setSlug(String slug) {
        this.slug = slug;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public void setTechnologies(String technologies) {
        this.technologies = technologies;
    }


    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }


    public void setDemoUrl(String demoUrl) {
        this.demoUrl = demoUrl;
    }


    public void setFeatured(boolean featured) {
        this.featured = featured;
    }


    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
