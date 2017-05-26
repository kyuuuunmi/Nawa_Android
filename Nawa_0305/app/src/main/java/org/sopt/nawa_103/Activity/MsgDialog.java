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
public class MsgDialog extends Dialog {

    String sender;
    String date;
    String time;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //????
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        //

        setContentView(R.layout.msg_dialog);

        setLayout();
        setClickListener(yesClickListener, noClickListener);
    }

    public MsgDialog(Context context,
                     View.OnClickListener leftListener, View.OnClickListener rightListener,
                     String sender, String date, String time, String message) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        this.yesClickListener = leftListener;
        this.noClickListener = rightListener;
        this.sender = sender;
        this.date = date;
        this.time = time;
        this.message = message;
    }

    private void setClickListener(View.OnClickListener left , View.OnClickListener right){
        if(left!=null && right!=null){
            btnYes.setOnClickListener(left);
            btnNo.setOnClickListener(right);
        }else if(left!=null && right==null){
            btnYes.setOnClickListener(left);
        }else {

        }
    }

    private TextView tvFrom_msg;
    private TextView tvDate_msg;
    private TextView tvTime_msg;
    private TextView tvMsg_msg;
    private ImageView btnYes;
    private ImageView btnNo;

    private ImageView.OnClickListener yesClickListener;
    private ImageView.OnClickListener noClickListener;

    /*
     * Layout
     */
    private void setLayout(){
        tvFrom_msg = (TextView)findViewById(R.id.tvFrom_msg);
        tvDate_msg = (TextView)findViewById(R.id.tvDate_msg);
        tvTime_msg = (TextView)findViewById(R.id.tvTime_msg);
        tvMsg_msg = (TextView)findViewById(R.id.tvMsg_msg);
        btnYes= (ImageView)findViewById(R.id.btnYes);
        btnNo = (ImageView)findViewById(R.id.btnNo);

        tvFrom_msg.setText(sender);
        tvDate_msg.setText(date);
        tvTime_msg.setText(time);
        tvMsg_msg.setText(message);
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