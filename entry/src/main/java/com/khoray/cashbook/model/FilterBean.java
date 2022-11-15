package com.khoray.cashbook.model;

import com.khoray.cashbook.utils.TimeUtil;
import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmPredicates;

import java.sql.Time;

public class FilterBean {
    public int lowValue, highValue, majorType, minorType;
    public long startTime, endTime;
    public String note;

    public FilterBean() {
        lowValue = -1;
        highValue = -1;
        majorType = -1;
        minorType = -1;
        startTime = -1;
        endTime = -1;
        note = "";
    }

    public FilterBean(FilterBean fb) {
        this.lowValue = fb.lowValue;
        this.highValue = fb.highValue;
        this.majorType = fb.majorType;
        this.minorType = fb.minorType;
        this.startTime = fb.startTime;
        this.endTime = fb.endTime;
        this.note = fb.note;
    }

    public static FilterBean getDayFilter() {
        FilterBean fb = new FilterBean();
        long time = System.currentTimeMillis();
        time = time - time % Const.dayMilllis;
        fb.startTime = time;
        fb.endTime = time + Const.dayMilllis;
        return fb;
    }

    public static FilterBean getWeekFilter() {
        FilterBean fb = new FilterBean();
        long time = System.currentTimeMillis();
        time = time - time % Const.dayMilllis - 6 * Const.dayMilllis;
        fb.startTime = time;
        fb.endTime = time + Const.dayMilllis * 7;
        return fb;
    }
    public static FilterBean getMonthFilter() {
        FilterBean fb = new FilterBean();
        long time = System.currentTimeMillis();
        time = time - time % Const.dayMilllis - 30 * Const.dayMilllis;
        fb.startTime = time;
        fb.endTime = time + Const.dayMilllis * 31;
        return fb;
    }
    public static FilterBean getYearFilter() {
        FilterBean fb = new FilterBean();
        long time = System.currentTimeMillis();
        time = time - time % Const.dayMilllis - 365 * Const.dayMilllis;
        fb.startTime = time;
        fb.endTime = time + Const.dayMilllis * 366;
        return fb;
    }

    public OrmPredicates generatePredicates(OrmContext context) {
        OrmPredicates predicates = context.where(RecordBean.class);
        predicates.greaterThanOrEqualTo("time", 0);
        if(startTime != -1) {
            predicates.and().greaterThanOrEqualTo("time", startTime);
        }
        if(endTime != -1) {
            predicates.and().lessThan("time", endTime);
        }
        if(majorType != -1) {
            predicates.and().equalTo("majorType", majorType);
            if(minorType != -1) {
                predicates.and().equalTo("minorType", minorType);
            }
        }
        if(lowValue != -1) {
            predicates.and().greaterThanOrEqualTo("value", lowValue);
        }
        if(highValue != -1) {
            predicates.and().lessThanOrEqualTo("value", highValue);
        }
        if(!note.equals("")) {
            predicates.and().contains("note", note);
        }
        return predicates;
    }

    @Override
    public String toString() {
        return "{sT:" + startTime + ",eT:" + endTime + ",major/minorType:[" + majorType + ", " + minorType + "],lowRange:[" + lowValue + ","
 + highValue + "],note:\"" + note + "\"}";    }
}
