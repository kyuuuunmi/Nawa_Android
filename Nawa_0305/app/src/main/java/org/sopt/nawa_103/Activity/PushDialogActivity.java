package org.sopt.nawa_103.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.sopt.nawa_103.R;
public class PushDialogActivity extends Activity {

    private PushDialog pushDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_push_dialog);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String location = intent.getStringExtra("location");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String friend = intent.getStringExtra("friend");

        pushDialog = new PushDialog(this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PushDialogActivity.this,PushStorageActivity.class);
                        startActivityForResult(intent, 0);
                        pushDialog.dismiss();
                        finish();
                    }},
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pushDialog.dismiss();
                        finish();
                    }}, title, location, date, time, friend);
        pushDialog.setCancelable(true);
        pushDialog.show();


        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                pushDialog.dismiss();
                finish();
            }
        }, 10000);

    }
}