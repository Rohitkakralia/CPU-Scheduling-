package com.CPUScheduling.CPUScheduling.services;

import com.CPUScheduling.CPUScheduling.entities.ScheduleResult;
import com.CPUScheduling.CPUScheduling.entities.GantChart;
import com.CPUScheduling.CPUScheduling.entities.Process;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SchedulerService {
    HashMap<String, int[]> processToAllDetails = new HashMap<>();

    private void printProcessDetails() {
        System.out.println("Current process details in map:");
        for (String processId : processToAllDetails.keySet()) {
            int[] details = processToAllDetails.get(processId);
            System.out.println("Process: " + processId + ", AT: " + details[0] + ", BT: " + details[1]);
        }
    }

    public List<ScheduleResult> runFCFS(List<Process> processes) {
        System.out.println("FCFS scheduling started...");

        // Debug: Print all processes and their arrival times first
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

        // Store process details in the map first
        for (Process p : processes) {
            if (p != null && p.getprocessId() != null) {
                int[] details = new int[2];
                details[0] = p.getArrivalTime();  // Arrival time
                details[1] = p.getBurstTime();   // Burst time

                processToAllDetails.put(p.getprocessId(), details);
            }
        }

        printProcessDetails();

        // Create a fresh priority queue with the correct comparator
        PriorityQueue<String> processQueue = new PriorityQueue<>(
                (a, b) -> {
                    int arrivalA = processToAllDetails.get(a)[0];
                    int arrivalB = processToAllDetails.get(b)[0];

                    if (arrivalA != arrivalB) {
                        return Integer.compare(arrivalA, arrivalB);  // Sort by arrival time (ascending)
                    } else {
                        return a.compareTo(b);  // If arrival times are equal, sort by process ID
                    }
                }
        );

        // Add all processes to the queue
        for (Process p : processes) {
            if (p != null && p.getprocessId() != null) {
                processQueue.offer(p.getprocessId());
            }
        }

        // Debug: Print the expected execution order
        System.out.println("Expected execution order:");
        PriorityQueue<String> debugQueue = new PriorityQueue<>(processQueue);
        while (!debugQueue.isEmpty()) {
            String processId = debugQueue.poll();
            int[] details = processToAllDetails.get(processId);
            System.out.println("Process: " + processId + ", AT: " + details[0]);
        }

        // Execute the scheduling algorithm
        List<ScheduleResult> results = new ArrayList<>();
        List<GantChart> ganttChart = new ArrayList<>();
        int currentTime = 0;

        while (!processQueue.isEmpty()) {
            String processId = processQueue.poll();
            int[] details = processToAllDetails.get(processId);

            int arrivalTime = details[0];
            int burstTime = details[1];

            // If process hasn't arrived yet, wait for it (record idle time)
            if (currentTime < arrivalTime) {
                ganttChart.add(new GantChart("IDLE", currentTime, arrivalTime));
                currentTime = arrivalTime;
            }

            int startTime = currentTime;
            int completionTime = currentTime + burstTime;
            int turnaroundTime = completionTime - arrivalTime;
            int waitingTime = turnaroundTime - burstTime;

            // Record process execution in Gantt chart
            ganttChart.add(new GantChart(processId, startTime, completionTime));

            // Update the current time
            currentTime = completionTime;

            // Create and add the result
            ScheduleResult result = new ScheduleResult();
            result.setName(processId);
            result.setArivalTime(arrivalTime);
            result.setBurstTime(burstTime);
            result.setCriticalTime(completionTime);
            result.setTurnAroundTime(turnaroundTime);
            result.setWaitingTime(waitingTime);
            result.setSequence(new ArrayList<>(ganttChart));

            results.add(result);

            System.out.println("Processed: " + processId +
                    ", AT: " + arrivalTime +
                    ", CT: " + completionTime +
                    ", TAT: " + turnaroundTime +
                    ", WT: " + waitingTime);
        }

        // Print Gantt chart
        printGanttChart(ganttChart);

        System.out.println("FCFS scheduling completed.");
        return results;
    }

    private void printGanttChart(List<GantChart> ganttChart) {
        System.out.println("\nGantt Chart:");
        System.out.println("Process\tStart\tEnd");
        for (GantChart entry : ganttChart) {
            System.out.println(entry.processId + "\t" + entry.startTime + "\t" + entry.endTime);
        }
    }
}