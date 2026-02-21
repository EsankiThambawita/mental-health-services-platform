package com.nsbm.health.resourcesmoodtrackingservice.controller;

import com.nsbm.health.resourcesmoodtrackingservice.model.Resource;
import com.nsbm.health.resourcesmoodtrackingservice.service.ResourceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Resource operations
 */
@RestController
@RequestMapping("/v1/resources")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Resources", description = "APIs for managing mental health resources")
public class ResourceController {

    private final ResourceService resourceService;

    /**
     * Create a new resource
     */
    @PostMapping
    public ResponseEntity<Resource> createResource(@Valid @RequestBody Resource resource) {
        Resource created = resourceService.createResource(resource);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Get resource by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResourceById(@PathVariable String id) {
        Optional<Resource> resource = resourceService.getResourceById(id);
        return resource.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get all active resources
     */
    @GetMapping
    public ResponseEntity<List<Resource>> getAllActiveResources() {
        List<Resource> resources = resourceService.getAllActiveResources();
        return ResponseEntity.ok(resources);
    }

    /**
     * Get resources by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Resource>> getResourcesByCategory(@PathVariable String category) {
        List<Resource> resources = resourceService.getResourcesByCategory(category);
        return ResponseEntity.ok(resources);
    }

    /**
     * Get resources by type
     */
    @GetMapping("/type/{resourceType}")
    public ResponseEntity<List<Resource>> getResourcesByType(@PathVariable String resourceType) {
        List<Resource> resources = resourceService.getResourcesByType(resourceType);
        return ResponseEntity.ok(resources);
    }

    /**
     * Recommend resources for a mood level (sorted by popularity)
     */
    @GetMapping("/recommend/{moodLevel}")
    public ResponseEntity<List<Resource>> recommendResourcesForMood(@PathVariable Integer moodLevel) {
        if (moodLevel < 1 || moodLevel > 10) {
            return ResponseEntity.badRequest().build();
        }
        List<Resource> resources = resourceService.recommendResourcesForMood(moodLevel);
        return ResponseEntity.ok(resources);
    }

    /**
     * Get resources by difficulty
     */
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<Resource>> getResourcesByDifficulty(@PathVariable String difficulty) {
        List<Resource> resources = resourceService.getResourcesByDifficulty(difficulty);
        return ResponseEntity.ok(resources);
    }

    /**
     * Search resources
     */
    @GetMapping("/search")
    public ResponseEntity<List<Resource>> searchResources(@RequestParam String query) {
        List<Resource> resources = resourceService.searchResources(query);
        return ResponseEntity.ok(resources);
    }

    /**
     * Get resources by tags
     */
    @PostMapping("/tags")
    public ResponseEntity<List<Resource>> getResourcesByTags(@RequestBody List<String> tags) {
        List<Resource> resources = resourceService.getResourcesByTags(tags);
        return ResponseEntity.ok(resources);
    }

    /**
     * Update a resource
     */
    @PutMapping("/{id}")
    public ResponseEntity<Resource> updateResource(
            @PathVariable String id,
            @Valid @RequestBody Resource resource) {
        Resource updated = resourceService.updateResource(id, resource);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Update resource status (activate/deactivate)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Resource> updateResourceStatus(
            @PathVariable String id,
            @RequestParam Boolean isActive) {
        Resource updated = isActive
            ? resourceService.activateResource(id)
            : resourceService.deactivateResource(id);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete a resource
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable String id) {
        if (resourceService.deleteResource(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
