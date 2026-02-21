package com.nsbm.health.resourcesmoodtrackingservice.service;

import com.nsbm.health.resourcesmoodtrackingservice.model.ResourceCategory;
import com.nsbm.health.resourcesmoodtrackingservice.repository.ResourceCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing resource categories (groups of resources)
 */
@Service
@AllArgsConstructor
public class ResourceCategoryService {

    private final ResourceCategoryRepository resourceCategoryRepository;

    /**
     * Save a new category to the database
     */
    public ResourceCategory createCategory(ResourceCategory category) {
        // Set timestamps
        category.prePersist();
        // Save and return
        return resourceCategoryRepository.save(category);
    }

    /**
     * Find a category by its ID
     */
    public Optional<ResourceCategory> getCategoryById(String id) {
        // Return the category if found
        return resourceCategoryRepository.findById(id);
    }

    /**
     * Get all active categories, ordered by display order
     */
    public List<ResourceCategory> getAllActiveCategories() {
        // Return only active categories in display order
        return resourceCategoryRepository.findByIsActiveTrueOrderByDisplayOrder();
    }


    /**
     * Find categories by a specific tag
     */
    public List<ResourceCategory> getCategoriesByTag(String tag) {
        // Return categories that have this tag
        return resourceCategoryRepository.findByTags(tag);
    }

    /**
     * Update a category with new information
     */
    public ResourceCategory updateCategory(String id, ResourceCategory category) {
        // Find the category
        Optional<ResourceCategory> existingCategory = resourceCategoryRepository.findById(id);
        if (existingCategory.isPresent()) {
            // Update all fields
            ResourceCategory existing = existingCategory.get();
            existing.setName(category.getName());
            existing.setDescription(category.getDescription());
            existing.setIcon(category.getIcon());
            existing.setColor(category.getColor());
            existing.setDisplayOrder(category.getDisplayOrder());
            existing.setTags(category.getTags());
            existing.setUpdatedAt(LocalDateTime.now());
            // Save and return updated category
            return resourceCategoryRepository.save(existing);
        }
        return null;
    }

    /**
     * Make a category active so it's visible
     */
    public ResourceCategory activateCategory(String id) {
        // Find the category
        Optional<ResourceCategory> category = resourceCategoryRepository.findById(id);
        if (category.isPresent()) {
            // Set to active and save
            ResourceCategory c = category.get();
            c.setIsActive(true);
            c.setUpdatedAt(LocalDateTime.now());
            return resourceCategoryRepository.save(c);
        }
        return null;
    }

    /**
     * Hide a category from users
     */
    public ResourceCategory deactivateCategory(String id) {
        // Find the category
        Optional<ResourceCategory> category = resourceCategoryRepository.findById(id);
        if (category.isPresent()) {
            // Set to inactive and save
            ResourceCategory c = category.get();
            c.setIsActive(false);
            c.setUpdatedAt(LocalDateTime.now());
            return resourceCategoryRepository.save(c);
        }
        return null;
    }

    /**
     * Delete a category from the database
     */
    public boolean deleteCategory(String id) {
        // Check if exists, then delete it
        if (resourceCategoryRepository.existsById(id)) {
            resourceCategoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Update the display order of a category
     */
    public ResourceCategory updateDisplayOrder(String id, Integer displayOrder) {
        // Find the category
        Optional<ResourceCategory> category = resourceCategoryRepository.findById(id);
        if (category.isPresent()) {
            // Update the display order
            ResourceCategory c = category.get();
            c.setDisplayOrder(displayOrder);
            c.setUpdatedAt(LocalDateTime.now());
            // Save and return
            return resourceCategoryRepository.save(c);
        }
        return null;
    }
}

