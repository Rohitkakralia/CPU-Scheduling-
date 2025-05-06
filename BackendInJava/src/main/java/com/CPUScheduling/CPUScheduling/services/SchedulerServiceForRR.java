//package com.CPUScheduling.CPUScheduling.services;
//
//import com.CPUScheduling.CPUScheduling.entities.GantChart;
//import com.CPUScheduling.CPUScheduling.entities.Process;
//import com.CPUScheduling.CPUScheduling.entities.ScheduleResult;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class SchedulerServiceForRR {
//    private static class ProcessState {
//        int arrivalTime;
//        int burstTime;
//        int remainingTime;
//        int startTime = -1;
//
//        ProcessState(int arrivalTime, int burstTime, int remainingTime) {
//            this.arrivalTime = arrivalTime;
//            this.burstTime = burstTime;
//            this.remainingTime = remainingTime;
//        }
//    }
//
//    HashMap<String, ProcessState> processToDetails = new HashMap<>();
//    HashSet<String> remainingProcessesSet = new HashSet<>();
//
//
//    public List<ScheduleResult> runRR(List<Process> processes) {
//        processToDetails.clear();
//
//        for(Process process: processes){
//            if(process != null && process.getprocessId() != null){
//                ProcessState ps = new ProcessState(process.getArrivalTime(), process.getBurstTime(), process.getBurstTime());
//                remainingProcessesSet.add(process.getprocessId());
//                processToDetails.put(process.getprocessId(), ps);
//            }
//        }
//
//        PriorityQueue<String> topMostProcess = new PriorityQueue<>(
//                (a, b) -> {
//                    int arrivalA = processToDetails.get(a).arrivalTime;
//                    int arrivalB = processToDetails.get(b).arrivalTime;
//
//                    if (arrivalA != arrivalB) {
//                        return Integer.compare(arrivalA, arrivalB);  // Sort by arrival time (ascending)
//                    } else {
//                        return a.compareTo(b);  // If arrival times are equal, sort by process ID
//                    }
//                }
//        );
//
//        for (Process p : processes) {
//            if (p != null && p.getprocessId() != null) {
//                topMostProcess.offer(p.getprocessId());
//            }
//        }
//
//        List<ScheduleResult> ans = new ArrayList<>();
//        int currentTime = 0;
//        int timeQuantum = 4;
//        List<GantChart> ganttChart = new ArrayList<>();
//
//        while(!topMostProcess.isEmpty()){
//            String currentProcess = topMostProcess.poll();
//            int arrivalTime = processToDetails.get(currentProcess).arrivalTime;
//            int burstTime = processToDetails.get(currentProcess).burstTime;
//            int startTime = currentTime;
//            int remainingTime = processToDetails.get(currentProcess).remainingTime - timeQuantum;
//
//
//            if(remainingTime <= 0){
//                remainingProcessesSet.remove(currentProcess);
//            }
//
//            if(remainingTime <= 0){
//                int completionTime = currentTime + burstTime;
//                int TAT = completionTime - arrivalTime;
//                int WT = TAT - burstTime;
//
//                ScheduleResult result = new ScheduleResult();
//                result.setName(currentProcess);
//                result.setArivalTime(arrivalTime);
//                result.setBurstTime(burstTime);
//                result.setCriticalTime(completionTime);
//                result.setTurnAroundTime(TAT);
//                result.setWaitingTime(WT);
//
//                ans.add(result);
//            }else{
//                processToDetails.put(currentProcess, new ProcessState(arrivalTime, burstTime, remainingTime));
//            }
//
//            currentTime += processToDetails.get(currentProcess).remainingTime >= timeQuantum ? timeQuantum : processToDetails.get(currentProcess).remainingTime;
//
//            if(remainingTime <= 0){
//                processToDetails.remove(currentProcess);
//            }
//
//            int endTime = currentTime;
//
//            ganttChart.add(new GantChart(currentProcess, startTime, endTime));
//
//
//            if(remainingProcessesSet.size() <= 0 && topMostProcess.isEmpty()) break;
//            else if(!remainingProcessesSet.isEmpty() && topMostProcess.isEmpty()) {
//                // Add all remaining processes back to the queue
//                topMostProcess.addAll(remainingProcessesSet);
//            }
//
//        }
//        return ans;
//    }
//}

package com.CPUScheduling.CPUScheduling.services;

