package com.CPUScheduling.CPUScheduling.services;

import com.CPUScheduling.CPUScheduling.entities.ScheduleResult;
import com.CPUScheduling.CPUScheduling.entities.Process;
import com.CPUScheduling.CPUScheduling.entities.GantChart;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SchedulerServiceForPSJF {

    private static class ProcessState {
        int arrivalTime;
        int burstTime;
        int remainingTime;
        int startTime = -1;

        ProcessState(int arrivalTime, int burstTime) {
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.remainingTime = burstTime;
        }
    }

    public List<ScheduleResult> runPSJF(List<Process> processes) {
        // Validate input
        if (processes == null || processes.isEmpty()) {
            return new ArrayList<>();
        }

        // Create process state map
        Map<String, ProcessState> processMap = new HashMap<>();
        for (Process p : processes) {
            if (p != null && p.getprocessId() != null) {
                processMap.put(p.getprocessId(),
                        new ProcessState(p.getArrivalTime(), p.getBurstTime()));
            }
        }

        // Priority queue sorted by remaining time (then arrival time for tie-breaker)
        PriorityQueue<String> readyQueue = new PriorityQueue<>((a, b) -> {
            int cmp = Integer.compare(processMap.get(a).remainingTime, processMap.get(b).remainingTime);
            if (cmp == 0) {
                return Integer.compare(processMap.get(a).arrivalTime, processMap.get(b).arrivalTime);
            }
            return cmp;
        });

        List<ScheduleResult> results = new ArrayList<>();
        List<GantChart> ganttChart = new ArrayList<>();
        int currentTime = 0;
        String currentProcess = null;
        int totalProcesses = processMap.size();
        int lastProcessChangeTime = 0;

        System.out.println("Starting PSJF Scheduling:");
        printProcessDetails(processMap);

        while (results.size() < totalProcesses) {
            // Add newly arrived processes to ready queue
            for (Map.Entry<String, ProcessState> entry : processMap.entrySet()) {
                String pid = entry.getKey();
                ProcessState ps = entry.getValue();

                if (ps.arrivalTime == currentTime) {
                    readyQueue.add(pid);
                    System.out.println("Time " + currentTime + ": Process " + pid + " arrived");
                }
            }

            // Check if we need to preempt current process
            if (!readyQueue.isEmpty()) {
                String shortestJob = readyQueue.peek();
                ProcessState shortestState = processMap.get(shortestJob);
                ProcessState currentState = currentProcess != null ? processMap.get(currentProcess) : null;

                if (currentProcess == null ||
                        (currentState != null && shortestState.remainingTime < currentState.remainingTime)) {

                    // Record the execution of the previous process in Gantt chart
                    if (currentProcess != null) {
                        ganttChart.add(new GantChart(currentProcess, lastProcessChangeTime, currentTime));
                        readyQueue.add(currentProcess);
                        System.out.println("Time " + currentTime + ": Preempting " + currentProcess +
                                " for " + shortestJob);
                    }

                    // Update the current process and record the start time
                    currentProcess = shortestJob;
                    readyQueue.remove(shortestJob);
                    lastProcessChangeTime = currentTime;

                    // Mark start time if this is first execution
                    if (processMap.get(currentProcess).startTime == -1) {
                        processMap.get(currentProcess).startTime = currentTime;
                    }
                }
            }

            // Execute current process for 1 time unit
            if (currentProcess != null) {
                ProcessState ps = processMap.get(currentProcess);
                ps.remainingTime--;
                System.out.println("Time " + currentTime + ": Executing " + currentProcess +
                        " (remaining: " + ps.remainingTime + ")");

                // Check if process completed
                if (ps.remainingTime == 0) {
                    int completionTime = currentTime + 1;
                    int turnaroundTime = completionTime - ps.arrivalTime;
                    int waitingTime = turnaroundTime - ps.burstTime;

                    // Add the final execution segment to Gantt chart
                    ganttChart.add(new GantChart(currentProcess, lastProcessChangeTime, completionTime));

                    ScheduleResult result = new ScheduleResult();
                    result.setName(currentProcess);
                    result.setArivalTime(ps.arrivalTime);
                    result.setBurstTime(ps.burstTime);
                    result.setCriticalTime(completionTime);
                    result.setTurnAroundTime(turnaroundTime);
                    result.setWaitingTime(waitingTime);
                    results.add(result);

                    System.out.println("Time " + completionTime + ": Process " + currentProcess +
                            " completed (CT: " + completionTime +
                            ", TAT: " + turnaroundTime +
                            ", WT: " + waitingTime + ")");

                    currentProcess = null;
                    lastProcessChangeTime = completionTime;
                }
            } else {
                System.out.println("Time " + currentTime + ": CPU idle");
            }

            currentTime++;
        }

        // Assign Gantt chart to each result
        for (ScheduleResult result : results) {
            result.setSequence(new ArrayList<>(ganttChart));
        }

        // Calculate and print averages
        printAverages(results);
        printGanttChart(ganttChart);

        return results;
    }

    private void printProcessDetails(Map<String, ProcessState> processMap) {
        System.out.println("Process Details:");
        System.out.println("PID\tAT\tBT");
        for (Map.Entry<String, ProcessState> entry : processMap.entrySet()) {
            ProcessState ps = entry.getValue();
            System.out.println(entry.getKey() + "\t" + ps.arrivalTime + "\t" + ps.burstTime);
        }
    }

    private void printAverages(List<ScheduleResult> results) {
        if (results.isEmpty()) return;

        double avgTAT = results.stream().mapToInt(ScheduleResult::getTurnAroundTime).average().orElse(0);
        double avgWT = results.stream().mapToInt(ScheduleResult::getWaitingTime).average().orElse(0);

        System.out.println("\nAverage Turnaround Time: " + String.format("%.2f", avgTAT));
        System.out.println("Average Waiting Time: " + String.format("%.2f", avgWT));
    }

    private void printGanttChart(List<GantChart> ganttChart) {
        System.out.println("\nGantt Chart:");
        System.out.println("Process\tStart\tEnd");
        for (GantChart entry : ganttChart) {
            System.out.println(entry.processId + "\t" + entry.startTime + "\t" + entry.endTime);
        }
    }
}