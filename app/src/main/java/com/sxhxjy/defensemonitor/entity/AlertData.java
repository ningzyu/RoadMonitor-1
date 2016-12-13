package com.sxhxjy.defensemonitor.entity;

import java.io.Serializable;

/**
 * 2016/9/26
 *
 * @author Michael Zhao
 */
public class AlertData implements Serializable{

    /**
     * alarmContent : 设备诊断异常
     * confirmInfo : 确认人:乔景安;确认内容:已确认;确认时间:2016-11-08 13:58:57
     * confirmMsg : 已确认
     * confirmTime : 1478584737000
     * etime : 1477713600000
     * generationReason : null
     * id : 4028770e57e5bf140157e620a2ca0004
     * level : 二级
     * levelId : 4028812c57a344a30157a374eb000005
     * num : 1
     * stationName : 传感器:BD-2-表面位移200米
     * stime : 1477713600000
     * type : 传感器
     * typeId : 4028812c57a344a30157a376908c0009
     * userId : 4028770e57fafbe60157faff4cdd0000
     */

    private String alarmContent;
    private String confirmInfo;
    private String confirmMsg;
    private long confirmTime;
    private String etime;
    private String generationReason;
    private String id;
    private String level;
    private String levelId;
    private String num;
    private String stationName;
    private String stime;
    private String type;
    private String typeId;
    private String userId;
    public long generationTime;

    public String getAlarmContent() {
        return alarmContent;
    }

    public void setAlarmContent(String alarmContent) {
        this.alarmContent = alarmContent;
    }

    public String getConfirmInfo() {
        return confirmInfo;
    }

    public void setConfirmInfo(String confirmInfo) {
        this.confirmInfo = confirmInfo;
    }

    public String getConfirmMsg() {
        return confirmMsg;
    }

    public void setConfirmMsg(String confirmMsg) {
        this.confirmMsg = confirmMsg;
    }

    public long getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(long confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getEtime() {
        return etime;
    }

    public void setEtime(String etime) {
        this.etime = etime;
    }

    public String getGenerationReason() {
        return generationReason;
    }

    public void setGenerationReason(String generationReason) {
        this.generationReason = generationReason;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
