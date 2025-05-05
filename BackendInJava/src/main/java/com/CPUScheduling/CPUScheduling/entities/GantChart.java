package com.CPUScheduling.CPUScheduling.entities;

public class GantChart {
    public String processId;
    public int startTime;
    public int endTime;

    public GantChart(String processId, int startTime, int endTime) {
        this.processId = processId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
