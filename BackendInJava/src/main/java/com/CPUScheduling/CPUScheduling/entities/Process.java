package com.CPUScheduling.CPUScheduling.entities;

public class Process {
    private String processId;
    private int arrivalTime;  // Note the spelling: arrival not arival
    private int burstTime;
    private int timeQuantum;

    // Constructors
    public Process() {}

    public Process(String processId, int arrivalTime, int burstTime) {
        this.processId = processId;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
    }

    // Getters and setters
    public String getprocessId() {
        return processId;
    }

    public void setprocessId(String processId) {
        this.processId = processId;
    }

    public int getArrivalTime() {  // Correct spelling of method
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public int getTimeQuantum() {
        return timeQuantum;
    }

    public void setTimeQuantum(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }
}
