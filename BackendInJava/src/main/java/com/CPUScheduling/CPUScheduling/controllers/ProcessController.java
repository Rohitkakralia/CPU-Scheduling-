package com.CPUScheduling.CPUScheduling.controllers;

import com.CPUScheduling.CPUScheduling.entities.ScheduleResult;
import com.CPUScheduling.CPUScheduling.services.SchedulerService;
import com.CPUScheduling.CPUScheduling.services.SchedulerServiceForNPSJF;
import com.CPUScheduling.CPUScheduling.services.SchedulerServiceForPSJF;
import com.CPUScheduling.CPUScheduling.entities.Process;
import com.CPUScheduling.CPUScheduling.services.SchedulerServiceForRR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/scheduler")
@CrossOrigin(origins = "*") // Enable CORS for frontend requests
public class ProcessController {

    public List<ScheduleResult> result;
    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private SchedulerServiceForNPSJF schedulerServiceForNPSJF;
    @Autowired
    private SchedulerServiceForPSJF schedulerServiceForPSJF;
    @Autowired
    private SchedulerServiceForRR schedulerServiceForRR;

    // Create a DTO class to represent the request body
    public static class SchedulingRequest {
        private List<Process> processes;
        private Integer timeQuantum;

        // Getters and setters
        public List<Process> getProcesses() {
            return processes;
        }

        public void setProcesses(List<Process> processes) {
            this.processes = processes;
        }

        public Integer getTimeQuantum() {
            return timeQuantum;
        }

        public void setTimeQuantum(Integer timeQuantum) {
            this.timeQuantum = timeQuantum;
        }
    }

    @PostMapping("/fcfs")
    public ResponseEntity<List<ScheduleResult>> runFCFS(@RequestBody SchedulingRequest request) {
        result = schedulerService.runFCFS(request.getProcesses());
        System.out.println("FCFS Scheduling Result:");
        for (ScheduleResult r : result) {
            System.out.println(r);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("np-sjf")
    public ResponseEntity<List<ScheduleResult>> runNPSJF(@RequestBody SchedulingRequest request){
        result = schedulerServiceForNPSJF.runNPSJF(request.getProcesses());
        System.out.println("NP-SJF Scheduling Result:");
        for (ScheduleResult r : result) {
            System.out.println(r);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("p-sjf")
    public ResponseEntity<List<ScheduleResult>> runPSJF(@RequestBody SchedulingRequest request){
        result = schedulerServiceForPSJF.runPSJF(request.getProcesses());
        System.out.println("P-SJF Scheduling Result:");
        for (ScheduleResult r : result) {
            System.out.println(r);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("rr")
    public ResponseEntity<List<ScheduleResult>> runRR(@RequestBody SchedulingRequest request){
        // Pass the timeQuantum to the RR service
        result = schedulerServiceForRR.runRR(request.getProcesses(), request.getTimeQuantum());
        System.out.println("RR Scheduling Result:");
        for (ScheduleResult r : result) {
            System.out.println(r);
        }
        return ResponseEntity.ok(result);
    }
}