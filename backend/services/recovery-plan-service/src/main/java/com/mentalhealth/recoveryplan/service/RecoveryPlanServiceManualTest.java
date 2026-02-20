package com.mentalhealth.recoveryplan.service;

import java.time.LocalDateTime;
import java.util.*;

import com.mentalhealth.recoveryplan.dto.*;
import com.mentalhealth.recoveryplan.model.*;
import com.mentalhealth.recoveryplan.repository.RecoveryPlanRepository;

public class RecoveryPlanServiceManualTest {

    public static void main(String[] args) {

        FakeRecoveryPlanRepository fakeRepo = new FakeRecoveryPlanRepository();
        RecoveryPlanService service = new RecoveryPlanService(fakeRepo);

        System.out.println("===== MANUAL SERVICE TEST =====");

        testCreatePlan(service);
        testAddTask(service);
        testCompleteTask(service);

        System.out.println("===== TEST FINISHED =====");
    }

    private static void testCreatePlan(RecoveryPlanService service) {

        CreateRecoveryPlanRequest request = new CreateRecoveryPlanRequest(
                "patient1",
                "counselor1",
                null,
                LocalDateTime.now().plusMonths(3),
                "3 month structured plan",
                LocalDateTime.now(),
                "Depression Recovery");

        RecoveryPlanResponse response = service.createPlan(request);

        if (response != null && response.getPatientId().equals("patient1")) {
            System.out.println("✅ Create Plan PASSED");
        } else {
            System.out.println("❌ Create Plan FAILED");
        }
    }

    private static void testAddTask(RecoveryPlanService service) {

        List<RecoveryPlanResponse> plans = service.getPlansByCounselor("counselor1");
        String planId = plans.get(0).getId();

        CreateTaskRequest taskRequest = new CreateTaskRequest(
                "Daily journaling",
                LocalDateTime.now().plusDays(7),
                "Reflect honestly");

        RecoveryPlanResponse updated = service.addTask(planId, "counselor1", taskRequest);

        if (updated.getTasks().size() == 1) {
            System.out.println("✅ Add Task PASSED");
        } else {
            System.out.println("❌ Add Task FAILED");
        }
    }

    private static void testCompleteTask(RecoveryPlanService service) {

        RecoveryPlanResponse plan = service.getPlansByPatient("patient1").get(0);

        String planId = plan.getId();
        String taskId = plan.getTasks().get(0).getTaskId();

        RecoveryPlanResponse updated = service.completeTask(planId, taskId, "patient1");

        if (updated.getTasks().get(0).isCompleted()) {
            System.out.println("✅ Complete Task PASSED");
        } else {
            System.out.println("❌ Complete Task FAILED");
        }
    }
}