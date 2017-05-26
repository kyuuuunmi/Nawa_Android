package org.sopt.nawa_103.Activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.sopt.nawa_103.R;

/**
 * Created by kimhj on 2016-01-15.
 */
public class PushDialog extends Dialog {

    String title;
    String location;
    String date;
    String time;
    String friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //????
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        //

        setContentView(R.layout.push_dialog);

        setLayout();
        setClickListener(yesClickListener, noClickListener);
    }

    public PushDialog(Context context,
                      View.OnClickListener leftListener, View.OnClickListener rightListener,
                      String title, String location, String date, String time, String friend) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.yesClickListener = leftListener;
        this.noClickListener = rightListener;
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.friend = friend;
    }

    private void setClickListener(View.OnClickListener left, View.OnClickListener right) {
        if (left != null && right != null) {
            btnYes.setOnClickListener(left);
            btnNo.setOnClickListener(right);
        } else if (left != null && right == null) {
            btnYes.setOnClickListener(left);
        } else {

        }
    }

    private TextView tvTitle_push;
    private TextView tvLoc_push;
    private TextView tvPeople_push;
    private TextView tvTime_push;
    private ImageView btnYes;
    private ImageView btnNo;

    private ImageView.OnClickListener yesClickListener;
    private ImageView.OnClickListener noClickListener;

    /*
     * Layout
     */
    private void setLayout() {
        tvTitle_push = (TextView) findViewById(R.id.tvTitle_push);
        tvLoc_push = (TextView) findViewById(R.id.tvLoc_push);
        tvPeople_push = (TextView) findViewById(R.id.tvPeople_push);
        tvTime_push = (TextView) findViewById(R.id.tvTime_push);
        btnYes = (ImageView) findViewById(R.id.btnYes);
        btnNo = (ImageView) findViewById(R.id.btnNo);

        tvTitle_push.setText(title);
        tvLoc_push.setText(location);
        tvPeople_push.setText(friend);
        tvTime_push.setText(date + " " + time);
        btnYes.setBackground(null);
        btnNo.setBackground(null);

        btnYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnYes.setImageResource(R.mipmap.dialog_btn_yes_click);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                }
                return false;
            }
        });
        btnNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnNo.setImageResource(R.mipmap.dialog_btn_no_click);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btnNo.setImageResource(R.mipmap.dialog_btn_no_unclick);
                }
                return false;
            }
        });
    }
}