package org.sopt.nawa_103.Model.DB;

/**
 * Created by jihoon on 2016-01-15.
 */
public class Friend {
    public String phone;
    public String name;
    public int p_id;

    public Friend(String name, String phone){
        this.name=name;
        this.phone=phone;

    }
    public Friend(String name, String phone, int p_id){
        this.name=name; this.phone=phone; this.p_id=p_id;
    }
}
