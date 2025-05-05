package com.CPUScheduling.CPUScheduling.entities;

import java.util.List;

public class ScheduleResult {
    private int criticalTime;
    private int arivalTime;
    private int burstTime;
    private String name;
    private int turnAroundTime;
    private int waitingTime;
    private List<GantChart> sequence;

    public List<GantChart> getSequence() {
        return sequence;
    }

    public void setSequence(List<GantChart> sequence) {
        this.sequence = sequence;
    }

    public int getCriticalTime() {
        return criticalTime;
    }

    public void setCriticalTime(int criticalTime) {
        this.criticalTime = criticalTime;
    }

    public int getArivalTime() {
        return arivalTime;
    }

    public void setArivalTime(int arivalTime) {
        this.arivalTime = arivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTurnAroundTime() {
        return turnAroundTime;
    }

    public void setTurnAroundTime(int turnAroundTime) {
        this.turnAroundTime = turnAroundTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }
}
