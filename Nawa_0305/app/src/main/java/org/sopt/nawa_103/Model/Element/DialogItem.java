package org.sopt.nawa_103.Model.Element;

/**
 * Created by yunmi on 2016-01-11.
 */
public class DialogItem {
    String name_dialog;
    int p_id;

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public DialogItem(String name_dialog, int p_id) {
        this.name_dialog = name_dialog;
        this.p_id = p_id;
    }


    public String getName_dialog() {
        return name_dialog;
    }

    public void setName_dialog(String name_dialog) {
        this.name_dialog = name_dialog;
    }

}
