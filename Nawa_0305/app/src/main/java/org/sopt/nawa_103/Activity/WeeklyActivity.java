package org.sopt.nawa_103.Activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.sopt.nawa_103.Adapter.WeeklyAdapter;
import org.sopt.nawa_103.Component.ApplicationController;
import org.sopt.nawa_103.Model.DB.Data;
import org.sopt.nawa_103.Model.DB.Friend;
import org.sopt.nawa_103.Model.DB.ScheduleContent;
import org.sopt.nawa_103.Model.DB.ScheduleTmp;
import org.sopt.nawa_103.Model.Element.ItemData;
import org.sopt.nawa_103.Network.NetworkService;
import org.sopt.nawa_103.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class WeeklyActivity extends AppCompatActivity {

    private NetworkService networkService;
    private ArrayList<ItemData> itemDatas = null;
    ListView listView;
    WeeklyAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout list_item;
    LinearLayout imageButton;


    ArrayList<Friend> arrayList;
    ArrayList<Data> datas = new ArrayList<>();
    int year;
    int month;
    int date;
    int day;
    int current_p_id = 0;
    String friends = "";
    String title = "";


    int storage_size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly);
        initNetworkService();
        SharedPreferences setting = getSharedPreferences("setting", 0);
        current_p_id = setting.getInt("current_p_id", 1);

        init();
        makeList();
        aboutView();
    }

    private void initNetworkService() {
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    private void aboutView() {
        // 날짜, 요일 설정

        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);


        //ArrayList<Data> datas2 = findTodo(year, month);
        findTodo(year, month, date);

        //  Toast.makeText(getApplicationContext(), year + "년" + month + "월" + day + "일", Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();

        // swipe 했을 때 행동 정의
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (month == 0) {
                    month = 12;
                    year -= 1;
                }

                //ArrayList<Data> datas1 = findTodo(year, month);


                findTodo(year, month - 1, date);
                //setCalendar(year, month - 1, date, 1);
                //        Toast.makeText(getApplicationContext(), year + "년" + month + "월" + day + "일", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                listView.setSelection(28);
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    private void findTodo(final int year, final int month, final int date) {
        Call<List<ScheduleContent>> SchedulesInMonthCall = networkService.getDateScheduleContent(current_p_id, year, month + 1);
        SchedulesInMonthCall.enqueue(new Callback<List<ScheduleContent>>() {
            @Override
            public void onResponse(Response<List<ScheduleContent>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<ScheduleContent> SchedulesInMonth = response.body();
                    final int size = SchedulesInMonth.size();
                    Log.i("MyTagsize", "" + size);

                    if(size==0) setCalendar(year, month, date, 1);

                    else {
                        for (int i = 0; i < size; i++) {
                            final int tmpInt = i;
                          /*int year = SchedulesInMonth.get(i).year;
                          int month = SchedulesInMonth.get(i).month;
                          int date = SchedulesInMonth.get(i).date;*/
                            int current_s_id = SchedulesInMonth.get(i).s_id;

                            Call<List<ScheduleTmp>> ScheduleInDateCall = networkService.getInfoScheduleContent(current_s_id);

                            ScheduleInDateCall.enqueue(new Callback<List<ScheduleTmp>>() {
                                @Override
                                public void onResponse(Response<List<ScheduleTmp>> response, Retrofit retrofit) {
                                    if (response.isSuccess()) {
                                        List<ScheduleTmp> tmps = response.body();
                                        friends = "";
                                        arrayList = new ArrayList<Friend>();
                                        for (int j = 0; j < tmps.size(); j++) {
                                            arrayList.add(new Friend(tmps.get(j).name, tmps.get(j).phone, tmps.get(j).p_id));
                                            friends += tmps.get(j).name + " ";
                                        }
                                        //title = tmps.get(0).title;

                                        datas.add(new Data(friends, tmps.get(0).title, tmps.get(0).year, tmps.get(0).month, tmps.get(0).date, tmps.get(0).s_id));
                                        Log.i("MyDateReturn", "" + datas.size());

                                    } else {
                                        int statusCode = response.code();
                                        Log.i("MyTag", "응답코드 : " + statusCode);
                                    }


                                    if (tmpInt == size - 1)
                                        setCalendar(year, month, date, 1);

                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                                    Log.i("MyTag", "에러내용 : " + t.getMessage());
                                }
                            });
                            //   Log.i("MyDateReturn11", "" + datas.get(0).getDate());
                        }
                    }
                    //
                } else {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드 : " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to load this month schedule", Toast.LENGTH_LONG).show();
                Log.i("MyTag", "에러내용 : " + t.getMessage());
            }
        });
        Log.i("MyDateReturn2222222", "" + datas.size());
        //Log.i("MyDateReturn22", "" + datas.get(0).getDate());


    }

    private void setCalendar(int syear, int smonth, int sdate, int position) {
        // 달력날짜 맞추기
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, syear);
        cal.set(Calendar.MONTH, smonth);


        day = cal.get(Calendar.DAY_OF_WEEK);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        String dayStr = "";


        for (int j = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
             j <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); j++) {
            cal.set(Calendar.DATE, j);
            switch (cal.get(Calendar.DAY_OF_WEEK)) {

                case 1:
                    dayStr = "SUN";
                    if (sdate - j < 7 && sdate - j >= 0)
                        listView.setSelection(j - 1);
                    break;
                case 2:
                    dayStr = "MON";
                    break;
                case 3:
                    dayStr = "TUE";
                    break;
                case 4:
                    dayStr = "WED";
                    break;
                case 5:
                    dayStr = "THU";
                    break;
                case 6:
                    dayStr = "FRI";
                    break;
                case 7:
                    dayStr = "SAT";
                    break;

            }
            // TODO: 2016-01-13  DB에서 받아온 Data 입력
            /*
            * if( DBData != null) ~~
            * ItemData 생성자 하나 더 필요한듯
            *
            *
            * */


            Log.i("MyDatasSize", "" + datas.size() + " " + storage_size);

            if (storage_size == datas.size())
                itemDatas.add(j - 1, new ItemData(dayStr, year, month, j, new Data("", "", year, month, j, 0), arrayList));
            else {
                for (int k = storage_size; k < datas.size(); k++) {

                    if (j == datas.get(k).getDate() ) {
                        //itemDatas.add(j - 1, new ItemData(dayStr, year, month, j, datas.get(k), arrayList));
                        itemDatas.add(j - 1, new ItemData(dayStr, year, datas.get(k).getMonth(), j, datas.get(k), arrayList));

                        //storage_size++;
                        if(j == cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                            storage_size=datas.size();
                        break;
                    } else if (k == datas.size() - 1) {
                        itemDatas.add(j - 1, new ItemData(dayStr, year, month, j, new Data("", "", year, month, j, 0 ), arrayList));
                        if(j == cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                            storage_size=datas.size();
                        break;
                    }

                }

                adapter.notifyDataSetChanged();

            }
        }


    }

    private void makeList() {
        adapter = new WeeklyAdapter(itemDatas, getApplicationContext(), this);
        listView.setAdapter(adapter);
    }

    private void init() {
        itemDatas = new ArrayList<ItemData>();
        listView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        list_item = (LinearLayout) findViewById(R.id.list_item);
        imageButton = (LinearLayout)findViewById(R.id.weekly_toolbar_btnBack);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
