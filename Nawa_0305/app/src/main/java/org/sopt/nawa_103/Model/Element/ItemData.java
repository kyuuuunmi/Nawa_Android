package org.sopt.nawa_103.Model.Element;

import org.sopt.nawa_103.Model.DB.Data;
import org.sopt.nawa_103.Model.DB.Friend;

import java.util.List;

/**
 * Created by yunmi on 2015-11-04.
 */
public class ItemData {

    String day;
    int year;
    int month;
    int date;
    Data data;
    List<Friend> friendList;

    public List<Friend> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<Friend> friendList) {
        this.friendList = friendList;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public ItemData(String day, int year, int month, int date, Data data, List<Friend> friendList) {
        this.day = day;
        this.year = year;
        this.month = month;
        this.date = date;
        this.data = data;
        this.friendList=friendList;
    }


    public Object getData() {
        return data;
    }


    public void setDay(String day) {
        this.day = day;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getDay() {

        return day;
    }

    public int getDate() {
        return date;
    }


    public void setData(Data data) {
        this.data = data;
    }

}
