package org.sopt.nawa_103.Model.DB;

/**
 * Created by yunmi on 2016-01-13.
 */
public class Account {
    String id;
    String pw;

    public Account(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }
}
