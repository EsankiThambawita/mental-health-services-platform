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
 * REST API for resource categories
 */
@RestController
@RequestMapping("/v1/resource-categories")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Resource Categories", description = "APIs for managing resource categories")
public class ResourceCategoryController {

    private final ResourceCategoryService resourceCategoryService;

    // Create new category
    @PostMapping
    public ResponseEntity<ResourceCategory> createCategory(@Valid @RequestBody ResourceCategory category) {
        ResourceCategory created = resourceCategoryService.createCategory(category);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Get category by ID
    @GetMapping("/{id}")
    public ResponseEntity<ResourceCategory> getCategoryById(@PathVariable String id) {
        Optional<ResourceCategory> category = resourceCategoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all active categories
    @GetMapping
    public ResponseEntity<List<ResourceCategory>> getAllActiveCategories() {
        List<ResourceCategory> categories = resourceCategoryService.getAllActiveCategories();
        return ResponseEntity.ok(categories);
    }

    // Update category
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

    // Delete category
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        if (resourceCategoryService.deleteCategory(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
