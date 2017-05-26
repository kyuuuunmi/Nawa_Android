package org.sopt.nawa_103.Model.DB;

import java.util.List;

/**
 * Created by jihoon on 2016-01-15.
 */
public class ScheduleContent {
    public int p_id;
    public int s_id;
    public String title;
    public String place;
    public String place_address;
    public String place_tel;
    public int year;
    public int month;
    public int date;
    public int hour;
    public int minute;
    public List<Friend> friend;
    public double place_latitude;
    public double place_longitude;
    public String amorpm;
    public String totaldate;

}
