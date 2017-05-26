package org.sopt.nawa_103.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.sopt.nawa_103.R;


public class MsgDialogActivity extends Activity {

    private MsgDialog msgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_msg_dialog);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        Intent intent = getIntent();
        String sender = intent.getStringExtra("sender");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String message = intent.getStringExtra("message");

        msgDialog = new MsgDialog(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MsgDialogActivity.this, MainActivity.class);
                        startActivityForResult(intent, 0);
                        msgDialog.dismiss();
                        finish();
                    }},
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgDialog.dismiss();
                        finish();
                    }}, sender, date, time, message);
        msgDialog.setCancelable(false);
        msgDialog.show();


        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                msgDialog.dismiss();
                finish();
            }
        }, 10000);

    }
}