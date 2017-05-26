package org.sopt.nawa_103.Model.DB;

/**
 * Created by jihoon on 2016-01-15.
 */
public class PrivateMemoModel {
    private int m_id;
    private int Location_in_calendar;
    private String memo;

    public int getLocation_in_calendar() {
        return Location_in_calendar;
    }

    public void setLocation_in_calendar(int location_in_calendar) {
        this.Location_in_calendar = location_in_calendar;
    }

    public PrivateMemoModel(int m_id, int date){
        this.m_id = m_id;
        this.Location_in_calendar = date;
    }

    public PrivateMemoModel(int m_id, String memo) {
        this.m_id = m_id;
        this.memo = memo;
    }

    public int getM_id() {
        return m_id;
    }

    public void setM_id(int m_id) {
        this.m_id = m_id;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
