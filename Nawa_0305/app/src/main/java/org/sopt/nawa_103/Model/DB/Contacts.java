package org.sopt.nawa_103.Model.DB;

/**
 * Created by USER on 2016-01-11.
 */
public class Contacts {
    private String name;
    private boolean isChecked;

    public Contacts(String name) {
        this.name = name;
        this.isChecked = false;
    }


    public String getName() {
        return name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
