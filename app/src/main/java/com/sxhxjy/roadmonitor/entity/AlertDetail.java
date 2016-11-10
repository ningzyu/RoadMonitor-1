package com.sxhxjy.roadmonitor.entity;

/**
 * 2016/11/10
 *
 * @author Michael Zhao
 */

public class AlertDetail {
    /**
     * alarmContent : 设备诊断异常
     * generationTime : 1477713600000
     * id : 188823
     * stationName : 表面位移200米
     */

    private String alarmContent;
    private long generationTime;
    private String id;
    private String stationName;

    public String getAlarmContent() {
        return alarmContent;
    }

    public void setAlarmContent(String alarmContent) {
        this.alarmContent = alarmContent;
    }

    public long getGenerationTime() {
        return generationTime;
    }

    public void setGenerationTime(long generationTime) {
        this.generationTime = generationTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
}
