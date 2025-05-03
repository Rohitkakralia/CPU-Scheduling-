package com.mongoDb.MongoDb.controllers;

import com.mongoDb.MongoDb.entities.Process;
import com.mongoDb.MongoDb.entities.ScheduleResult;
import com.mongoDb.MongoDb.services.SchedulerService;
import com.mongoDb.MongoDb.services.SchedulerServiceForNPSJF;
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
}
