package com.prod.sudesi.lotusherbalsnew.Models;

import java.io.Serializable;

public class AttendanceModel implements Serializable{

    String ADate;
    String AttendanceValue;
    String Aid;
    String AbsentType;

    public String getAid() {
        return Aid;
    }

    public void setAid(String aid) {
        Aid = aid;
    }


    public String getADate() {
        return ADate;
    }

    public void setADate(String ADate) {
        this.ADate = ADate;
    }

    public String getAttendanceValue() {
        return AttendanceValue;
    }

    public void setAttendanceValue(String attendanceValue) {
        AttendanceValue = attendanceValue;
    }

    public String getAbsentType() {
        return AbsentType;
    }

    public void setAbsentType(String absentType) {
        AbsentType = absentType;
    }


}