import com.CPUScheduling.CPUScheduling.entities.GantChart;
import com.CPUScheduling.CPUScheduling.entities.Process;
import com.CPUScheduling.CPUScheduling.entities.ScheduleResult;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SchedulerServiceForRR {
    private static class ProcessState {
        int arrivalTime;
        int burstTime;
        int remainingTime;
        int startTime = -1;

        ProcessState(int arrivalTime, int burstTime, int remainingTime) {
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.remainingTime = remainingTime;
        }
    }

    public List<ScheduleResult> runRR(List<Process> processes) {
        // Reset data structures
        HashMap<String, ProcessState> processToDetails = new HashMap<>();

        // Sort processes by arrival time for initial queuing
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        // Initialize process states
        for (Process process : processes) {
            if (process != null && process.getprocessId() != null) {
                ProcessState ps = new ProcessState(
                        process.getArrivalTime(),
                        process.getBurstTime(),
                        process.getBurstTime()
                );
                processToDetails.put(process.getprocessId(), ps);
            }
        }

        int timeQuantum = 4;
        int currentTime = 0;
        List<ScheduleResult> results = new ArrayList<>();
        List<GantChart> ganttChart = new ArrayList<>();

        // Queue for ready processes
        Queue<String> readyQueue = new LinkedList<>();

        // Keep track of arrived and unfinished processes
        Set<String> arrivedProcesses = new HashSet<>();
        Set<String> completedProcesses = new HashSet<>();

        // Continue until all processes are completed
        while (completedProcesses.size() < processes.size()) {
            // Check for new arrivals at current time
            for (Process p : processes) {
                String pid = p.getprocessId();
                if (p.getArrivalTime() <= currentTime && !arrivedProcesses.contains(pid)) {
                    readyQueue.add(pid);
                    arrivedProcesses.add(pid);
                }
            }

            // If nothing in ready queue but processes remain, advance time to next arrival
            if (readyQueue.isEmpty() && completedProcesses.size() < processes.size()) {
                int nextArrival = Integer.MAX_VALUE;
                for (Process p : processes) {
                    if (!arrivedProcesses.contains(p.getprocessId()) && p.getArrivalTime() < nextArrival) {
                        nextArrival = p.getArrivalTime();
                    }
                }

                // Add idle time to Gantt chart if there's a gap
                if (currentTime < nextArrival) {
                    ganttChart.add(new GantChart("Idle", currentTime, nextArrival));
                }

                currentTime = nextArrival;
                continue;
            }

            // Process the next ready process
            if (!readyQueue.isEmpty()) {
                String currentProcess = readyQueue.poll();
                ProcessState ps = processToDetails.get(currentProcess);

                // Record start time for this burst
                int startTime = currentTime;

                // Calculate execution time for this quantum
                int executionTime = Math.min(timeQuantum, ps.remainingTime);

                // Update current time and remaining time
                currentTime += executionTime;
                ps.remainingTime -= executionTime;

                // Add to Gantt chart
                ganttChart.add(new GantChart(currentProcess, startTime, currentTime));

                // Check for newly arrived processes during this execution
                for (Process p : processes) {
                    String pid = p.getprocessId();
                    if (p.getArrivalTime() > startTime && p.getArrivalTime() <= currentTime &&
                            !arrivedProcesses.contains(pid)) {
                        readyQueue.add(pid);
                        arrivedProcesses.add(pid);
                    }
                }

                // If process is completed
                if (ps.remainingTime <= 0) {
                    completedProcesses.add(currentProcess);

                    Process originalProcess = null;
                    for (Process p : processes) {
                        if (p.getprocessId().equals(currentProcess)) {
                            originalProcess = p;
                            break;
                        }
                    }

                    if (originalProcess != null) {
                        int completionTime = currentTime;
                        int arrivalTime = originalProcess.getArrivalTime();
                        int burstTime = originalProcess.getBurstTime();
                        int turnaroundTime = completionTime - arrivalTime;
                        int waitingTime = turnaroundTime - burstTime;

                        ScheduleResult result = new ScheduleResult();
                        result.setName(currentProcess);
                        result.setArivalTime(arrivalTime);
                        result.setBurstTime(burstTime);
                        result.setCriticalTime(completionTime);
                        result.setTurnAroundTime(turnaroundTime);
                        result.setWaitingTime(waitingTime);

                        results.add(result);
                    }
                }
                // If process still has remaining time, add it back to ready queue
                else {
                    readyQueue.add(currentProcess);
                }
            }
        }

        // Create a special ScheduleResult object to hold the Gantt chart data
        for (ScheduleResult result : results) {
            result.setSequence(new ArrayList<>(ganttChart));
        }

        return results;
    }
}