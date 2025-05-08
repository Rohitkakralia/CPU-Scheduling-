package com.CPUScheduling.CPUScheduling.services;

import com.CPUScheduling.CPUScheduling.entities.GantChart;
import com.CPUScheduling.CPUScheduling.entities.Process;
import com.CPUScheduling.CPUScheduling.entities.ScheduleResult;
import com.sun.tools.jconsole.JConsoleContext;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SchedulerServiceForNPPS {
    HashMap<String, int[]> processToDetails = new HashMap<>();
    HashSet<String> completedProcesses = new HashSet<>();

    PriorityQueue<String> initialQueue = new PriorityQueue<>(
            (a, b) -> {
                int arrivalA = processToDetails.get(a)[0];
                int arrivalB = processToDetails.get(b)[0];
                if(arrivalA != arrivalB){
                    return arrivalA - arrivalB;
                }else{
                    return processToDetails.get(b)[2] - processToDetails.get(a)[2];
                }
            }
    );

    public List<ScheduleResult> runNPPS(List<Process> processes) {
        processToDetails.clear();
        completedProcesses.clear();

        for(Process p: processes){
            if(p != null && p.getprocessId() != null){
                int[] details = new int[3];
                details[0] = p.getArrivalTime();
                details[1] = p.getBurstTime();
                details[2] = p.getPriority();

                processToDetails.put(p.getprocessId(), details);
            }
        }

        for(Process p: processes){
            if(p != null && p.getprocessId() != null){
                initialQueue.offer(p.getprocessId());
            }
        }

        int currentTime = 0;
        List<ScheduleResult> ans = new ArrayList<>();
        List<GantChart> ganttChart = new ArrayList<>();
        PriorityQueue<String> arrivedQueue = new PriorityQueue<>(
                (a, b) -> {
                    if(processToDetails.get(a)[2] != processToDetails.get(b)[2]){
                        return processToDetails.get(b)[2] - processToDetails.get(a)[2];
                    }else{
                        if(processToDetails.get(a)[0] != processToDetails.get(b)[0]){
                            return processToDetails.get(a)[0] - processToDetails.get(b)[0];
                        }else{
                            return a.compareTo(b);
                        }
                    }
                }
        );

        while(!arrivedQueue.isEmpty() || !initialQueue.isEmpty()) {

            while (!initialQueue.isEmpty()) {
                String nextProcess = initialQueue.peek();
                int arrivalTime = processToDetails.get(nextProcess)[0];
                if (arrivalTime <= currentTime) {
                    arrivedQueue.offer(initialQueue.poll());
                } else {
                    break;
                }
            }

            if (arrivedQueue.isEmpty() && !initialQueue.isEmpty()) {
                String nextProcess = initialQueue.peek();
                int nextArrivalTime = processToDetails.get(nextProcess)[0];
                if (currentTime < nextArrivalTime) {
                    // Record idle time in Gantt chart
                    ganttChart.add(new GantChart("IDLE", currentTime, nextArrivalTime));
                    currentTime = nextArrivalTime;
                }
                continue;
            }

            String currentProcess = arrivedQueue.poll();
            if (currentProcess == null) continue;

            if (completedProcesses.contains(currentProcess)) continue;

            int[] details = processToDetails.get(currentProcess);
            int arrivalTime = details[0];
            int burstTime = details[1];

            // Record process execution in Gantt chart
            int startTime = currentTime;
            int completionTime = currentTime + burstTime;
            ganttChart.add(new GantChart(currentProcess, startTime, completionTime));

            // Calculate metrics
            int turnaroundTime = completionTime - arrivalTime;
            int waitingTime = turnaroundTime - burstTime;

            // Update current time
            currentTime = completionTime;

            // Mark process as completed
            completedProcesses.add(currentProcess);

            // Create and add the result
            ScheduleResult result = new ScheduleResult();
            result.setName(currentProcess);
            result.setArivalTime(arrivalTime);
            result.setBurstTime(burstTime);
            result.setCriticalTime(completionTime);
            result.setTurnAroundTime(turnaroundTime);
            result.setWaitingTime(waitingTime);
            result.setSequence(new ArrayList<>(ganttChart));

            ans.add(result);

            System.out.println("Processed: " + currentProcess +
                    ", AT: " + arrivalTime +
                    ", CT: " + completionTime +
                    ", TAT: " + turnaroundTime +
                    ", WT: " + waitingTime);
        }

        return ans;

    }
}
