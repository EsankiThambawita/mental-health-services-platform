package com.nsbm.health.resourcesmoodtrackingservice.controller;

import com.nsbm.health.resourcesmoodtrackingservice.model.ResourceCategory;
import com.nsbm.health.resourcesmoodtrackingservice.service.ResourceCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Resource Category operations
 */
@RestController
@RequestMapping("/v1/resource-categories")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Resource Categories", description = "APIs for managing resource categories")
public class ResourceCategoryController {

    private final ResourceCategoryService resourceCategoryService;

    /**
     * Create a new category
     */
    @PostMapping
    public ResponseEntity<ResourceCategory> createCategory(@Valid @RequestBody ResourceCategory category) {
        ResourceCategory created = resourceCategoryService.createCategory(category);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Get category by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceCategory> getCategoryById(@PathVariable String id) {
        Optional<ResourceCategory> category = resourceCategoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get all active categories
     */
    @GetMapping
    public ResponseEntity<List<ResourceCategory>> getAllActiveCategories() {
        List<ResourceCategory> categories = resourceCategoryService.getAllActiveCategories();
        return ResponseEntity.ok(categories);
    }


    /**
     * Get categories by tag
     */
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<ResourceCategory>> getCategoriesByTag(@PathVariable String tag) {
        List<ResourceCategory> categories = resourceCategoryService.getCategoriesByTag(tag);
        return ResponseEntity.ok(categories);
    }

    /**
     * Update a category
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResourceCategory> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody ResourceCategory category) {
        ResourceCategory updated = resourceCategoryService.updateCategory(id, category);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Update category status (activate/deactivate)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ResourceCategory> updateCategoryStatus(
            @PathVariable String id,
            @RequestParam Boolean isActive) {
        ResourceCategory updated = isActive
            ? resourceCategoryService.activateCategory(id)
            : resourceCategoryService.deactivateCategory(id);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Update display order
     */
    @PutMapping("/{id}/display-order")
    public ResponseEntity<ResourceCategory> updateDisplayOrder(
            @PathVariable String id,
            @RequestParam Integer displayOrder) {
        ResourceCategory updated = resourceCategoryService.updateDisplayOrder(id, displayOrder);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete a category
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        if (resourceCategoryService.deleteCategory(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

