package org.sopt.nawa_103.Model.Element;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.sopt.nawa_103.Activity.CalendarActivity;
import org.sopt.nawa_103.R;

/**
 * Created by jeongjisu on 2016. 1. 14..
 */
public class AlarmReceiver extends BroadcastReceiver {
    String todo, time;
    int id=0;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extra = intent.getExtras();
        if(extra!=null){
            todo = extra.getString("todo", "약속 이름");
            time = extra.getString("time", "시간");
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.mipmap.logo);
            builder.setTicker("약속이 1시간 남았습니다");
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, CalendarActivity.class), 0);
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            builder.setContentTitle(todo);
            builder.setContentText(time);
            nm.notify(id, builder.build());
        }
        else{
            Toast.makeText(context, "HEY", Toast.LENGTH_SHORT).show();
        }
    }
}
