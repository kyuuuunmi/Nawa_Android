package org.sopt.nawa_103.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.sopt.nawa_103.Component.ApplicationController;
import org.sopt.nawa_103.Model.DB.Friend;
import org.sopt.nawa_103.Model.DB.PushModel;
import org.sopt.nawa_103.Model.DB.ScheduleContent;
import org.sopt.nawa_103.Model.DB.ScheduleTmp;
import org.sopt.nawa_103.Network.NetworkService;
import org.sopt.nawa_103.R;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by jihoon on 2016-01-15.
 */
public class UpdateActivity extends Activity {
    Calendar calendar;
    Intent intent_search_friend, intent_search_map, intent_save_info;
    EditText edittext_name;
    TextView textview_date, textview_time,textview_friend, textview_place;
    ImageButton imagebtn_search_friend, imagebtn_search_map, imagebtn_morning, imagebtn_afternoon, imagebtn_dinner, imagebtn_ok, imagebtn_back;
    private NetworkService networkService;
    ScheduleContent tempContent;
    int current_p_id=0;
    int current_s_id=0;
    SharedPreferences preferences;
    ArrayList<String> tmpFriendName=new ArrayList<>();
    ArrayList<String> tmpFriendPhone=new ArrayList<>();
    String[] resultName = null;
    String[] resultNum = null;
    String friends = "";
    String mapName ="";
    String mapAddress = "";
    String mapTel = "";
    double mapLatitude = 0;
    double mapLongitude = 0;
    ArrayList<Friend> tmpFriend = new ArrayList<Friend>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        initNetworkService();
        initView();
        setListener();

    }


    @Override
    protected void onResume() {
        super.onResume();
        tempContent = new ScheduleContent();
        preferences = getSharedPreferences("setting", 0);
        current_s_id=getIntent().getIntExtra("current_s_id", 0);
        current_p_id = preferences.getInt("current_p_id", -1);

        initData();
        textview_friend.setText(friends);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        current_s_id=getIntent().getIntExtra("current_s_id", 0);
        String temp = "";
        if(resultName != null) {
            for (int i = 0; i < resultName.length; i++) {
                temp += " " + resultName[i];
            }
        }
        friends=temp;

    }

    private void initNetworkService() {
        networkService = ApplicationController.getInstance().getNetworkService();

    }
    private void setListener() {
        imagebtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imagebtn_search_friend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    imagebtn_search_friend.setImageResource(R.mipmap.add_friend_click);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    imagebtn_search_friend.setImageResource(R.mipmap.add_friend_unclick);
                }
                return false;
            }
        });
        imagebtn_search_map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    imagebtn_search_map.setImageResource(R.mipmap.add_map_click);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    imagebtn_search_map.setImageResource(R.mipmap.add_map_unclick);
                }
                return false;
            }
        });
        imagebtn_search_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                intent_search_friend = new Intent(UpdateActivity.this, FriendsActivity.class);
                intent_search_friend.putStringArrayListExtra("exist_friend_list", tmpFriendPhone);

                startActivityForResult(intent_search_friend, 1);
            }
        });

        imagebtn_search_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent_search_map = new Intent(UpdateActivity.this, MapActivity.class);
                startActivityForResult(intent_search_map, 0);
            }
        });
        textview_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagebtn_morning.setImageResource(R.mipmap.add_morning_unclick);
                imagebtn_afternoon.setImageResource(R.mipmap.add_afternoon_unclick);
                imagebtn_dinner.setImageResource(R.mipmap.add_dinner_unclick);

                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateActivity.this, onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

                tempContent.year=datePickerDialog.getDatePicker().getYear();
                tempContent.month=datePickerDialog.getDatePicker().getMonth();
                tempContent.date=datePickerDialog.getDatePicker().getDayOfMonth();
            }
        });
        textview_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagebtn_morning.setImageResource(R.mipmap.add_morning_unclick);
                imagebtn_afternoon.setImageResource(R.mipmap.add_afternoon_unclick);
                imagebtn_dinner.setImageResource(R.mipmap.add_dinner_unclick);
                TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateActivity.this, onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });
        imagebtn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagebtn_morning.setImageResource(R.mipmap.add_morning_unclick);
                imagebtn_afternoon.setImageResource(R.mipmap.add_afternoon_unclick);
                imagebtn_dinner.setImageResource(R.mipmap.add_dinner_unclick);
                startActivity(intent_save_info);
                finish();
            }
        });
        imagebtn_morning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagebtn_morning.setImageResource(R.mipmap.add_morning_click);
                imagebtn_afternoon.setImageResource(R.mipmap.add_afternoon_unclick);
                imagebtn_dinner.setImageResource(R.mipmap.add_dinner_unclick);
                TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateActivity.this, onTimeSetListener, 9, 00, false);
                timePickerDialog.show();

            }
        });
        imagebtn_afternoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagebtn_morning.setImageResource(R.mipmap.add_morning_unclick);
                imagebtn_afternoon.setImageResource(R.mipmap.add_afternoon_click);
                imagebtn_dinner.setImageResource(R.mipmap.add_dinner_unclick);
                TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateActivity.this, onTimeSetListener, 12, 00, false);
                timePickerDialog.show();
            }
        });
        imagebtn_dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagebtn_morning.setImageResource(R.mipmap.add_morning_unclick);
                imagebtn_afternoon.setImageResource(R.mipmap.add_afternoon_unclick);
                imagebtn_dinner.setImageResource(R.mipmap.add_dinner_click);
                TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateActivity.this, onTimeSetListener, 18, 00, false);
                timePickerDialog.show();
            }
        });
        imagebtn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Friend> tmpFriend=new ArrayList<>();


                if(resultName!=null) {
                    tempContent.friend.clear();
                    for (int i = 0; i < resultName.length; i++) {
                        tmpFriend.add(new Friend(resultName[i], resultNum[i]));
                    }
                    tempContent.friend = tmpFriend;
                }

                tempContent.p_id=current_p_id;
                tempContent.title = edittext_name.getText().toString();

                tempContent.place = mapName;
                tempContent.place_address = mapAddress;
                tempContent.place_tel = mapTel;
                tempContent.place_latitude = mapLatitude;
                tempContent.place_longitude = mapLongitude;

                Call<ScheduleContent> scheduleContentCall = networkService.updateScheduleContent(current_s_id,tempContent);
                scheduleContentCall.enqueue(new Callback<ScheduleContent>() {

                    @Override
                    public void onResponse(Response<ScheduleContent> response, Retrofit retrofit) {
                        if (response.isSuccess()) {

                            Toast.makeText(UpdateActivity.this, "일정이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                            PushModel pm = new PushModel();
                            pm.s_id = current_s_id ;
                            pm.p_id = current_p_id;

                            Call<PushModel> pushCall = networkService.push(pm);
                            pushCall.enqueue(new Callback<PushModel>() {
                                @Override
                                public void onResponse(Response<PushModel> response, Retrofit retrofit) {
                                    if (response.isSuccess()) {
                                        //Toast.makeText(MainActivity.this, "일정이 등록되었습니다.", Toast.LENGTH_SHORT).show();
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

                            Intent intentSubmit = new Intent(UpdateActivity.this, CalendarActivity.class);
                            startActivity(intentSubmit);
                            finish();
                        } else {
                            Log.i("MyTag", "응답코드: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i("MyTag", "에러: " + t.getMessage());
                    }
                });

            }
        });
    }

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month = "", index = "", tmpmonth="", tmpday="";
            switch (monthOfYear) {
                case 0:
                    month = "JANUARY";
                    break;
                case 1:
                    month = "FEBURARY";
                    break;
                case 2:
                    month = "MARCH";
                    break;
                case 3:
                    month = "APRIL";
                    break;
                case 4:
                    month = "MAY";
                    break;
                case 5:
                    month = "JUNE";
                    break;
                case 6:
                    month = "JULY";
                    break;
                case 7:
                    month = "AUGUST";
                    break;
                case 8:
                    month = "SEPTEMBER";
                    break;
                case 9:
                    month = "OCTOBER";
                    break;
                case 10:
                    month = "NOVEMBER";
                    break;
                case 11:
                    month = "DECEMBER";
                    break;
            }
            switch (dayOfMonth) {
                case 1:
                    index = "st";
                    break;
                case 2:
                    index = "nd";
                    break;
                case 3:
                    index = "rd";
                    break;
                default:
                    index = "th";
            }
            textview_date.setText(month + " " + dayOfMonth + index + ", " + year);

            if(monthOfYear / 10 == 0 ){
                tmpmonth='0'+Integer.toString(monthOfYear+1);
            }else{
                tmpmonth=Integer.toString(monthOfYear+1);
            }
            if(dayOfMonth / 10 == 0){
                tmpday='0'+Integer.toString(dayOfMonth);
            }else{
                tmpday=Integer.toString(dayOfMonth);
            }

            tempContent.totaldate=year+'-'+tmpmonth+'-'+tmpday;

            tempContent.year=year;
            tempContent.month=monthOfYear+1;
            tempContent.date=dayOfMonth;


        }
    };
    private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String strHour;
            String strMinute;
            String amorpm = "";
            if (hourOfDay < 12) {
                amorpm = "AM";
                strHour = "" + hourOfDay;

            } else {
                amorpm = "PM";
                hourOfDay -= 12;
                strHour = "" + hourOfDay;
            }

            if(minute<10)
                strMinute = "0"+minute;
            else strMinute = ""+minute;

            textview_time.setText(strHour + ":" + strMinute + " " + amorpm);
            tempContent.hour = Integer.parseInt(strHour.toString());
            tempContent.minute = Integer.parseInt(strMinute.toString());
            tempContent.amorpm =  amorpm;

        }
    };


    private void initView() {
        edittext_name = (EditText) findViewById(R.id.add_txtName);

        textview_friend = (TextView) findViewById(R.id.add_txtFriend);
        textview_date = (TextView) findViewById(R.id.add_txtSetdate);
        textview_time = (TextView) findViewById(R.id.add_txtSettime);
        textview_place = (TextView) findViewById(R.id.add_btnLocation);
        imagebtn_search_friend = (ImageButton) findViewById(R.id.add_btnFriend);
        imagebtn_search_map = (ImageButton) findViewById(R.id.location_imageButton);
        imagebtn_morning = (ImageButton) findViewById(R.id.add_btnMorning);
        imagebtn_afternoon = (ImageButton) findViewById(R.id.add_btnAfternoon);
        imagebtn_dinner = (ImageButton) findViewById(R.id.add_btnDinner);
        imagebtn_ok = (ImageButton) findViewById(R.id.ok_imagebutton);
        imagebtn_back = (ImageButton) findViewById(R.id.add_toolbar_btnBack);
        calendar = Calendar.getInstance();

    }

    private void initData(){
        Call<List<ScheduleTmp>> InfoScheduleCall = networkService.getInfoScheduleContent(current_s_id);
        InfoScheduleCall.enqueue(new Callback<List<ScheduleTmp>>() {

            @Override
            public void onResponse(Response<List<ScheduleTmp>> response, Retrofit retrofit) {
                List<ScheduleTmp> scheduleTmps = response.body();
                List<Friend> inittmpFriend=new ArrayList<>();

                String initFriends="";
                for (int i = 0; i < scheduleTmps.size(); i++) {
                    if(scheduleTmps.get(i).p_id != current_p_id){
                        inittmpFriend.add(new Friend(scheduleTmps.get(i).name, scheduleTmps.get(i).phone));
                        tmpFriendName.add(scheduleTmps.get(i).name);
                        tmpFriendPhone.add(scheduleTmps.get(i).phone);
                        initFriends += scheduleTmps.get(i).name + " ";
                    }
                }

                if(textview_friend.getText().length()==0){
                    textview_friend.setText(initFriends);
                }
                friends="";

                if(edittext_name.getText().length()==0) {
                    edittext_name.setText(scheduleTmps.get(0).title);
                }
                if(textview_place.getText().length()==0) {
                    mapName=scheduleTmps.get(0).place;
                    mapAddress=scheduleTmps.get(0).place_address;
                    mapTel=scheduleTmps.get(0).place_tel;
                    mapLatitude=scheduleTmps.get(0).place_latitude;
                    mapLongitude=scheduleTmps.get(0).place_longitude;
                    textview_place.setText(scheduleTmps.get(0).place);
                }

                if(textview_date.getText().length()==0){
                    if(scheduleTmps.get(0).amorpm.equals("AM")){
                        calendar.set(scheduleTmps.get(0).year, scheduleTmps.get(0).month - 1, scheduleTmps.get(0).date, scheduleTmps.get(0).hour, scheduleTmps.get(0).minute);
                    }
                    else{
                        calendar.set(scheduleTmps.get(0).year, scheduleTmps.get(0).month - 1, scheduleTmps.get(0).date, scheduleTmps.get(0).hour+12, scheduleTmps.get(0).minute);

                    }
                }

                tempContent.friend=inittmpFriend;
                tempContent.year=scheduleTmps.get(0).year;
                tempContent.month=scheduleTmps.get(0).month;
                tempContent.date=scheduleTmps.get(0).date;
                tempContent.hour = scheduleTmps.get(0).hour;
                tempContent.minute = scheduleTmps.get(0).minute;
                tempContent.amorpm =  scheduleTmps.get(0).amorpm;

                int isAMorPM = calendar.get(Calendar.AM_PM);
                switch (isAMorPM) {
                    case Calendar.AM:
                        textview_time.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + " AM");
                        break;
                    case Calendar.PM:
                        textview_time.setText(calendar.get(Calendar.HOUR_OF_DAY)-12 + ":" + calendar.get(Calendar.MINUTE) + " PM");
                }
                String month = "", index = "";
                switch (calendar.get(Calendar.MONTH)) {
                    case 0:
                        month = "JANUARY";
                        break;
                    case 1:
                        month = "FEBURARY";
                        break;
                    case 2:
                        month = "MARCH";
                        break;
                    case 3:
                        month = "APRIL";
                        break;
                    case 4:
                        month = "MAY";
                        break;
                    case 5:
                        month = "JUNE";
                        break;
                    case 6:
                        month = "JULY";
                        break;
                    case 7:
                        month = "AUGUST";
                        break;
                    case 8:
                        month = "SEPTEMBER";
                        break;
                    case 9:
                        month = "OCTOBER";
                        break;
                    case 10:
                        month = "NOVEMBER";
                        break;
                    case 11:
                        month = "DECEMBER";
                        break;
                }
                switch (calendar.get(Calendar.DAY_OF_MONTH)) {
                    case 1:
                        index = "st";
                        break;
                    case 2:
                        index = "nd";
                        break;
                    case 3:
                        index = "rd";
                        break;
                    default:
                        index = "th";
                }
                textview_date.setText(month + " " + calendar.get(Calendar.DAY_OF_MONTH) + index + ", " + calendar.get(Calendar.YEAR));
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    //친구, 장소 검색 이후 처리
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) { //친구 검색 결과
            Bundle b;

            if((b = data.getBundleExtra("items")) != null){
                friends="";
                tmpFriendPhone.clear();

                resultName = b.getStringArray("selectedName");
                resultNum = b.getStringArray("selectedNum");

                for (int i = 0; i < resultNum.length; i++) {
                    tmpFriendPhone.add(resultNum[i]);
                    friends+=resultName[i]+ " ";
                }
            }

        } else if (resultCode == 2) {

            Bundle b2 = data.getBundleExtra("map_items");

            //장소 검색 결과
            mapName = b2.getString("map_name");
            mapAddress = b2.getString("map_address");
            mapTel = b2.getString("map_phone");
            mapLatitude = b2.getDouble("map_Latitude");
            mapLongitude = b2.getDouble("map_Longitude");
            textview_place.setText(mapName);

        }
        else {

        }
    }
}