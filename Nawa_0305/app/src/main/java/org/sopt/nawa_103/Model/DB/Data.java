package org.sopt.nawa_103.Model.DB;

/**
 * Created by yunmi on 2016-01-12.
 */
public class Data {
    String name;
    String todo;

    int year;
    int month;
    int date;

    int current_s_id;
    int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCurrent_s_id() {
        return current_s_id;
    }

    public void setCurrent_s_id(int current_s_id) {
        this.current_s_id = current_s_id;
    }

    public long getYear() {
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

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public Data(String name, String todo, int year, int month, int date, int current_s_id) {

        this.name = name;
        this.todo = todo;
        this.year = year;
        this.month = month;
        this.date = date;
        this.current_s_id = current_s_id;
    }


    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public Data() {
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
