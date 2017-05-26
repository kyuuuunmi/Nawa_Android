package org.sopt.nawa_103.Activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.sopt.nawa_103.Adapter.PushStorageAdapter;
import org.sopt.nawa_103.Component.ApplicationController;
import org.sopt.nawa_103.Model.DB.PushSender;
import org.sopt.nawa_103.Model.DB.TodayModel;
import org.sopt.nawa_103.Network.NetworkService;
import org.sopt.nawa_103.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class PushStorageActivity extends Activity {

    NetworkService networkService;

    ArrayList<PushSender> pushList;
    PushStorageAdapter adapter;

    ImageView img_push_logo_today, img_push_map_today, img_push_friend_today, img_push_time_today;
    TextView tvTitle_push;
    TextView tvLoc_push;
    TextView tvPeople_push;
    TextView tvTime_push;
    ListView listView;

    boolean haveSchedule; //일정이 있으면 true, 없으면 false
    String title;
    String loc;
    String people;
    String datetime;

    SharedPreferences preferences;
    int current_p_id=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_storage);
        networkService = ApplicationController.getInstance().getNetworkService();

        preferences = getSharedPreferences("setting", 0);
        current_p_id=preferences.getInt("current_p_id", -1);

        pushList = new ArrayList<>();

        Calendar calendar;
        calendar = Calendar.getInstance();


        Call<List<TodayModel>> TodaysSchedule = networkService.getTodaySchedule(current_p_id, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
        TodaysSchedule.enqueue(new Callback<List<TodayModel>>() {
            @Override
            public void onResponse(Response<List<TodayModel>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    haveSchedule = true;

                    List<TodayModel> todayModel = response.body();
                    Log.i("mytag", "오늘의 약속 size : " + todayModel.size());
                    String friends = "";
                    for(int i=0; i<todayModel.size(); i++) {
                        friends += todayModel.get(i).getName() + " ";
                        Log.i("mytag", "친구리스트 " + i + ": " + friends);
                    }
                    title = todayModel.get(0).getTitle();
                    loc = todayModel.get(0).getPlace();
                    people = friends;
                    datetime = todayModel.get(0).getHour() + ":" + todayModel.get(0).getMinute();

                    Log.i("MyTag", haveSchedule + " " + title + " " + loc + " " + people + " " + datetime);

                } else {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드: " + statusCode);

                    if(statusCode == 503) {
                        haveSchedule = false;
                        Log.i("MyTag", "오늘 약속 없음");
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "에러내용: " + t.getMessage());
            }
        });




        setPushListData();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setPushListData() {

        //푸시 삭제 먼저
        Call<PushSender> DeletePushList = networkService.deletePushList(current_p_id);
        DeletePushList.enqueue(new Callback<PushSender>() {
            @Override
            public void onResponse(Response<PushSender> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    //Toast.makeText(PushStorageActivity.this, "지난 푸시가 삭제되었습니당ㅎ", Toast.LENGTH_SHORT).show();
                    //setPushListData();

                } else {
                    int statusCode = response.code();

                    Log.i("MyTag", "응답코드: " + statusCode + " " + response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "에러내용: " + t.getMessage());
            }
        });



        Call<ArrayList<PushSender>> SenderList = networkService.getPushList(current_p_id);
        SenderList.enqueue(new Callback<ArrayList<PushSender>>() {
            @Override
            public void onResponse(Response<ArrayList<PushSender>> response, Retrofit retrofit) {
                if (response.isSuccess()) {

                    ArrayList<PushSender> tmpList = response.body();
                    for (int i = 0; i < tmpList.size(); i++) {
                        int s_id = tmpList.get(i).getS_id();
                        int push_id = tmpList.get(i).getPush_id();
                        String name = tmpList.get(i).getName();
                        int pmcheck = tmpList.get(i).getPmcheck();

                        pushList.add(new PushSender(s_id, push_id, current_p_id, name, pmcheck));

                        Log.i("MyTag", "푸시리스트 "+  i+ ": " + pushList.get(i).getS_id() + " "
                                + pushList.get(i).getPush_id() + " " + pushList.get(i).getName() + " " + pushList.get(i).getPmcheck());

                    }

                    adapter = new PushStorageAdapter(PushStorageActivity.this, R.layout.list_push_item, pushList);
                    initView();

                } else {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드: " + statusCode);

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "에러내용: " + t.getMessage());
            }
        });



    }

    public void initView() {


        img_push_logo_today = (ImageView)findViewById(R.id.img_push_logo_today);
        img_push_map_today = (ImageView)findViewById(R.id.img_push_map_today);

        img_push_friend_today = (ImageView)findViewById(R.id.img_push_friend_today);
        img_push_time_today = (ImageView)findViewById(R.id.img_push_time_today);
     //   tvTitle_push = (TextView)findViewById(R.id.tvTitle_push_s);
        tvLoc_push = (TextView)findViewById(R.id.tvLoc_today);
        tvPeople_push = (TextView)findViewById(R.id.tvPeople_today);
        tvTime_push = (TextView)findViewById(R.id.tvTime_today);
//        tvTitle_push.setText(title);
        tvLoc_push.setText(loc);
        tvPeople_push.setText(people);
        tvTime_push.setText(datetime);
        listView = (ListView)findViewById(R.id.listViewPush);
        listView.setAdapter(adapter);

        //오늘 일정있는지 여부에 따라 다른 이미지뷰 보이기
        if(haveSchedule) {
            Log.i("mytag", "약속있어유~");
            img_push_logo_today.setVisibility(View.VISIBLE);
            img_push_map_today.setVisibility(View.VISIBLE);
            img_push_friend_today.setVisibility(View.VISIBLE);
            img_push_time_today.setVisibility(View.VISIBLE);
      //      tvTitle_push.setVisibility(View.VISIBLE);
            tvLoc_push.setVisibility(View.VISIBLE);
            tvPeople_push.setVisibility(View.VISIBLE);
            tvTime_push.setVisibility(View.VISIBLE);
        } else {
            Log.i("mytag", "약속없어유~");

            img_push_logo_today.setVisibility(View.GONE);
            img_push_map_today.setVisibility(View.GONE);
            img_push_friend_today.setVisibility(View.GONE);
            img_push_time_today.setVisibility(View.GONE);
      //      tvTitle_push.setVisibility(View.GONE);
            tvLoc_push.setVisibility(View.GONE);
            tvPeople_push.setVisibility(View.GONE);
            tvTime_push.setVisibility(View.GONE);
        }


    }


}