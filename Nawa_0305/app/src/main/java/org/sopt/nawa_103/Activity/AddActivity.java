package org.sopt.nawa_103.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.sopt.nawa_103.Component.ApplicationController;
import org.sopt.nawa_103.Model.DB.Friend;
import org.sopt.nawa_103.Model.DB.PushModel;
import org.sopt.nawa_103.Model.DB.ScheduleContent;
import org.sopt.nawa_103.Network.NetworkService;
import org.sopt.nawa_103.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class AddActivity extends Activity {
    Calendar calendar;
    Intent intent_search_friend, intent_search_map, intent_save_info;
    EditText edittext_name;
    TextView textview_date, textview_time, textview_friend, textview_place;
    ImageButton imagebtn_search_friend, imagebtn_search_map, imagebtn_morning, imagebtn_afternoon, imagebtn_dinner, imagebtn_ok;
    LinearLayout imagebtn_back;
    int current_p_id=0;
    SharedPreferences preferences;
    boolean checkDate = false, checkTime = false;

    private NetworkService networkService;
    ScheduleContent tempContent;
    String[] resultName = null;
    String[] resultNum = null;
    String mapName ="";
    String mapAddress = "";
    String mapTel = "";
    double mapLatitude =0;
    double mapLongitude=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        initNetworkService();
        initView();
        setListener();

    }
    private void initNetworkService() {
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    @Override
    protected void onResume() {
        super.onResume();

        tempContent= new ScheduleContent();
        preferences = getSharedPreferences("setting", 0);
        current_p_id = preferences.getInt("current_p_id",-1);

    }

    protected void onRestart() {
        super.onRestart();

        String temp = "";
        if(resultName != null) {
            for (int i = 0; i < resultName.length; i++) {
                temp += " " + resultName[i];
            }
        }
        textview_friend.setText(temp);
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
                intent_search_friend = new Intent(AddActivity.this, FriendsActivity.class);
                startActivityForResult(intent_search_friend, 1);
            }
        });

        imagebtn_search_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent_search_map = new Intent(AddActivity.this, MapActivity.class);
                startActivityForResult(intent_search_map, 0);
            }
        });
        textview_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagebtn_morning.setImageResource(R.mipmap.add_morning_unclick);
                imagebtn_afternoon.setImageResource(R.mipmap.add_afternoon_unclick);
                imagebtn_dinner.setImageResource(R.mipmap.add_dinner_unclick);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivity.this, onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

            }
        });
        textview_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagebtn_morning.setImageResource(R.mipmap.add_morning_unclick);
                imagebtn_afternoon.setImageResource(R.mipmap.add_afternoon_unclick);
                imagebtn_dinner.setImageResource(R.mipmap.add_dinner_unclick);
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddActivity.this, onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddActivity.this, onTimeSetListener, 9, 00, false);
                timePickerDialog.show();
            }
        });
        imagebtn_afternoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagebtn_morning.setImageResource(R.mipmap.add_morning_unclick);
                imagebtn_afternoon.setImageResource(R.mipmap.add_afternoon_click);
                imagebtn_dinner.setImageResource(R.mipmap.add_dinner_unclick);
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddActivity.this, onTimeSetListener, 12, 00, false);
                timePickerDialog.show();
            }
        });
        imagebtn_dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagebtn_morning.setImageResource(R.mipmap.add_morning_unclick);
                imagebtn_afternoon.setImageResource(R.mipmap.add_afternoon_unclick);
                imagebtn_dinner.setImageResource(R.mipmap.add_dinner_click);
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddActivity.this, onTimeSetListener, 18, 00, false);
                timePickerDialog.show();
            }
        });

        imagebtn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkDate) {
                    if (!checkTime)
                        Toast.makeText(getApplicationContext(), "시간을 선택하세요.", Toast.LENGTH_SHORT).show();
                    else{
                        List<Friend> tmpFriend = new ArrayList<>();
                        for(int i=0; i<resultName.length; i++){
                            tmpFriend.add(new Friend(resultName[i], resultNum[i]));
                        }

                        tempContent.p_id = current_p_id;
                        tempContent.title = edittext_name.getText().toString();
                        tempContent.friend = tmpFriend;

                        tempContent.place = mapName;
                        tempContent.place_address = mapAddress;
                        tempContent.place_tel = mapTel;
                        tempContent.place_latitude = mapLatitude;
                        tempContent.place_longitude = mapLongitude;

                        Call<ScheduleContent> scheduleContentCall = networkService.insertScheduleContent(tempContent);
                        scheduleContentCall.enqueue(new Callback<ScheduleContent>() {

                            @Override
                            public void onResponse(Response<ScheduleContent> response, Retrofit retrofit) {
                                if (response.isSuccess()) {

                                    Toast.makeText(AddActivity.this, "새로운 일정이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                    ScheduleContent scheduleContent = response.body();

                                    PushModel pm = new PushModel();
                                    pm.s_id = scheduleContent.s_id ;
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

                                    Intent intentSubmit = new Intent(AddActivity.this, CalendarActivity.class);
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

                }else{
                    Toast.makeText(getApplicationContext(), "날짜를 선택하세요.", Toast.LENGTH_SHORT).show();
                }

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
                tmpmonth="0"+Integer.toString(monthOfYear+1);
            }else{
                tmpmonth=Integer.toString(monthOfYear+1);
            }
            if(dayOfMonth / 10 == 0){
                tmpday="0"+Integer.toString(dayOfMonth);
            }else{
                tmpday=Integer.toString(dayOfMonth);
            }

            tempContent.totaldate=Integer.toString(year)+"-"+tmpmonth+"-"+tmpday;
            Log.i("mytag", "totaldate:" + tempContent.totaldate);

            tempContent.year=year;
            tempContent.month=monthOfYear+1;
            tempContent.date=dayOfMonth;

            checkDate = true;

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
            tempContent.amorpm = amorpm;

            checkTime = true;
        }
    };


    private void initView() {
        edittext_name = (EditText) findViewById(R.id.add_txtName);
        textview_friend = (TextView) findViewById(R.id.add_txtFriend);
        textview_place = (TextView) findViewById(R.id.add_btnLocation);
        textview_date = (TextView) findViewById(R.id.add_txtSetdate);
        textview_time = (TextView) findViewById(R.id.add_txtSettime);
        imagebtn_search_friend = (ImageButton) findViewById(R.id.add_btnFriend);
        imagebtn_search_map = (ImageButton) findViewById(R.id.location_imageButton);
        imagebtn_morning = (ImageButton) findViewById(R.id.add_btnMorning);
        imagebtn_afternoon = (ImageButton) findViewById(R.id.add_btnAfternoon);
        imagebtn_dinner = (ImageButton) findViewById(R.id.add_btnDinner);
        imagebtn_ok = (ImageButton) findViewById(R.id.ok_imagebutton);
        imagebtn_back = (LinearLayout)findViewById(R.id.add_toolbar_btnBack);
        calendar = Calendar.getInstance();
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

    //친구, 장소 검색 이후 처리
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) { //친구 검색 결과
            Bundle b;
            if((b = data.getBundleExtra("items")) != null){
                resultName = b.getStringArray("selectedName");
                resultNum = b.getStringArray("selectedNum");
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
            resultName = null;
            resultNum = null;
        }
    }
}