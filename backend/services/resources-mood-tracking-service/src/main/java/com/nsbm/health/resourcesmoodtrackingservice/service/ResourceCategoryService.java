package com.nsbm.health.resourcesmoodtrackingservice.service;

import com.nsbm.health.resourcesmoodtrackingservice.model.ResourceCategory;
import com.nsbm.health.resourcesmoodtrackingservice.repository.ResourceCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Handles resource category management
@Service
@AllArgsConstructor
public class ResourceCategoryService {

    private final ResourceCategoryRepository resourceCategoryRepository;

    // Create new category
    public ResourceCategory createCategory(ResourceCategory category) {
        category.prePersist();
        return resourceCategoryRepository.save(category);
    }

    // Find category by ID
    public Optional<ResourceCategory> getCategoryById(String id) {
        return resourceCategoryRepository.findById(id);
    }

    // Get all active categories (ordered by display)
    public List<ResourceCategory> getAllActiveCategories() {
        return resourceCategoryRepository.findByIsActiveTrueOrderByDisplayOrder();
    }

    // Update category
    public ResourceCategory updateCategory(String id, ResourceCategory category) {
        Optional<ResourceCategory> existingCategory = resourceCategoryRepository.findById(id);
        if (existingCategory.isPresent()) {
            ResourceCategory existing = existingCategory.get();
            existing.setName(category.getName());
            existing.setDescription(category.getDescription());
            existing.setIcon(category.getIcon());
            existing.setColor(category.getColor());
            existing.setDisplayOrder(category.getDisplayOrder());
            existing.setTags(category.getTags());
            existing.setUpdatedAt(LocalDateTime.now());
            return resourceCategoryRepository.save(existing);
        }
        return null;
    }

    // Delete category
    public boolean deleteCategory(String id) {
        if (resourceCategoryRepository.existsById(id)) {
            resourceCategoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
