package com.mentalhealth.recoveryplan.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentalhealth.recoveryplan.dto.CreateRecoveryPlanRequest;
import com.mentalhealth.recoveryplan.dto.CreateTaskRequest;
import com.mentalhealth.recoveryplan.dto.UpdatePlanStatusRequest;
import com.mentalhealth.recoveryplan.model.PlanStatus;
import com.mentalhealth.recoveryplan.repository.RecoveryPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RecoveryPlanIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecoveryPlanRepository repository;

    @PostConstruct
    void init() {
        repository.deleteAll(); // clear DB at startup
    }

    private String createPlan(String patientId, String counselorId, String title) throws Exception {
        CreateRecoveryPlanRequest request = new CreateRecoveryPlanRequest(
                patientId, counselorId, null, LocalDateTime.now().plusMonths(3),
                "Integration test description",
                LocalDateTime.now(),
                title);

        MvcResult result = mockMvc.perform(post("/api/recovery-plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asText();
    }

    private String addTask(String planId, String counselorId, String description) throws Exception {
        CreateTaskRequest taskRequest = new CreateTaskRequest(
                description,
                LocalDateTime.now().plusDays(7),
                "Notes");

        MvcResult result = mockMvc
                .perform(post("/api/recovery-plans/" + planId + "/counselor/" + counselorId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("tasks").get(0).get("taskId").asText();
    }

    public void runTests() throws Exception {
        System.out.println("Running integration test: complete workflow...");

        String planId = createPlan("patient123", "counselor456", "Full Workflow Plan");
        String taskId = addTask(planId, "counselor456", "Practice deep breathing");

        // Patient completes task
        mockMvc.perform(patch("/api/recovery-plans/" + planId + "/patient/patient123/tasks/" + taskId + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks[0].completed").value(true));

        // Counselor completes plan
        UpdatePlanStatusRequest statusRequest = new UpdatePlanStatusRequest(PlanStatus.COMPLETED);
        mockMvc.perform(patch("/api/recovery-plans/" + planId + "/counselor/counselor456/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        System.out.println("Integration test completed successfully!");
    }

    // You can create more methods like runAuthorizationTest(),
    // runCancelledPlanTest(), etc.
}