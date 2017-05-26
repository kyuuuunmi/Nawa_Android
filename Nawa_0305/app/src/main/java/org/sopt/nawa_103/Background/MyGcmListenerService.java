package org.sopt.nawa_103.Background;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.sopt.nawa_103.Activity.MainActivity;
import org.sopt.nawa_103.Activity.MsgDialogActivity;
import org.sopt.nawa_103.Activity.PushDialogActivity;
import org.sopt.nawa_103.R;

/**
 * Created by jihoon on 2016-01-15.
 */public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {

        Log.d(TAG, "title: " + data.getString("title")); //일정 제목
        Log.d(TAG, "message: " + data.getString("message")); //notification text
        Log.d(TAG, "key0: " + data.getString("key0")); //푸시, 쪽지 구분
        Log.d(TAG, "key1: " + data.getString("key1")); //장소(푸시) or 보낸이(쪽지)
        Log.d(TAG, "key2: " + data.getString("key2")); //날짜
        Log.d(TAG, "key3: " + data.getString("key3")); //시간
        Log.d(TAG, "key4: " + data.getString("key4")); //참여자 리스트(푸시) or 쪽지 내용(쪽지)

        String title = data.getString("title");
        String message = data.getString("message");

        // GCM으로 받은 메세지를 디바이스에 알려주는 sendNotification()을 호출한다.
        sendNotification(title, message);

        //0이면 푸시, 1이면 쪽지
        String pushtype = data.getString("key0");

        Intent intent;
        if(pushtype.equals("0")) { //푸시
            intent = new Intent(getApplicationContext(), PushDialogActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("title", data.getString("title"));
            intent.putExtra("location", data.getString("key1"));
            intent.putExtra("date", data.getString("key2"));
            intent.putExtra("time", data.getString("key3"));
            intent.putExtra("friend", data.getString("key4"));
            startActivity(intent);
        } else if(pushtype.equals("1")) { //쪽지
            intent = new Intent(getApplicationContext(), MsgDialogActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("sender", data.getString("key1"));
            intent.putExtra("date", data.getString("key2"));
            intent.putExtra("time", data.getString("key3"));
            intent.putExtra("message", data.getString("key4"));
            startActivity(intent);
        }


    }


    /**
     * 실제 디바에스에 GCM으로부터 받은 메세지를 알려주는 함수이다. 디바이스 Notification Center에 나타난다.
     * @param title
     * @param message
     */
    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri uri = Uri.parse("android.resource://" + getPackageName() +"/" + R.raw.noti_sound);
        //Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(uri);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}