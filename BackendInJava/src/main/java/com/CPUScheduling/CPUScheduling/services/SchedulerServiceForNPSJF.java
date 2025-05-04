package com.CPUScheduling.CPUScheduling.services;

import com.CPUScheduling.CPUScheduling.entities.Process;
import com.CPUScheduling.CPUScheduling.entities.ScheduleResult;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SchedulerServiceForNPSJF {
    HashMap<String, int[]> processToAllDetails = new HashMap<>();
    HashSet<String> completedProcesses = new HashSet<>();

    private void printProcessDetails() {
        System.out.println("Current process details in map:");
        for (String processId : processToAllDetails.keySet()) {
            int[] details = processToAllDetails.get(processId);
            System.out.println("Process: " + processId + ", AT: " + details[0] + ", BT: " + details[1]);
        }
    }

    public List<ScheduleResult> runNPSJF(List<Process> processes) {
        System.out.println("Input processes:");
        for (Process p : processes) {
            if (p != null) {
                System.out.println("Process ID: " + p.getprocessId() +
                        ", Arrival Time: " + p.getArrivalTime() +
                        ", Burst Time: " + p.getBurstTime());
            }
        }

        // Clear any previous data
        processToAllDetails.clear();
        completedProcesses.clear();

        // Initialize process details
        for(Process p: processes) {
            if (p != null && p.getprocessId() != null) {
                int[] details = new int[2];
                details[0] = p.getArrivalTime();  // Arrival time
                details[1] = p.getBurstTime();   // Burst time
                processToAllDetails.put(p.getprocessId(), details);
            }
        }

        printProcessDetails();

        // Priority queue for initial process ordering (sorted by arrival time then burst time)
        PriorityQueue<String> initialQueue = new PriorityQueue<>(
                (a, b) -> {
                    int arrivalA = processToAllDetails.get(a)[0];
                    int arrivalB = processToAllDetails.get(b)[0];
                    if (arrivalA != arrivalB) {
                        return Integer.compare(arrivalA, arrivalB);//SORTED BY ARRIVAL TIME
                    } else {
                        return Integer.compare(processToAllDetails.get(a)[1], processToAllDetails.get(b)[1]);//SORTED BY BURST TIME
                    }
                }
        );

        // Priority queue for arrived processes (sorted by burst time)
        PriorityQueue<String> arrivalQueue = new PriorityQueue<>(
                (a, b) -> processToAllDetails.get(a)[1] - processToAllDetails.get(b)[1]
        );

        // Add all processes to initial queue
        for (Process p : processes) {
            if (p != null && p.getprocessId() != null) {
                initialQueue.offer(p.getprocessId());
            }
        }

        List<ScheduleResult> results = new ArrayList<>();
        int currentTime = 0;

        while(!initialQueue.isEmpty() || !arrivalQueue.isEmpty()) {
            // Add all processes that have arrived by current time to arrivalQueue
            while (!initialQueue.isEmpty()) {
                String nextProcess = initialQueue.peek();
                int arrivalTime = processToAllDetails.get(nextProcess)[0];
                if (arrivalTime <= currentTime) {
                    arrivalQueue.offer(initialQueue.poll());
                } else {
                    break;
                }
            }

            // If no processes have arrived yet, fast-forward time to next arrival
            if (arrivalQueue.isEmpty() && !initialQueue.isEmpty()) {
                String nextProcess = initialQueue.peek();
                currentTime = processToAllDetails.get(nextProcess)[0];
                continue;
            }

            // Get the next process to execute (shortest job)
            String currentProcess = arrivalQueue.poll();
            if (currentProcess == null) continue;

            // Skip if already completed (shouldn't happen with proper queue management)
            if (completedProcesses.contains(currentProcess)) {
                continue;
            }

            int[] details = processToAllDetails.get(currentProcess);
            int arrivalTime = details[0];
            int burstTime = details[1];

            // Calculate metrics
            int completionTime = currentTime + burstTime;
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

            results.add(result);

            System.out.println("Processed: " + currentProcess +
                    ", AT: " + arrivalTime +
                    ", CT: " + completionTime +
                    ", TAT: " + turnaroundTime +
                    ", WT: " + waitingTime);
        }

        return results;
    }
}