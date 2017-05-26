package org.sopt.nawa_103.Model.Element;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by yunmi on 2015-11-04.
 */
public class ViewHolder {

    TextView textDay_item;
    TextView textDate_item;
    TextView textTodo_item;
    TextView textName_item;
    ImageButton btnMsg_item;

    public ImageButton getBtnMsg_item() {
        return btnMsg_item;
    }

    LinearLayout linear_item;

    public LinearLayout getLinear_item() {
        return linear_item;
    }

    public void setLinear_item(LinearLayout linear_item) {
        this.linear_item = linear_item;
    }



    public TextView getTextName_item() {
        return textName_item;
    }

    public void setTextName_item(TextView textName_item) {
        this.textName_item = textName_item;
    }


    public void setBtnMsg_item(ImageButton btnMsg_item) {
        this.btnMsg_item = btnMsg_item;
    }

    public TextView getTextDate_item() {
        return textDate_item;
    }

    public void setTextDate_item(TextView textDate_item) {
        this.textDate_item = textDate_item;
    }

    public TextView getTextTodo_item() {
        return textTodo_item;
    }

    public void setTextTodo_item(TextView textTodo_item) {
        this.textTodo_item = textTodo_item;
    }

    public TextView getTextDay_item() {
        return textDay_item;
    }

    public void setTextDay_item(TextView textDay_item) {
        this.textDay_item = textDay_item;
    }


}
