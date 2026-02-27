package com.nsbm.health.resourcesmoodtrackingservice.util;

import com.nsbm.health.resourcesmoodtrackingservice.model.Resource;
import com.nsbm.health.resourcesmoodtrackingservice.model.ResourceCategory;
import com.nsbm.health.resourcesmoodtrackingservice.repository.ResourceRepository;
import com.nsbm.health.resourcesmoodtrackingservice.repository.ResourceCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Sample data loader for initialization
 * Loads sample resources and categories when the app starts
 */
@Component
@AllArgsConstructor
public class SampleDataLoader implements CommandLineRunner {

    private final ResourceCategoryRepository resourceCategoryRepository;
    private final ResourceRepository resourceRepository;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Check if data already exists
            if (resourceCategoryRepository.count() == 0) {
                loadSampleCategories();
                loadSampleResources();
                System.out.println("✅ Sample data loaded successfully!");
            }
        } catch (Exception e) {
            // If MongoDB is not available, just log and continue
            // The app will still work, just without sample data
            System.out.println("⚠️ Could not load sample data - MongoDB may not be running yet");
            System.out.println("   The application will still work, but you'll need to create data manually");
        }
    }

    private void loadSampleCategories() {
        ResourceCategory stressManagement = new ResourceCategory();
        stressManagement.setName("Stress Management");
        stressManagement.setDescription("Resources for managing stress and anxiety");
        stressManagement.setIcon("stress");
        stressManagement.setColor("#FF6B6B");
        stressManagement.setDisplayOrder(1);
        stressManagement.setTags(Arrays.asList("stress", "anxiety", "relaxation"));
        stressManagement.setIsActive(true);
        stressManagement.setCreatedAt(LocalDateTime.now());
        stressManagement.setUpdatedAt(LocalDateTime.now());
        resourceCategoryRepository.save(stressManagement);

        ResourceCategory meditation = new ResourceCategory();
        meditation.setName("Meditation & Mindfulness");
        meditation.setDescription("Guided meditations and mindfulness practices");
        meditation.setIcon("meditation");
        meditation.setColor("#4ECDC4");
        meditation.setDisplayOrder(2);
        meditation.setTags(Arrays.asList("meditation", "mindfulness", "peace"));
        meditation.setIsActive(true);
        meditation.setCreatedAt(LocalDateTime.now());
        meditation.setUpdatedAt(LocalDateTime.now());
        resourceCategoryRepository.save(meditation);

        ResourceCategory exercise = new ResourceCategory();
        exercise.setName("Physical Exercise");
        exercise.setDescription("Exercises and activities for physical health");
        exercise.setIcon("exercise");
        exercise.setColor("#95E1D3");
        exercise.setDisplayOrder(3);
        exercise.setTags(Arrays.asList("exercise", "fitness", "movement"));
        exercise.setIsActive(true);
        exercise.setCreatedAt(LocalDateTime.now());
        exercise.setUpdatedAt(LocalDateTime.now());
        resourceCategoryRepository.save(exercise);

        ResourceCategory crisis = new ResourceCategory();
        crisis.setName("Crisis Support");
        crisis.setDescription("Immediate support and crisis resources");
        crisis.setIcon("crisis");
        crisis.setColor("#F38181");
        crisis.setDisplayOrder(4);
        crisis.setTags(Arrays.asList("crisis", "emergency", "help"));
        crisis.setIsActive(true);
        crisis.setCreatedAt(LocalDateTime.now());
        crisis.setUpdatedAt(LocalDateTime.now());
        resourceCategoryRepository.save(crisis);

        ResourceCategory selfCare = new ResourceCategory();
        selfCare.setName("Self Care");
        selfCare.setDescription("Self-care practices and routines");
        selfCare.setIcon("selfcare");
        selfCare.setColor("#AA96DA");
        selfCare.setDisplayOrder(5);
        selfCare.setTags(Arrays.asList("self-care", "wellness", "health"));
        selfCare.setIsActive(true);
        selfCare.setCreatedAt(LocalDateTime.now());
        selfCare.setUpdatedAt(LocalDateTime.now());
        resourceCategoryRepository.save(selfCare);
    }

    private void loadSampleResources() {
        // Breathing Exercise
        Resource breathing = new Resource();
        breathing.setTitle("4-7-8 Breathing Technique");
        breathing.setDescription("A simple yet powerful breathing technique to calm your nervous system");
        breathing.setContent("Inhale for 4 counts, hold for 7 counts, exhale for 8 counts. Repeat 4 times.");
        breathing.setCategory("Stress Management");
        breathing.setResourceType("TECHNIQUE");
        breathing.setTags(Arrays.asList("breathing", "relaxation", "quick-relief"));
        breathing.setRecommendedMoodLevels(Arrays.asList(1, 2, 3, 4));
        breathing.setDifficulty("EASY");
        breathing.setDurationMinutes(5);
        breathing.setAuthor("Mental Health Services");
        breathing.setIsActive(true);
        breathing.setViewCount(0);
        breathing.setCreatedAt(LocalDateTime.now());
        breathing.setUpdatedAt(LocalDateTime.now());
        breathing.setPublishedAt(LocalDateTime.now());
        resourceRepository.save(breathing);

        // Meditation Article
        Resource meditation = new Resource();
        meditation.setTitle("Benefits of Daily Meditation");
        meditation.setDescription("Learn about the science-backed benefits of incorporating meditation into your daily routine");
        meditation.setContent("Meditation can reduce stress, improve focus, and enhance emotional well-being...");
        meditation.setCategory("Meditation & Mindfulness");
        meditation.setResourceType("ARTICLE");
        meditation.setTags(Arrays.asList("meditation", "mental-health", "science"));
        meditation.setRecommendedMoodLevels(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        meditation.setDifficulty("EASY");
        meditation.setDurationMinutes(10);
        meditation.setAuthor("Mental Health Services");
        meditation.setIsActive(true);
        meditation.setViewCount(0);
        meditation.setCreatedAt(LocalDateTime.now());
        meditation.setUpdatedAt(LocalDateTime.now());
        meditation.setPublishedAt(LocalDateTime.now());
        resourceRepository.save(meditation);

        // Walking Exercise
        Resource walking = new Resource();
        walking.setTitle("Mindful Walking for Mental Wellness");
        walking.setDescription("Discover how a simple walk can transform your mental health");
        walking.setContent("Walking in nature can reduce anxiety and improve mood. Start with 15-30 minutes daily...");
        walking.setCategory("Physical Exercise");
        walking.setResourceType("EXERCISE");
        walking.setTags(Arrays.asList("exercise", "walking", "nature"));
        walking.setRecommendedMoodLevels(Arrays.asList(2, 3, 4, 5, 6));
        walking.setDifficulty("EASY");
        walking.setDurationMinutes(30);
        walking.setAuthor("Mental Health Services");
        walking.setIsActive(true);
        walking.setViewCount(0);
        walking.setCreatedAt(LocalDateTime.now());
        walking.setUpdatedAt(LocalDateTime.now());
        walking.setPublishedAt(LocalDateTime.now());
        resourceRepository.save(walking);

        // Crisis Helpline
        Resource crisis = new Resource();
        crisis.setTitle("National Crisis Helpline");
        crisis.setDescription("24/7 crisis support available. You are not alone.");
        crisis.setContent("Call: 1-800-273-8255 (Suicide & Crisis Lifeline)\nText: HOME to 741741 (Crisis Text Line)\nWe're here to help.");
        crisis.setCategory("Crisis Support");
        crisis.setResourceType("CRISIS_INFO");
        crisis.setTags(Arrays.asList("crisis", "emergency", "support"));
        crisis.setRecommendedMoodLevels(Arrays.asList(1, 2));
        crisis.setDifficulty("EASY");
        crisis.setAuthor("Mental Health Services");
        crisis.setIsActive(true);
        crisis.setViewCount(0);
        crisis.setCreatedAt(LocalDateTime.now());
        crisis.setUpdatedAt(LocalDateTime.now());
        crisis.setPublishedAt(LocalDateTime.now());
        resourceRepository.save(crisis);

        // Sleep Hygiene
        Resource sleep = new Resource();
        sleep.setTitle("Improve Your Sleep Quality");
        sleep.setDescription("Practical tips for better sleep and rest");
        sleep.setContent("Maintain a consistent sleep schedule, avoid screens 1 hour before bed, keep your room cool and dark...");
        sleep.setCategory("Self Care");
        sleep.setResourceType("ARTICLE");
        sleep.setTags(Arrays.asList("sleep", "self-care", "wellness"));
        sleep.setRecommendedMoodLevels(Arrays.asList(1, 2, 3, 4, 5));
        sleep.setDifficulty("EASY");
        sleep.setDurationMinutes(15);
        sleep.setAuthor("Mental Health Services");
        sleep.setIsActive(true);
        sleep.setViewCount(0);
        sleep.setCreatedAt(LocalDateTime.now());
        sleep.setUpdatedAt(LocalDateTime.now());
        sleep.setPublishedAt(LocalDateTime.now());
        resourceRepository.save(sleep);

        // Gratitude Exercise
        Resource gratitude = new Resource();
        gratitude.setTitle("Gratitude Journaling");
        gratitude.setDescription("A simple yet powerful practice to boost your mood");
        gratitude.setContent("Write down 3 things you're grateful for each day. This practice can significantly improve your mental health...");
        gratitude.setCategory("Self Care");
        gratitude.setResourceType("EXERCISE");
        gratitude.setTags(Arrays.asList("gratitude", "journaling", "positive-thinking"));
        gratitude.setRecommendedMoodLevels(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        gratitude.setDifficulty("EASY");
        gratitude.setDurationMinutes(10);
        gratitude.setAuthor("Mental Health Services");
        gratitude.setIsActive(true);
        gratitude.setViewCount(0);
        gratitude.setCreatedAt(LocalDateTime.now());
        gratitude.setUpdatedAt(LocalDateTime.now());
        gratitude.setPublishedAt(LocalDateTime.now());
        resourceRepository.save(gratitude);
    }
}

