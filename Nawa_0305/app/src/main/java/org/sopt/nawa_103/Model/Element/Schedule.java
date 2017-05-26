package org.sopt.nawa_103.Model.Element;

/**
 * Created by kimhj on 2016-01-14.
 */
public class Schedule {
    int s_id;
    String from;
    String title;
    String loc;
    String people;
    String time;

    public Schedule(String from, String title, String loc, String people, String time) {
        this.from = from;
        this.title = title;
        this.loc = loc;
        this.people = people;
        this.time = time;
    }

    public int getS_id() {
        return s_id;
    }

    public void setS_id(int s_id) {
        this.s_id = s_id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
