package com.khoray.cashbook.model;

import com.khoray.cashbook.utils.TimeUtil;
import ohos.data.orm.OrmObject;
import ohos.data.orm.annotation.Entity;
import ohos.data.orm.annotation.Index;
import ohos.data.orm.annotation.PrimaryKey;

@Entity(tableName = "record", ignoredColumns = {"isTitle", "pay", "income"},
        indices = {@Index(value = {"recordId"}, name = "name_index", unique = true)})
public class RecordBean extends OrmObject {
    @PrimaryKey(autoGenerate = true)
    private Integer recordId;
    private int majorType, minorType;
    private long time;
    private double value;
    private String note;
    public boolean isTitle = false;
    public double pay, income;

    public RecordBean() {
    }

    public RecordBean(RecordBean record) {
        this.recordId = record.recordId;
        this.majorType = record.majorType;
        this.minorType = record.minorType;
        this.time = record.time;
        this.value = record.value;
        this.note = record.note;
    }

    public RecordBean(long time) {
        this.time = time;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getMajorType() {
        return majorType;
    }

    public void setMajorType(int majorType) {
        this.majorType = majorType;
    }

    public int getMinorType() {
        return minorType;
    }

    public void setMinorType(int minorType) {
        this.minorType = minorType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "{" + "majorType:" + Integer.toString(majorType) + ",minorType:" + Integer.toString(minorType) + ",time:" + TimeUtil.formatYMDHM(time) + ",value:" + Double.toString(value) + ",note:" + note + "}";
    }
}
