package org.sopt.nawa_103.Model.DB;

/**
 * Created by jiyeon on 2016-02-24.
 */
public class UnderScheduleContent {
    public int p_id;
    public int s_id;
    public String title;
    public String place;
    public int year;
    public int month;
    public int date;
    public int hour;
    public int minute;
    public String friends;
    public String amorpm;
    public UnderScheduleContent(int p_id, int s_id, String title, String friends, String place, int year, int month, int date, int hour, int minute, String amorpm) {
        this.p_id = p_id;
        this.s_id = s_id;
        this.title = title;
        this.friends=friends;
        this.place = place;
        this.year = year;
        this.month = month;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.amorpm=amorpm;
    }

}
