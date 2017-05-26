package org.sopt.nawa_103.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.sopt.nawa_103.Adapter.ViewPagerAdapter;
import org.sopt.nawa_103.Component.ApplicationController;
import org.sopt.nawa_103.Model.DB.Friend;
import org.sopt.nawa_103.Model.DB.MemoContent;
import org.sopt.nawa_103.Model.DB.PrivateCalendarModel;
import org.sopt.nawa_103.Model.DB.PrivateMemoModel;
import org.sopt.nawa_103.Model.DB.ScheduleContent;
import org.sopt.nawa_103.Model.DB.ScheduleTmp;
import org.sopt.nawa_103.Model.DB.UnderScheduleContent;
import org.sopt.nawa_103.Model.Element.AlarmReceiver;
import org.sopt.nawa_103.Network.NetworkService;
import org.sopt.nawa_103.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class CalendarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    int current_s_id = 0;
    int current_p_id =0;
    int current_m_id = 0;

    ViewPagerAdapter adapter;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    MemoContent current_MemoCentent;
    List<ScheduleTmp> current_scheduletmp_list;
    ArrayList<UnderScheduleContent> under_schedulecontent_list=new ArrayList<>();

    private boolean bool_memo_was_exist = false;
    private NetworkService networkService;
    AutoCompleteTextView autoCompleteTextView;
    Intent intent;
    AlarmManager alarmManager;
    FrameLayout frameLayout;
    AppBarLayout appBarLayout;
    GridLayout gridLayout;
    TextView calendar_textview_0, calendar_textview_1, calendar_textview_2, calendar_textview_3, calendar_textview_4, calendar_textview_5, calendar_textview_6, calendar_textview_7, calendar_textview_8, calendar_textview_9, calendar_textview_10,
            calendar_textview_11, calendar_textview_12, calendar_textview_13, calendar_textview_14, calendar_textview_15, calendar_textview_16, calendar_textview_17, calendar_textview_18, calendar_textview_19, calendar_textview_20,
            calendar_textview_21, calendar_textview_22, calendar_textview_23, calendar_textview_24, calendar_textview_25, calendar_textview_26, calendar_textview_27, calendar_textview_28, calendar_textview_29, calendar_textview_30,
            calendar_textview_31, calendar_textview_32, calendar_textview_33, calendar_textview_34, calendar_textview_35, calendar_textview_36, calendar_textview_37, calendar_textview_38, calendar_textview_39, calendar_textview_40, calendar_textview_41;
    ArrayList<TextView> textview_array;
    Calendar calendar;
    public Calendar selected_day_calendar;
    ImageView drawer_calendar_btn, drawer_message_btn, drawer_setting_btn, drawer_mailbox_btn, imagebtn_alarm_setting;
    ImageButton imagebtn_add;
    ImageView bLastMonth, bNextMonth, under_icon_delete, under_icon_edit;
    TextView under_tv_todo, under_tv_location, under_tv_friend, under_tv_time;
    ViewPager pager;
    Toolbar toolbar;

    public static int SUNDAY = 1;
    public static int MONDAY = 2;
    public static int TUESDAY = 3;
    public static int WEDNSESDAY = 4;
    public static int THURSDAY = 5;
    public static int FRIDAY = 6;
    public static int SATURDAY = 7;

    private TextView mTvCalendarTitle;
    int calendar_index = 0, calendar_index_2 = 0;
    ArrayList<PrivateMemoModel> private_memo_model_list;
    ArrayList<PrivateCalendarModel> private_calendar_model_list;
    ArrayList<PrivateCalendarModel> private_calendar_model_list_include_title;
    ArrayList<Integer> private_s_id_list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        preferences = getSharedPreferences("setting", 0);
        current_p_id=preferences.getInt("current_p_id", -1);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setbtncontroller();
        initNetworkService();

        initView();
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        initCalendar(calendar);
        setListener();
        long thismonth = ((long) Calendar.getInstance().get(Calendar.MONTH) + 1);
        long thisyear = ((long) Calendar.getInstance().get(Calendar.YEAR));
        private_memo_model_list = new ArrayList<PrivateMemoModel>();
        private_calendar_model_list = new ArrayList<PrivateCalendarModel>();
        private_calendar_model_list_include_title = new ArrayList<PrivateCalendarModel>();
        initDB(thismonth, thisyear);
        setautocompeletetextview();
    }
    private void setautocompeletetextview() {
        final int[] size = {0};
        Call<List<ScheduleContent>> SchedulesCall = networkService.searchScheduleContent(current_p_id);
        SchedulesCall.enqueue(new Callback<List<ScheduleContent>>() {
            @Override
            public void onResponse(Response<List<ScheduleContent>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    private_calendar_model_list_include_title.clear();
                    List<ScheduleContent> allschedules = response.body();
                    for (int i = 0; i < allschedules.size(); i++) {
                        PrivateCalendarModel model = new PrivateCalendarModel(allschedules.get(i).s_id, allschedules.get(i).title);
                        private_calendar_model_list_include_title.add(model);
                    }
                    size[0] = private_calendar_model_list_include_title.size();
                    final String[] all_schedules_title_list = new String[size[0]];
                    for (int i = 0; i < size[0]; i++) {
                        all_schedules_title_list[i] = private_calendar_model_list_include_title.get(i).getTitle();
                    }
                    ArrayAdapter<String> stringArrayAdapter;
                    stringArrayAdapter = new ArrayAdapter<String>(CalendarActivity.this, android.R.layout.simple_dropdown_item_1line, all_schedules_title_list);
                    autoCompleteTextView.setAdapter(stringArrayAdapter);

                    autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        int s_id = 0;

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            for (int i = 0; i < size[0]; i++) {
                                if (all_schedules_title_list[i].toString().equals(autoCompleteTextView.getText().toString())) {
                                    s_id = private_calendar_model_list_include_title.get(i).getS_id();
                                }
                            }
                            Call<List<ScheduleTmp>> InfoScheduleCall = networkService.getInfoScheduleContent(s_id);
                            Log.d("MyTag", String.valueOf(position) + " " + String.valueOf(all_schedules_title_list.length));
                            InfoScheduleCall.enqueue(new Callback<List<ScheduleTmp>>() {
                                @Override
                                public void onResponse(Response<List<ScheduleTmp>> response, Retrofit retrofit) {
                                    if (response.isSuccess()) {
                                        List<ScheduleTmp> scheduleTmps = response.body();
                                        current_scheduletmp_list = scheduleTmps;
                                        int month = scheduleTmps.get(0).month - 1;
                                        int year = scheduleTmps.get(0).year;

                                        selected_day_calendar.set(year, month, 1);
                                        calendar.set(year, month, 1);
                                        initCalendar(calendar);
                                        initDB((long) calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
                                        setListener();
                                        switch (calendar.get(Calendar.MONTH)) {
                                            case 0:
                                                appBarLayout.setBackgroundResource(R.drawable.month1);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#37464e"));
                                                imagebtn_add.setImageResource(R.mipmap.calendar_icon_add);
                                                imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm);
                                                bLastMonth.setImageResource(R.mipmap.calendar_left);
                                                bNextMonth.setImageResource(R.mipmap.calendar_right);
                                                toolbar.setNavigationIcon(R.mipmap.drawer_icon_open);
                                                break;
                                            case 1:
                                                appBarLayout.setBackgroundResource(R.drawable.month2);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                                                imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                                                bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                                                bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                                                toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                                                break;
                                            case 2:
                                                appBarLayout.setBackgroundResource(R.drawable.month3);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#37464e"));
                                                imagebtn_add.setImageResource(R.mipmap.calendar_icon_add);
                                                imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm);
                                                bLastMonth.setImageResource(R.mipmap.calendar_left);
                                                bNextMonth.setImageResource(R.mipmap.calendar_right);
                                                toolbar.setNavigationIcon(R.mipmap.drawer_icon_open);
                                                break;
                                            case 3:
                                                appBarLayout.setBackgroundResource(R.drawable.month4);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                                                imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                                                bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                                                bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                                                toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                                                break;
                                            case 4:
                                                appBarLayout.setBackgroundResource(R.drawable.month5);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#37464e"));
                                                imagebtn_add.setImageResource(R.mipmap.calendar_icon_add);
                                                imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm);
                                                bLastMonth.setImageResource(R.mipmap.calendar_left);
                                                bNextMonth.setImageResource(R.mipmap.calendar_right);
                                                toolbar.setNavigationIcon(R.mipmap.drawer_icon_open);
                                                break;
                                            case 5:
                                                appBarLayout.setBackgroundResource(R.drawable.month6);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                                                imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                                                bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                                                bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                                                toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                                                break;
                                            case 6:
                                                appBarLayout.setBackgroundResource(R.drawable.month7);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                                                imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                                                bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                                                bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                                                toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                                                break;
                                            case 7:
                                                appBarLayout.setBackgroundResource(R.drawable.month8);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                                                imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                                                bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                                                bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                                                toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                                                break;
                                            case 8:
                                                appBarLayout.setBackgroundResource(R.drawable.month9);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                                                imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                                                bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                                                bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                                                toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                                                break;
                                            case 9:
                                                appBarLayout.setBackgroundResource(R.drawable.month10);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                                                imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                                                bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                                                bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                                                toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                                                break;
                                            case 10:
                                                appBarLayout.setBackgroundResource(R.drawable.month11);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                                                imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                                                bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                                                bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                                                toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                                                break;
                                            case 11:
                                                appBarLayout.setBackgroundResource(R.drawable.month12);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                                                imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                                                bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                                                bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                                                toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                                                break;
                                        }
                                        ArrayList<Friend> tmpFriend = new ArrayList<Friend>();
                                        String friends = "";
                                        for (int i = 0; i < scheduleTmps.size(); i++) {
                                            if (current_scheduletmp_list.get(i).p_id != current_p_id) {
                                                tmpFriend.add(new Friend(scheduleTmps.get(i).name, scheduleTmps.get(i).phone));
                                                friends += scheduleTmps.get(i).name + " ";
                                            }
                                        }
                                        //setUnderTextView(scheduleTmps.get(0).title, scheduleTmps.get(0).place, friends, String.valueOf(scheduleTmps.get(0).hour + ":" + scheduleTmps.get(0).minute+ scheduleTmps.get(0).amorpm));
                                        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        mInputMethodManager.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
                                    } else {
                                        int statusCode = response.code();
                                        Log.i("MyTag", "응답코드 2: " + statusCode);
                                        //setUnderViewNull();
                                    }
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    Log.i("MyTag", "에러내용 2: " + t.getMessage());
                                    // setUnderViewNull();
                                }
                            });
                        }
                    });
                } else {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드 1: " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "에러내용 1: " + t.getMessage());
            }
        });

    }
    /*private void setautocompeletetextview(String title) {
        int size = 0;
        Call<List<ScheduleContent>> SchedulesCall = networkService.searchScheduleContent(current_p_id);
        SchedulesCall.enqueue(new Callback<List<ScheduleContent>>() {
            @Override
            public void onResponse(Response<List<ScheduleContent>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    private_calendar_model_list_include_title.clear();
                    List<ScheduleContent> allschedules = response.body();
                    int size = allschedules.size();
                    for (int i = 0; i < allschedules.size(); i++) {
                        PrivateCalendarModel model = new PrivateCalendarModel(allschedules.get(i).s_id, allschedules.get(i).title);
                        private_calendar_model_list_include_title.add(model);
                    }
                    size = private_calendar_model_list_include_title.size();
                } else {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드 1: " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "에러내용 1: " + t.getMessage());
            }
        });
        String[] all_schedules_title_list = new String[size];
        for (int i = 0; i < size; i++) {
            all_schedules_title_list[i] = private_calendar_model_list_include_title.get(i).getTitle();
        }
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, all_schedules_title_list));
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
                final long s_id = private_calendar_model_list_include_title.get(position).getS_id();
                Call<List<ScheduleTmp>> InfoScheduleCall = networkService.getInfoScheduleContent(s_id);
                InfoScheduleCall.enqueue(new Callback<List<ScheduleTmp>>() {
                    @Override
                    public void onResponse(Response<List<ScheduleTmp>> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            List<ScheduleTmp> scheduleTmps = response.body();
                            current_scheduletmp_list=scheduleTmps;
                            int month = scheduleTmps.get(0).month - 1;
                            int year = scheduleTmps.get(0).year;

                            selected_day_calendar.set(year, month, 1);
                            calendar.set(year, month, 1);
                            initCalendar(calendar);
                            initDB((long) calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.YEAR));
                            setListener();
                            switch (calendar.get(Calendar.MONTH)) {
                                case 1:
                                                appBarLayout.setBackgroundResource(R.drawable.month2);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                break;
                                            case 2:
                                                appBarLayout.setBackgroundResource(R.drawable.month3);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#37464e"));
                                                break;
                                            case 3:
                                                appBarLayout.setBackgroundResource(R.drawable.month4);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                break;
                                            case 4:
                                                appBarLayout.setBackgroundResource(R.drawable.month5);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#37464e"));
                                                break;
                                            case 5:
                                                appBarLayout.setBackgroundResource(R.drawable.month6);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                break;
                                            case 6:
                                                appBarLayout.setBackgroundResource(R.drawable.month7);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                break;
                                            case 7:
                                                appBarLayout.setBackgroundResource(R.drawable.month8);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                break;
                                            case 8:
                                                appBarLayout.setBackgroundResource(R.drawable.month9);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                break;
                                            case 9:
                                                appBarLayout.setBackgroundResource(R.drawable.month10);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                break;
                                            case 10:
                                                appBarLayout.setBackgroundResource(R.drawable.month11);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                break;
                                            case 11:
                                                appBarLayout.setBackgroundResource(R.drawable.month12);
                                                mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                                                break;
                            }
                            ArrayList<Friend> tmpFriend = new ArrayList<Friend>();
                            String friends="";
                            for(int i=0;i<scheduleTmps.size();i++){
                                tmpFriend.add(new Friend(scheduleTmps.get(i).name, scheduleTmps.get(i).phone));
                                friends+=scheduleTmps.get(i).name+" ";
                            }
                            setUnderTextView(scheduleTmps.get(0).title, scheduleTmps.get(0).place, friends, String.valueOf(scheduleTmps.get(0).hour+":"+String.valueOf(scheduleTmps.get(0).minute)));
                        } else {
                            int statusCode = response.code();
                            Log.i("MyTag", "응답코드 2: " + statusCode);
                            setUnderViewNull();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                        Log.i("MyTag", "에러내용 2: " + t.getMessage());
                        setUnderViewNull();
                    }
                });
            }
        });
    }
*/
    private void initNetworkService() {
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    public void initDB(final long thismonth, final long thisyear) {
        for(int i=0;i<42;i++){
            textview_array.get(i).setBackground(null);
        }
        //setUnderViewNull();
        private_memo_model_list.clear();
        private_calendar_model_list.clear();
        under_schedulecontent_list.clear();
        Call<List<MemoContent>> MemoInMonthCall = networkService.getDateMemoContent(current_p_id, thisyear, thismonth);
        MemoInMonthCall.enqueue(new Callback<List<MemoContent>>() {
            @Override
            public void onResponse(Response<List<MemoContent>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<MemoContent> MemosInMonth = response.body();
                    int size = MemosInMonth.size();

                    for (int i = 0; i < size; i++) {
                        PrivateMemoModel memoModel = new PrivateMemoModel(MemosInMonth.get(i).m_id, calendar_index + MemosInMonth.get(i).date - 1);
                        private_memo_model_list.add(memoModel);
                        textview_array.get(calendar_index + MemosInMonth.get(i).date - 1).setBackgroundResource(R.mipmap.calender_sth_withmemo);
                    }
                } else {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드 3: " + statusCode);
                }
            }

            @Override
            public void onFailure(Throwable t) {

                Toast.makeText(getApplicationContext(), "Failed to load this month schedule", Toast.LENGTH_LONG).show();
                Log.i("MyTag", "에러내용 3: " + t.getMessage());
            }
        });

        Call<List<ScheduleContent>> SchedulesInMonthCall = networkService.getDateScheduleContent(current_p_id, thisyear, thismonth);
        SchedulesInMonthCall.enqueue(new Callback<List<ScheduleContent>>() {
            @Override
            public void onResponse(Response<List<ScheduleContent>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<ScheduleContent> SchedulesInMonth = response.body();
                    int size = SchedulesInMonth.size();
                    private_s_id_list.clear();
                    under_schedulecontent_list.clear();

                    for (int i = 0; i < SchedulesInMonth.size(); i++) {

                        PrivateCalendarModel model = new PrivateCalendarModel(SchedulesInMonth.get(i).s_id, calendar_index + SchedulesInMonth.get(i).date - 1, SchedulesInMonth.get(i).date);
                        private_calendar_model_list.add(model);
                    }

                    for (int i = 0; i < SchedulesInMonth.size(); i++) {
                        for (int j = 0; j < private_calendar_model_list.size(); j++) {
                            if (private_calendar_model_list.get(j).getDate() == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                                private_s_id_list.add(private_calendar_model_list.get(j).getS_id());
                            } else {
                                if(calendar_index + SchedulesInMonth.get(i).date>=0) {
                                    textview_array.get(calendar_index + SchedulesInMonth.get(i).date - 1).setBackground(null);
                                    textview_array.get(calendar_index + SchedulesInMonth.get(i).date - 1).setBackgroundResource(R.mipmap.calender_sth);
                                }
                                adapter.setUnder_schedulecontent_list(under_schedulecontent_list);
                                adapter.notifyDataSetChanged();
                                pager.setAdapter(adapter);

                            }
                        }
                    }

                    if (private_s_id_list != null) {
                        for (int k = 0; k < private_s_id_list.size(); k++) {
                            current_s_id = private_s_id_list.get(k);
                            Call<List<ScheduleTmp>> InfoScheduleContent = networkService.getInfoScheduleContent(current_s_id);
                            InfoScheduleContent.enqueue(new Callback<List<ScheduleTmp>>() {
                                UnderScheduleContent tmpUnderScheduleContent;

                                @Override
                                public void onResponse(Response<List<ScheduleTmp>> response, Retrofit retrofit) {
                                    if (response.isSuccess()) {
                                        current_scheduletmp_list = response.body();
                                        String friends = "";
                                        ArrayList<Friend> tmpFriend = new ArrayList<Friend>();

                                        for (int i = 0; i < current_scheduletmp_list.size(); i++) {
                                            if (current_scheduletmp_list.get(i).p_id != current_p_id) {
                                                tmpFriend.add(new Friend(current_scheduletmp_list.get(i).name, current_scheduletmp_list.get(i).phone));
                                                friends += current_scheduletmp_list.get(i).name + " ";
                                            }
                                        }

                                        tmpUnderScheduleContent = new UnderScheduleContent(current_p_id, current_scheduletmp_list.get(0).s_id, current_scheduletmp_list.get(0).title, friends, current_scheduletmp_list.get(0).place,
                                                current_scheduletmp_list.get(0).year, current_scheduletmp_list.get(0).month, current_scheduletmp_list.get(0).date, current_scheduletmp_list.get(0).hour, current_scheduletmp_list.get(0).minute, current_scheduletmp_list.get(0).amorpm);


                                        under_schedulecontent_list.add(tmpUnderScheduleContent);

                                        adapter.setUnder_schedulecontent_list(under_schedulecontent_list);
                                        Log.i("initDBadapteradapter", Integer.toString(under_schedulecontent_list.size()));
                                        adapter.notifyDataSetChanged();
                                        pager.setAdapter(adapter);
                                    } else {
                                        int statusCode = response.code();
                                        Log.i("MyTag", "응답코드 5: " + statusCode);
                                        //setUnderViewNull();
                                    }
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                                    Log.i("MyTag", "에러내용 5: " + t.getMessage());
                                    //setUnderViewNull();
                                }
                            });
                        }
                    } else {

                    }
                } else {
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드 4: " + statusCode);
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to load this month schedule", Toast.LENGTH_LONG).show();
                Log.i("MyTag", "에러내용 4: " + t.getMessage());
            }
        });
        if(Calendar.getInstance().get(Calendar.MONTH)==thismonth-1 && Calendar.getInstance().get(Calendar.YEAR)==thisyear) {
            textview_array.get(calendar_index+Calendar.getInstance().get(Calendar.DAY_OF_MONTH)-1).setBackground(null);
            textview_array.get(calendar_index+Calendar.getInstance().get(Calendar.DAY_OF_MONTH)-1).setBackgroundResource(R.mipmap.calendar_today);
        }
    }

    private void setbtncontroller() {
        drawer_mailbox_btn = (ImageView) findViewById(R.id.drawer_btn_mailbox);
        drawer_calendar_btn = (ImageView) findViewById(R.id.drawer_btn_calendar);
        drawer_message_btn = (ImageView) findViewById(R.id.drawer_btn_message);
        drawer_setting_btn = (ImageView) findViewById(R.id.drawer_btn_setting);
        drawer_mailbox_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(CalendarActivity.this, PushStorageActivity.class);
                startActivity(intent);
            }
        });
        drawer_calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   Toast.makeText(CalendarActivity.this, "calendar button selected", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        drawer_message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(CalendarActivity.this, WeeklyActivity.class);
                startActivity(intent);
            }
        });
        drawer_setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                Intent i = new Intent(CalendarActivity.this, SettingActivity.class);
                startActivityForResult(i, 5);
            }
        });

    }

    private void setAlarm() {
        // TODO: 2016. 1. 14. selected_day_calendar day 시, 분 일정 시간으로 설정
        alarmManager = (AlarmManager) CalendarActivity.this.getSystemService(ALARM_SERVICE);
        intent = new Intent(CalendarActivity.this, AlarmReceiver.class);
        intent.putExtra("todo", under_tv_todo.getText());
        intent.putExtra("time", under_tv_time.getText());
        PendingIntent pender = PendingIntent.getBroadcast(CalendarActivity.this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        selected_day_calendar.set(current_scheduletmp_list.get(0).year, current_scheduletmp_list.get(0).month - 1, current_scheduletmp_list.get(0).date, current_scheduletmp_list.get(0).hour, current_scheduletmp_list.get(0).minute);
        alarmManager.set(AlarmManager.RTC_WAKEUP, selected_day_calendar.getTimeInMillis(), pender);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor2 = sharedPrefs.edit();
        if(resultCode == 0){
            if(sharedPrefs.getBoolean("prefAutoLogin", true)){//자동로그인 설정 여부 확인
                editor.putBoolean("autoCheck", true);
                Toast.makeText(getApplicationContext(), "자동 로그인 체크", Toast.LENGTH_SHORT).show();
            }else {
                editor.putBoolean("autoCheck", false);
                Toast.makeText(getApplicationContext(), "자동 로그인 해제", Toast.LENGTH_SHORT).show();
            }
            editor.commit();
        }else if(resultCode == 9){//설정 화면에서 로그아웃 눌렀을 때
            editor.putBoolean("autoCheck", false);
            editor2.putBoolean("prefAutoLogin", false);
            editor.commit();
            editor2.commit();
            finish();
        }
        else{ //back버튼으로 돌아오면 requestCode=5, resultCode=0
            Log.i("MyTag", "CalendarActivity 돌아와라!!!!!!!!!!!!");

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 이번달 의 캘린더 인스턴스를 생성한다.
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        initCalendar(calendar);

        preferences = getSharedPreferences("setting", 0);
        editor = preferences.edit();
        current_p_id = preferences.getInt("current_p_id", -1);

        pager.setAdapter(adapter);
    }

    private Calendar getLastMonth(Calendar calendar) {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, -1);
        return calendar;
    }

    private Calendar getNextMonth(Calendar calendar) {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, +1);
        return calendar;
    }

    public void setListener() {
        int index = 0;
        final boolean check = false;
        int i;
        for (i = 0; i < 42; i++) {
            index++;
            if (i < calendar_index) {
                textview_array.get(i).setOnClickListener(null);
                continue;
            } else if (i >= calendar_index + calendar_index_2) {
                textview_array.get(i).setOnClickListener(null);
                continue;
            }
            final int finalIndex = index;
            final int finalI = i;

            textview_array.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bool_memo_was_exist = false;
                    under_schedulecontent_list.clear();
                    private_s_id_list.clear();

                    for (int j = 0; j < private_calendar_model_list.size(); j++) {
                        if (private_calendar_model_list.get(j).getLocation_in_calendar() == finalI) {

                            private_s_id_list.add(private_calendar_model_list.get(j).getS_id());
                        } else {

                            adapter.setUnder_schedulecontent_list(under_schedulecontent_list);
                            adapter.notifyDataSetChanged();
                            pager.setAdapter(adapter);

                        }
                    }
                    under_schedulecontent_list.clear();
                    if (private_s_id_list != null) {
                        for (int k = 0; k < private_s_id_list.size(); k++) {
                            current_s_id = private_s_id_list.get(k);
                            Call<List<ScheduleTmp>> InfoScheduleContent = networkService.getInfoScheduleContent(current_s_id);
                            InfoScheduleContent.enqueue(new Callback<List<ScheduleTmp>>() {
                                UnderScheduleContent tmpUnderScheduleContent;

                                @Override
                                public void onResponse(Response<List<ScheduleTmp>> response, Retrofit retrofit) {
                                    if (response.isSuccess()) {
                                        current_scheduletmp_list = response.body();
                                        String friends = "";
                                        ArrayList<Friend> tmpFriend = new ArrayList<Friend>();
                                        for (int i = 0; i < current_scheduletmp_list.size(); i++) {
                                            if (current_scheduletmp_list.get(i).p_id != current_p_id) {
                                                tmpFriend.add(new Friend(current_scheduletmp_list.get(i).name, current_scheduletmp_list.get(i).phone));
                                                friends += current_scheduletmp_list.get(i).name + " ";
                                            }
                                        }

                                        tmpUnderScheduleContent = new UnderScheduleContent(current_p_id, current_scheduletmp_list.get(0).s_id, current_scheduletmp_list.get(0).title, friends, current_scheduletmp_list.get(0).place,
                                                current_scheduletmp_list.get(0).year, current_scheduletmp_list.get(0).month, current_scheduletmp_list.get(0).date, current_scheduletmp_list.get(0).hour, current_scheduletmp_list.get(0).minute, current_scheduletmp_list.get(0).amorpm);


                                        under_schedulecontent_list.add(tmpUnderScheduleContent);

                                        adapter.setUnder_schedulecontent_list(under_schedulecontent_list);
                                        adapter.notifyDataSetChanged();
                                        pager.setAdapter(adapter);

                                    } else {
                                        int statusCode = response.code();
                                        Log.i("MyTag", "응답코드 5: " + statusCode);
                                    }
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                                    Log.i("MyTag", "에러내용 5: " + t.getMessage());
                                    //setUnderViewNull();
                                }
                            });

                        }
                    }
                    String temp = String.valueOf(selected_day_calendar.get(Calendar.YEAR)) + String.valueOf(selected_day_calendar.get(Calendar.MONTH)) + String.valueOf(finalIndex - calendar_index);
                    int m_id = Integer.parseInt(temp);


                    //memo 검색
                    /*
                    for (int j = 0; j < private_memo_model_list.size(); j++) {
                        if (private_memo_model_list.get(j).getLocation_in_calendar() == finalI) {
                            current_m_id = private_memo_model_list.get(j).getM_id();

                            Call<MemoContent> InfoMemoContent = networkService.getInfoMemoContent(current_p_id, current_m_id);
                            InfoMemoContent.enqueue(new Callback<MemoContent>() {
                                @Override
                                public void onResponse(Response<MemoContent> response, Retrofit retrofit) {
                                    if (response.isSuccess()) {
                                        current_MemoCentent = response.body();
                                        edittext_memo.setText(current_MemoCentent.content);
                                        bool_memo_was_exist = true;
                                    } else {
                                        int statusCode = response.code();
                                        Log.i("MyTag", "응답코드 9: " + statusCode);
                                        //setUnderViewNull();
                                    }
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                                    Log.i("MyTag", "에러내용 9: " + t.getMessage());
                                    //setUnderViewNull();
                                }
                            });
                        }
                    }
                    switch (finalIndex % 7) {
                        case 0:
                            edittext_memo.setBackgroundResource(R.mipmap.memo_sat);
                            break;
                        case 1:
                            edittext_memo.setBackgroundResource(R.mipmap.memo_sun);
                            break;
                        case 2:
                            edittext_memo.setBackgroundResource(R.mipmap.memo_mon);
                            break;
                        case 3:
                            edittext_memo.setBackgroundResource(R.mipmap.memo_tue);
                            break;
                        case 4:
                            edittext_memo.setBackgroundResource(R.mipmap.memo_wed);
                            break;
                        case 5:
                            edittext_memo.setBackgroundResource(R.mipmap.memo_thu);
                            break;
                        case 6:
                            edittext_memo.setBackgroundResource(R.mipmap.memo_fri);
                            break;
                    }
                    edittext_memo.setX(75);
                    edittext_memo.setY(220);
                    imageview_memo.setX(570);
                    imageview_memo.setY(265);
                    if (finalIndex <= 7) {
                        edittext_memo.setY(60);
                        imageview_memo.setY(105);
                    } else if (finalIndex <= 14) {
                        edittext_memo.setY(150);
                        imageview_memo.setY(195);
                    } else if (finalIndex <= 21) {
                        edittext_memo.setY(240);
                        imageview_memo.setY(285);

                    } else if (finalIndex <= 28) {
                        edittext_memo.setY(330);
                        imageview_memo.setY(375);
                    } else if (finalIndex <= 35) {
                        edittext_memo.setY(420);
                        imageview_memo.setY(465);
                    } else {
                        if (finalIndex % 7 == 1) {
                            edittext_memo.setBackgroundResource(R.mipmap.memo_6th_sun);
                        } else {
                            edittext_memo.setBackgroundResource(R.mipmap.memo_6th_mon);
                        }
                        edittext_memo.setY(330);
                        imageview_memo.setY(375);

                    }
                    edittext_memo.setVisibility(View.VISIBLE);
                    imageview_memo.setVisibility(View.VISIBLE);
                    // TODO: 2016. 1. 13. 해당 날짜 db 불러오기(메모 포함)
                    */
                    selected_day_calendar.set(Calendar.DAY_OF_MONTH, finalIndex - calendar_index);
                }
            });
            /*
            if(edittext_memo.getText().toString()==""){ //메모 db가 null일 때로 후에 수정
                bool_memo_was_exist=false;
            } else {
                bool_memo_was_exist=true;
            }*/
        }
        /*
        imageview_memo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(edittext_memo.getWindowToken(), 0);

                if (bool_memo_was_exist == false && !edittext_memo.getText().toString().isEmpty()) { //메모 등록
                    MemoContent memo = new MemoContent();
                    memo.year = selected_day_calendar.get(Calendar.YEAR);
                    memo.month = selected_day_calendar.get(Calendar.MONTH) + 1;
                    memo.date = selected_day_calendar.get(Calendar.DAY_OF_MONTH);
                    memo.content = edittext_memo.getText().toString();
                    memo.p_id = current_p_id;
                    String temp = String.valueOf(selected_day_calendar.get(Calendar.YEAR)) + String.valueOf(selected_day_calendar.get(Calendar.MONTH)) + String.valueOf(selected_day_calendar.get(Calendar.DAY_OF_MONTH));
                    memo.m_id = Integer.parseInt(temp);
                    Call<MemoContent> memoCall = networkService.insertMemoContent(memo);
                    memoCall.enqueue(new Callback<MemoContent>() {
                        @Override
                        public void onResponse(Response<MemoContent> response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                //저장
                                Toast.makeText(CalendarActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                                initDB(selected_day_calendar.get(Calendar.MONTH) + 1, selected_day_calendar.get(Calendar.YEAR));
                                setListener();
                            } else {
                                int statusCode = response.code();
                                Log.i("MyTag", "응답코드 6: " + statusCode);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.i("MyTag", "에러내용 6: " + t.getMessage());
                            Toast.makeText(CalendarActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                            initDB(selected_day_calendar.get(Calendar.MONTH) + 1, selected_day_calendar.get(Calendar.YEAR));
                            setListener();
                        }
                    });
                } else if (bool_memo_was_exist == true && !edittext_memo.getText().toString().isEmpty()) { //수정
                    current_MemoCentent.content = edittext_memo.getText().toString();
                    Call<MemoContent> MemoEditCall = networkService.updateMemoContent(current_MemoCentent.m_id, current_MemoCentent);
                    MemoEditCall.enqueue(new Callback<MemoContent>() {
                        @Override
                        public void onResponse(Response<MemoContent> response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                Toast.makeText(CalendarActivity.this, "수정되었습니다.", Toast.LENGTH_SHORT).show();
                                initDB(selected_day_calendar.get(Calendar.MONTH) + 1, selected_day_calendar.get(Calendar.YEAR));
                                setListener();
                            } else {
                                int statusCode = response.code();
                                Log.i("MyTag", "응답코드 7: " + statusCode);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.i("MyTag", "에러내용 7: " + t.getMessage());
                        }
                    });

                } else if (bool_memo_was_exist == true && edittext_memo.getText().toString().isEmpty()) { //삭제
                    Call<MemoContent> MemoDeleteCall = networkService.deleteMemoContent(current_MemoCentent.m_id);
                    MemoDeleteCall.enqueue(new Callback<MemoContent>() {
                        @Override
                        public void onResponse(Response<MemoContent> response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                Toast.makeText(CalendarActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                initDB(selected_day_calendar.get(Calendar.MONTH) + 1, selected_day_calendar.get(Calendar.YEAR));
                                setListener();
                            } else {
                                int statusCode = response.code();
                                Log.i("MyTag", "응답코드 8: " + statusCode);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.i("MyTag", "에러내용 8: " + t.getMessage());
                        }
                    });
                }

                if (edittext_memo.getText().toString().isEmpty()) {
                    bool_memo_was_exist = false;
                } else {
                    bool_memo_was_exist = true;
                }
                edittext_memo.setText("");
                edittext_memo.setVisibility(View.GONE);
                imageview_memo.setVisibility(View.GONE);
            }
        });
        */
        bLastMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = getLastMonth(calendar);
                initCalendar(calendar);
                initDB((long) calendar.get(Calendar.MONTH) + 1, (long) calendar.get(Calendar.YEAR));
                setListener();
                switch (calendar.get(Calendar.MONTH)) {
                    case 0:
                        appBarLayout.setBackgroundResource(R.drawable.month1);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#37464e"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_icon_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm);
                        bLastMonth.setImageResource(R.mipmap.calendar_left);
                        bNextMonth.setImageResource(R.mipmap.calendar_right);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open);
                        break;
                    case 1:
                        appBarLayout.setBackgroundResource(R.drawable.month2);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                    case 2:
                        appBarLayout.setBackgroundResource(R.drawable.month3);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#37464e"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_icon_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm);
                        bLastMonth.setImageResource(R.mipmap.calendar_left);
                        bNextMonth.setImageResource(R.mipmap.calendar_right);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open);
                        break;
                    case 3:
                        appBarLayout.setBackgroundResource(R.drawable.month4);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                    case 4:
                        appBarLayout.setBackgroundResource(R.drawable.month5);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#37464e"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_icon_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm);
                        bLastMonth.setImageResource(R.mipmap.calendar_left);
                        bNextMonth.setImageResource(R.mipmap.calendar_right);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open);
                        break;
                    case 5:
                        appBarLayout.setBackgroundResource(R.drawable.month6);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                    case 6:
                        appBarLayout.setBackgroundResource(R.drawable.month7);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                    case 7:
                        appBarLayout.setBackgroundResource(R.drawable.month8);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                    case 8:
                        appBarLayout.setBackgroundResource(R.drawable.month9);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                    case 9:
                        appBarLayout.setBackgroundResource(R.drawable.month10);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                    case 10:
                        appBarLayout.setBackgroundResource(R.drawable.month11);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                    case 11:
                        appBarLayout.setBackgroundResource(R.drawable.month12);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                }
            }
        });
        bNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = getNextMonth(calendar);
                initCalendar(calendar);
                setListener();
                initDB((long) calendar.get(Calendar.MONTH) + 1, (long) calendar.get(Calendar.YEAR));
                switch (calendar.get(Calendar.MONTH)) {
                    case 0:
                        appBarLayout.setBackgroundResource(R.drawable.month1);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#37464e"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_icon_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm);
                        bLastMonth.setImageResource(R.mipmap.calendar_left);
                        bNextMonth.setImageResource(R.mipmap.calendar_right);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open);
                        break;
                    case 1:
                        appBarLayout.setBackgroundResource(R.drawable.month2);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                    case 2:
                        appBarLayout.setBackgroundResource(R.drawable.month3);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#37464e"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_icon_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm);
                        bLastMonth.setImageResource(R.mipmap.calendar_left);
                        bNextMonth.setImageResource(R.mipmap.calendar_right);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open);
                        break;
                    case 3:
                        appBarLayout.setBackgroundResource(R.drawable.month4);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                    case 4:
                        appBarLayout.setBackgroundResource(R.drawable.month5);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#37464e"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_icon_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm);
                        bLastMonth.setImageResource(R.mipmap.calendar_left);
                        bNextMonth.setImageResource(R.mipmap.calendar_right);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open);
                        break;
                    case 5:
                        appBarLayout.setBackgroundResource(R.drawable.month6);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                    case 6:
                        appBarLayout.setBackgroundResource(R.drawable.month7);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                    case 7:
                        appBarLayout.setBackgroundResource(R.drawable.month8);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                    case 8:
                        appBarLayout.setBackgroundResource(R.drawable.month9);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                    case 9:
                        appBarLayout.setBackgroundResource(R.drawable.month10);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                    case 10:
                        appBarLayout.setBackgroundResource(R.drawable.month11);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));
                        imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                    case 11:
                        appBarLayout.setBackgroundResource(R.drawable.month12);
                        mTvCalendarTitle.setTextColor(Color.parseColor("#ffffff"));imagebtn_add.setImageResource(R.mipmap.calendar_wh_add);
                        imagebtn_alarm_setting.setImageResource(R.mipmap.calendar_alarm_on_wh);
                        bLastMonth.setImageResource(R.mipmap.calendar_left_wh);
                        bNextMonth.setImageResource(R.mipmap.calendar_right_wh);
                        toolbar.setNavigationIcon(R.mipmap.drawer_icon_open_wh);
                        break;
                }
            }
        });
       /*under_icon_edit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    under_icon_edit.setBackgroundResource(R.mipmap.edit_icon_click);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    under_icon_edit.setBackgroundResource(R.mipmap.edit_icon_unclick);
                }
                Intent intent = new Intent(CalendarActivity.this, UpdateActivity.class);
                intent.putExtra("current_s_id", current_s_id);
                startActivity(intent);
                return false;
            }
        });
        under_icon_delete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    under_icon_delete.setImageResource(R.mipmap.delete_icon_click);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    under_icon_delete.setImageResource(R.mipmap.delete_icon_unclick);
                }

                Call<ScheduleContent> scheduleContentCall = networkService.deleteScheduleContent(current_p_id,current_s_id);
                scheduleContentCall.enqueue(new Callback<ScheduleContent>() {
                    @Override
                    public void onResponse(Response<ScheduleContent> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            //finish();
                            //onResume();
                            initDB(selected_day_calendar.get(Calendar.MONTH) + 1, selected_day_calendar.get(Calendar.YEAR));
                            setListener();
                            Log.i("MyTag", "삭제성공 ");
                        } else {
                            Log.i("MyTag", "응답코드: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i("MyTag", "에러: " + t.getMessage());
                    }
                });

                return false;
            }
        });
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_detail = new Intent(getApplicationContext(), DetailDialogActivity.class);
                intent_detail.putExtra("current_s_id", current_s_id);
                Log.i("MyTagCalendar", "" + current_s_id);
                startActivity(intent_detail);
            }
        });*/
        imagebtn_alarm_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CalendarActivity.this, "알람이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                setAlarm();
            }
        });
        imagebtn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CalendarActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initCalendar(Calendar calendar) {
        int lastMonthStartDay;
        int dayOfMonth;
        int thisMonthLastDay;

        // 이번달 시작일의 요일을 구한다. 시작일이 일요일인 경우 인덱스를 1(일요일)에서 8(다음주 일요일)로 바꾼다.)
        dayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);
        thisMonthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.add(Calendar.MONTH, -1);

        // 지난달의 마지막 일자를 구한다.
        lastMonthStartDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        calendar.add(Calendar.MONTH, 1);

        if (dayOfMonth == Calendar.SUNDAY) {
            dayOfMonth -= 7;
        }
        lastMonthStartDay -= (dayOfMonth - 1) - 1;
        int monthI = calendar.get(Calendar.MONTH);
        selected_day_calendar = calendar;
        String monthS = "";
        if (monthI == 0) {
            monthS = "JANUARY";
        } else if (monthI == 1) {
            monthS = "FEBRUARY";
        } else if (monthI == 2) {
            monthS = "MARCH";
        } else if (monthI == 3) {
            monthS = "APRIL";
        } else if (monthI == 4) {
            monthS = "MAY";
        } else if (monthI == 5) {
            monthS = "JUNE";
        } else if (monthI == 6) {
            monthS = "JULY";
        } else if (monthI == 7) {
            monthS = "AUGUST";
        } else if (monthI == 8) {
            monthS = "SEPTEMBER";
        } else if (monthI == 9) {
            monthS = "OCTOBER";
        } else if (monthI == 10) {
            monthS = "NOVEMBER";
        } else if (monthI == 11) {
            monthS = "DECEMBER";
        }
        mTvCalendarTitle.setText(monthS + " " + calendar.get(Calendar.YEAR));
        int index = 0;
        calendar_index = dayOfMonth - 1;
        for (int i = 0; i < dayOfMonth - 1; i++) {
            textview_array.get(index).setText("");
            index++;
        }
        calendar_index_2 = thisMonthLastDay;
        for (int i = 1; i <= thisMonthLastDay; i++) {
            if(i<10) {
                textview_array.get(index).setText(String.valueOf(i));
            } else {
                textview_array.get(index).setText(String.valueOf(i));
            }
            if (index % 7 == 0) {
                textview_array.get(index).setTextColor(Color.parseColor("#bc7272"));
            } else {
                textview_array.get(index).setTextColor(Color.parseColor("#708895"));
            }
            index++;
        }
        for (int i = index; i < 42; i++) {
            textview_array.get(index).setTextColor(Color.WHITE);
            index++;
        }
    }


    private void initView() {
        pager= (ViewPager)findViewById(R.id.under_view_pager);
        adapter= new ViewPagerAdapter(getLayoutInflater());
        frameLayout = (FrameLayout) findViewById(R.id.under_view_frame);
        under_icon_delete = (ImageView) findViewById(R.id.under_delete_icon);
        under_icon_edit = (ImageView) findViewById(R.id.under_edit_icon);
        under_tv_todo = (TextView) findViewById(R.id.under_textview_todo);
        under_tv_friend = (TextView) findViewById(R.id.under_textview_friend);
        under_tv_location = (TextView) findViewById(R.id.under_textview_location);
        under_tv_time = (TextView) findViewById(R.id.under_textview_time);
        bLastMonth = (ImageView) findViewById(R.id.gv_calendar_activity_b_last);
        bNextMonth = (ImageView) findViewById(R.id.gv_calendar_activity_b_next);
        mTvCalendarTitle = (TextView) findViewById(R.id.gv_calendar_activity_tv_title);
        imagebtn_add = (ImageButton) findViewById(R.id.imagebtn_add);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        calendar_textview_0 = (TextView) findViewById(R.id.calendar_textview_0);
        calendar_textview_1 = (TextView) findViewById(R.id.calendar_textview_1);
        calendar_textview_2 = (TextView) findViewById(R.id.calendar_textview_2);
        calendar_textview_3 = (TextView) findViewById(R.id.calendar_textview_3);
        calendar_textview_4 = (TextView) findViewById(R.id.calendar_textview_4);
        calendar_textview_5 = (TextView) findViewById(R.id.calendar_textview_5);
        calendar_textview_6 = (TextView) findViewById(R.id.calendar_textview_6);
        calendar_textview_7 = (TextView) findViewById(R.id.calendar_textview_7);
        calendar_textview_8 = (TextView) findViewById(R.id.calendar_textview_8);
        calendar_textview_9 = (TextView) findViewById(R.id.calendar_textview_9);
        calendar_textview_10 = (TextView) findViewById(R.id.calendar_textview_10);
        calendar_textview_11 = (TextView) findViewById(R.id.calendar_textview_11);
        calendar_textview_12 = (TextView) findViewById(R.id.calendar_textview_12);
        calendar_textview_13 = (TextView) findViewById(R.id.calendar_textview_13);
        calendar_textview_14 = (TextView) findViewById(R.id.calendar_textview_14);
        calendar_textview_15 = (TextView) findViewById(R.id.calendar_textview_15);
        calendar_textview_16 = (TextView) findViewById(R.id.calendar_textview_16);
        calendar_textview_17 = (TextView) findViewById(R.id.calendar_textview_17);
        calendar_textview_18 = (TextView) findViewById(R.id.calendar_textview_18);
        calendar_textview_19 = (TextView) findViewById(R.id.calendar_textview_19);
        calendar_textview_20 = (TextView) findViewById(R.id.calendar_textview_20);
        calendar_textview_21 = (TextView) findViewById(R.id.calendar_textview_21);
        calendar_textview_22 = (TextView) findViewById(R.id.calendar_textview_22);
        calendar_textview_23 = (TextView) findViewById(R.id.calendar_textview_23);
        calendar_textview_24 = (TextView) findViewById(R.id.calendar_textview_24);
        calendar_textview_25 = (TextView) findViewById(R.id.calendar_textview_25);
        calendar_textview_26 = (TextView) findViewById(R.id.calendar_textview_26);
        calendar_textview_27 = (TextView) findViewById(R.id.calendar_textview_27);
        calendar_textview_28 = (TextView) findViewById(R.id.calendar_textview_28);
        calendar_textview_29 = (TextView) findViewById(R.id.calendar_textview_29);
        calendar_textview_30 = (TextView) findViewById(R.id.calendar_textview_30);
        calendar_textview_31 = (TextView) findViewById(R.id.calendar_textview_31);
        calendar_textview_32 = (TextView) findViewById(R.id.calendar_textview_32);
        calendar_textview_33 = (TextView) findViewById(R.id.calendar_textview_33);
        calendar_textview_34 = (TextView) findViewById(R.id.calendar_textview_34);
        calendar_textview_35 = (TextView) findViewById(R.id.calendar_textview_35);
        calendar_textview_36 = (TextView) findViewById(R.id.calendar_textview_36);
        calendar_textview_37 = (TextView) findViewById(R.id.calendar_textview_37);
        calendar_textview_38 = (TextView) findViewById(R.id.calendar_textview_38);
        calendar_textview_39 = (TextView) findViewById(R.id.calendar_textview_39);
        calendar_textview_40 = (TextView) findViewById(R.id.calendar_textview_40);
        calendar_textview_41 = (TextView) findViewById(R.id.calendar_textview_41);

        textview_array = new ArrayList<TextView>();
        textview_array.add(calendar_textview_0);
        textview_array.add(calendar_textview_1);
        textview_array.add(calendar_textview_2);
        textview_array.add(calendar_textview_3);
        textview_array.add(calendar_textview_4);
        textview_array.add(calendar_textview_5);
        textview_array.add(calendar_textview_6);
        textview_array.add(calendar_textview_7);
        textview_array.add(calendar_textview_8);
        textview_array.add(calendar_textview_9);
        textview_array.add(calendar_textview_10);
        textview_array.add(calendar_textview_11);
        textview_array.add(calendar_textview_12);
        textview_array.add(calendar_textview_13);
        textview_array.add(calendar_textview_14);
        textview_array.add(calendar_textview_15);
        textview_array.add(calendar_textview_16);
        textview_array.add(calendar_textview_17);
        textview_array.add(calendar_textview_18);
        textview_array.add(calendar_textview_19);
        textview_array.add(calendar_textview_20);
        textview_array.add(calendar_textview_21);
        textview_array.add(calendar_textview_22);
        textview_array.add(calendar_textview_23);
        textview_array.add(calendar_textview_24);
        textview_array.add(calendar_textview_25);
        textview_array.add(calendar_textview_26);
        textview_array.add(calendar_textview_27);
        textview_array.add(calendar_textview_28);
        textview_array.add(calendar_textview_29);
        textview_array.add(calendar_textview_30);
        textview_array.add(calendar_textview_31);
        textview_array.add(calendar_textview_32);
        textview_array.add(calendar_textview_33);
        textview_array.add(calendar_textview_34);
        textview_array.add(calendar_textview_35);
        textview_array.add(calendar_textview_36);
        textview_array.add(calendar_textview_37);
        textview_array.add(calendar_textview_38);
        textview_array.add(calendar_textview_39);
        textview_array.add(calendar_textview_40);
        textview_array.add(calendar_textview_41);

        imagebtn_alarm_setting = (ImageView) findViewById(R.id.imagebtn_alarm);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.search_bar);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }/* else if (imageview_memo.getVisibility() == View.VISIBLE) {
            imageview_memo.setVisibility(View.GONE);
            edittext_memo.setVisibility(View.GONE);
            edittext_memo.setText("");
        } */else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}