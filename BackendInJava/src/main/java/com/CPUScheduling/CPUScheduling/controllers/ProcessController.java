package com.CPUScheduling.CPUScheduling.controllers;

import com.CPUScheduling.CPUScheduling.entities.ScheduleResult;
import com.CPUScheduling.CPUScheduling.services.SchedulerService;
import com.CPUScheduling.CPUScheduling.services.SchedulerServiceForNPSJF;
import com.CPUScheduling.CPUScheduling.services.SchedulerServiceForPSJF;
import com.CPUScheduling.CPUScheduling.entities.Process;
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

    @PostMapping("/fcfs")
    public ResponseEntity<List<ScheduleResult>> runFCFS(@RequestBody List<Process> processes) {
        result = schedulerService.runFCFS(processes);
        System.out.println("FCFS Scheduling Result:");
        for (ScheduleResult r : result) {
            System.out.println(r);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("np-sjf")
    public ResponseEntity<List<ScheduleResult>> runNPSJF(@RequestBody List<Process> processes){
        result = schedulerServiceForNPSJF.runNPSJF(processes);
        System.out.println("FCFS Scheduling Result:");
        for (ScheduleResult r : result) {
            System.out.println(r);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("p-sjf")
    public ResponseEntity<List<ScheduleResult>> runPSJF(@RequestBody List<Process> processes){
        result = schedulerServiceForPSJF.runPSJF(processes);
        System.out.println("FCFS Scheduling Result:");
        for (ScheduleResult r : result) {
            System.out.println(r);
        }
        return ResponseEntity.ok(result);
    }
}
