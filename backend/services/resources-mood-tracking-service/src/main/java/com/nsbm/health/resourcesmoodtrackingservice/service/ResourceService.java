package com.nsbm.health.resourcesmoodtrackingservice.service;

import com.nsbm.health.resourcesmoodtrackingservice.model.Resource;
import com.nsbm.health.resourcesmoodtrackingservice.repository.ResourceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing mental health resources (articles, exercises, etc)
 */
@Service
@AllArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    /**
     * Save a new resource to the database
     */
    public Resource createResource(Resource resource) {
        // Set creation timestamps
        resource.prePersist();
        resource.setPublishedAt(LocalDateTime.now());
        // Save and return the resource
        return resourceRepository.save(resource);
    }

    /**
     * Find a resource by its ID and increment view count
     */
    public Optional<Resource> getResourceById(String id) {
        // Get the resource from database
        Optional<Resource> resource = resourceRepository.findById(id);
        // Increase the view count when viewed
        resource.ifPresent(this::incrementViewCount);
        return resource;
    }

    /**
     * Get all resources that are currently active
     */
    public List<Resource> getAllActiveResources() {
        // Return only active resources
        return resourceRepository.findByIsActiveTrue();
    }

    /**
     * Find resources in a specific category
     */
    public List<Resource> getResourcesByCategory(String category) {
        // Filter by category and active status
        return resourceRepository.findByCategoryAndIsActiveTrue(category);
    }

    /**
     * Find resources by their type (article, exercise, etc)
     */
    public List<Resource> getResourcesByType(String resourceType) {
        // Return resources matching the type
        return resourceRepository.findByResourceType(resourceType);
    }


    /**
     * Search for resources using keywords
     */
    public List<Resource> searchResources(String searchText) {
        // Search in resource title, description, and content
        return resourceRepository.searchResources(searchText);
    }

    /**
     * Find resources by their difficulty level
     */
    public List<Resource> getResourcesByDifficulty(String difficulty) {
        // Get resources by difficulty and filter by active status
        return resourceRepository.findByDifficulty(difficulty)
                .stream()
                .filter(Resource::getIsActive)
                .collect(Collectors.toList());
    }

    /**
     * Find resources by tags
     */
    public List<Resource> getResourcesByTags(List<String> tags) {
        // Get all active resources and filter by tags
        return resourceRepository.findByIsActiveTrue()
                .stream()
                .filter(resource -> resource.getTags() != null &&
                        resource.getTags().stream().anyMatch(tags::contains))
                .collect(Collectors.toList());
    }

    /**
     * Recommend the best resources for a user's current mood
     */
    public List<Resource> recommendResourcesForMood(Integer moodLevel) {
        // Get resources suitable for this mood level
        List<Resource> recommendedResources = resourceRepository.findActiveResourcesByMoodLevel(moodLevel);

        // Sort by popularity (view count) so best resources show first
        return recommendedResources.stream()
                .sorted((r1, r2) -> {
                    // First, sort by view count (most viewed first)
                    if (!r1.getViewCount().equals(r2.getViewCount())) {
                        return r2.getViewCount().compareTo(r1.getViewCount());
                    }
                    // Then sort by category alphabetically
                    return r1.getCategory().compareTo(r2.getCategory());
                })
                .collect(Collectors.toList());
    }

    /**
     * Update an existing resource with new information
     */
    public Resource updateResource(String id, Resource resource) {
        // Find the resource by ID
        Optional<Resource> existingResource = resourceRepository.findById(id);
        if (existingResource.isPresent()) {
            // Update all fields
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
            // Save and return updated resource
            return resourceRepository.save(existing);
        }
        return null;
    }

    /**
     * Make a resource active so users can see it
     */
    public Resource activateResource(String id) {
        // Find the resource
        Optional<Resource> resource = resourceRepository.findById(id);
        if (resource.isPresent()) {
            // Set it to active and save
            Resource r = resource.get();
            r.setIsActive(true);
            r.setUpdatedAt(LocalDateTime.now());
            return resourceRepository.save(r);
        }
        return null;
    }

    /**
     * Hide a resource from users
     */
    public Resource deactivateResource(String id) {
        // Find the resource
        Optional<Resource> resource = resourceRepository.findById(id);
        if (resource.isPresent()) {
            // Set it to inactive and save
            Resource r = resource.get();
            r.setIsActive(false);
            r.setUpdatedAt(LocalDateTime.now());
            return resourceRepository.save(r);
        }
        return null;
    }

    /**
     * Delete a resource from the database
     */
    public boolean deleteResource(String id) {
        // Check if resource exists, then delete it
        if (resourceRepository.existsById(id)) {
            resourceRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Increase the view count when someone views a resource
     */
    private void incrementViewCount(Resource resource) {
        // Add 1 to the view count
        resource.setViewCount(resource.getViewCount() + 1);
        // Save the updated count
        resourceRepository.save(resource);
    }
}
