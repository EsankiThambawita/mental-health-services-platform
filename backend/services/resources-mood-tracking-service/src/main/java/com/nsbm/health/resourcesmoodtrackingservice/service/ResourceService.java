package com.nsbm.health.resourcesmoodtrackingservice.service;

import com.nsbm.health.resourcesmoodtrackingservice.model.Resource;
import com.nsbm.health.resourcesmoodtrackingservice.repository.ResourceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Handles resource management
@Service
@AllArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    // Create new resource
    public Resource createResource(Resource resource) {
        resource.prePersist();
        resource.setPublishedAt(LocalDateTime.now());
        return resourceRepository.save(resource);
    }

    // Find resource by ID (and track views)
    public Optional<Resource> getResourceById(String id) {
        Optional<Resource> resource = resourceRepository.findById(id);
        resource.ifPresent(this::incrementViewCount);
        return resource;
    }

    // Get all active resources
    public List<Resource> getAllActiveResources() {
        return resourceRepository.findByIsActiveTrue();
    }

    // Update resource
    public Resource updateResource(String id, Resource resource) {
        Optional<Resource> existingResource = resourceRepository.findById(id);
        if (existingResource.isPresent()) {
            Resource existing = existingResource.get();
            existing.setTitle(resource.getTitle());
            existing.setDescription(resource.getDescription());
            existing.setContent(resource.getContent());
            existing.setCategory(resource.getCategory());
            existing.setResourceType(resource.getResourceType());
            existing.setTags(resource.getTags());
            existing.setRecommendedMoodLevels(resource.getRecommendedMoodLevels());
            existing.setDifficulty(resource.getDifficulty());
            existing.setDurationMinutes(resource.getDurationMinutes());
            existing.setSourceUrl(resource.getSourceUrl());
            existing.setAuthor(resource.getAuthor());
            existing.setUpdatedAt(LocalDateTime.now());
            return resourceRepository.save(existing);
        }
        return null;
    }

    // Delete resource
    public boolean deleteResource(String id) {
        if (resourceRepository.existsById(id)) {
            resourceRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Increment view count when viewed
    private void incrementViewCount(Resource resource) {
        resource.setViewCount(resource.getViewCount() + 1);
        resourceRepository.save(resource);
    }
}
