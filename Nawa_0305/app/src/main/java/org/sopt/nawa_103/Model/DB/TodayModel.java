package org.sopt.nawa_103.Model.DB;

/**
 * Created by jiyeon on 2016-02-22.
 */
public class TodayModel {

    //오늘의 약속

    int p_id;
    String title;
    String place;
    String name;
    String hour;
    String minute;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }
}