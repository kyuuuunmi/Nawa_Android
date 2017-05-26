package org.sopt.nawa_103.Model.DB;

/**
 * Created by jihoon on 2016-01-15.
 */
public class PrivateCalendarModel {
    private int s_id;
    private String title;
    private int location_in_calendar;
    private int date;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PrivateCalendarModel(int s_id, int location_in_calendar, int date) {
        this.s_id = s_id;
        this.location_in_calendar = location_in_calendar;
        this.date=date;
    }

    public PrivateCalendarModel(int s_id, String title) {
        this.s_id = s_id;
        this.title = title;
    }

    public int getS_id() {
        return s_id;
    }

    public void setS_id(int s_id) {
        this.s_id = s_id;
    }

    public int getLocation_in_calendar() {
        return location_in_calendar;
    }

    public void setLocation_in_calendar(int location_in_calendar) {
        this.location_in_calendar = location_in_calendar;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
