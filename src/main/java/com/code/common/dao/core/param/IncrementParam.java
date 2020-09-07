package com.code.common.dao.core.param;

import java.io.Serializable;
import java.util.Date;

public class IncrementParam implements Serializable {
    private String columnName;
    private String lastUpdateStr; // YYYYMMDDHH24MISS
    private Date lastUpdate;
    private long lastSequenceLong;
    private int lastSequenceInt;

    public enum IncrementType {
        STR,
        TS,
        SEQUENCE_LONG,
        SEQUENCE_INT
    }

    IncrementType incrementType;

    public IncrementParam(String cn, String lu) {
        this.columnName = cn;
        this.lastUpdateStr = lu;
        incrementType = IncrementType.STR;
    }

    public IncrementParam(String cn, Date lu) {
        this.columnName = cn;
        this.lastUpdate = lu;
        incrementType = IncrementType.TS;
    }

    public IncrementParam(String cn, long ls) {
        this.columnName = cn;
        this.lastSequenceLong = ls;
        incrementType = IncrementType.SEQUENCE_LONG;
    }

    public IncrementParam(String cn, int ls) {
        this.columnName = cn;
        this.lastSequenceInt = ls;
        incrementType = IncrementType.SEQUENCE_INT;
    }

    public String getColumnName() {
        return columnName;
    }

    public IncrementType getIncrementType() {
        return incrementType;
    }

    public long getLastSequenceLong() {
        return lastSequenceLong;
    }

    public int getLastSequenceInt() {
        return lastSequenceInt;
    }

    public String getLastUpdateStr() {
        return lastUpdateStr;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }
}
