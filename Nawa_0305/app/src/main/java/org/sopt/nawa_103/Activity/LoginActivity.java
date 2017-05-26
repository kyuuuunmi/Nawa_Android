package org.sopt.nawa_103.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.sopt.nawa_103.Background.QuickstartPreferences;
import org.sopt.nawa_103.Background.RegistrationIntentService;
import org.sopt.nawa_103.Component.ApplicationController;
import org.sopt.nawa_103.Model.DB.MemberContent;
import org.sopt.nawa_103.Network.NetworkService;
import org.sopt.nawa_103.R;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class LoginActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    EditText textId;
    EditText textPw;
    ImageButton btnJoin;
    ImageButton btnLogin;
    private NetworkService networkService;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private BroadcastReceiver mRegistrationBroadcastReceiver;




    /**
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     */
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라 UI에 변화를 준다.
     */
    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(action.equals(QuickstartPreferences.REGISTRATION_READY)){
                    // 액션이 READY일 경우
                } else if(action.equals(QuickstartPreferences.REGISTRATION_GENERATING)){
                    // 액션이 GENERATING일 경우
                } else if(action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)){
                    // 액션이 COMPLETE일 경우
                    String token = intent.getStringExtra("token");
                }

            }
        };
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //사용자 id, pw, 자동로그인여부 저장 SharedPreferences
        pref = getSharedPreferences("setting", 0);
        editor = pref.edit();
        // 스플래시
        startActivity(new Intent(LoginActivity.this, SplashActivity.class));

        hideStatus();
        ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService();
        initNetworkService();
        registBroadcastReceiver();
        Init();
        //txtEvnt();
        btnEvnt();


    }

    private void initNetworkService() {

        networkService = ApplicationController.getInstance().getNetworkService();

    }
    private void hideStatus() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void btnEvnt() {
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String unformattedNum = tm.getLine1Number();
                //String formattedNum = PhoneNumberUtils.formatNumber(unformattedNum);
                String formattedNum = PhoneNumberUtils.formatNumber(unformattedNum, "KR");


                if(formattedNum.charAt(0) == '+' || formattedNum.length() != 13){ // +82 1x-xxxx-xxxx일 때, 01x-xxxx-xxxx 형식으로 바꿔서 보내기
                    Log.i("MyTag", "전화번호 자릿수: "+formattedNum.length());
                    formattedNum = "0" + formattedNum.substring(4, 16);
                }

                JoinDialog joinDialog = new JoinDialog(LoginActivity.this);
                joinDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                joinDialog.getPhoneNum(formattedNum);
                joinDialog.show();

            }
        });
        btnJoin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    btnJoin.setBackgroundResource(R.mipmap.login_joinunselect);
                if (event.getAction() == MotionEvent.ACTION_UP)
                    btnJoin.setBackgroundResource(R.mipmap.login_joinselect);
                return false;
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login();

            }
        });
        btnLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    btnLogin.setBackgroundResource(R.mipmap.login_unselect);
                if (event.getAction() == MotionEvent.ACTION_UP)
                    btnLogin.setBackgroundResource(R.mipmap.login_select);

                return false;
            }
        });
    }

    private void login(){

        getInstanceIdToken(); //디바이스 토큰 생성

        MemberContent memberContent = new MemberContent();
        memberContent.user_id = textId.getText().toString();
        memberContent.pw = textPw.getText().toString();
        memberContent.reg_id = pref.getString("regid", "");


        Call<MemberContent> loginCall = networkService.loginMemberContent(memberContent);

        loginCall.enqueue(new Callback<MemberContent>() {
            @Override
            public void onResponse(Response<MemberContent> response, Retrofit retrofit) {
                if (response.isSuccess()) {

                    editor.putString("id", textId.getText().toString());
                    editor.putString("pw", textPw.getText().toString());
                    editor.putInt("current_p_id", response.body().p_id);

                    editor.commit();

                    Log.i("MyTag", "로그인성공 ID: " + textId.getText().toString() + ", pw: " + textPw.getText().toString());

                    Intent intent = new Intent(LoginActivity.this, CalendarActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Log.i("MyTag", "로그인 응답코드: " + response.code());
                    Toast.makeText(getApplicationContext(), "잘못된 ID 또는 비밀번호를 입력하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "로그인 에러: " + t.getMessage());
            }

        });
    }

/*
    private void txtEvnt() {
        // TODO: 2016-01-13 Login 로그인정보
        Account account;
        account = new Account(textId.getText().toString(), textPw.getText().toString());
    }
*/
    private void Init() {
        textId = (EditText) findViewById(R.id.Login_textId);
        textPw = (EditText) findViewById(R.id.Login_textPw);
        btnJoin = (ImageButton) findViewById(R.id.Login_btnJoin);
        btnLogin = (ImageButton) findViewById(R.id.Login_btnLogin);

    }


    /**
     * 앱이 실행되어 화면에 나타날때 LocalBoardcastManager에 액션을 정의하여 등록한다.
     */
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

    }

    /**
     * 앱이 화면에서 사라지면 등록된 LocalBoardcast를 모두 삭제한다.
     */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
    @Override
    protected void onStart() {
        super.onStart();
        //pref.getBoolean("autoCheck", false)
        if(pref.getBoolean("autoCheck",false)) {//자동 로그인 true일 때
            Log.i("MyTag", "자동로그인 설정 "+pref.getBoolean("autoCheck", false));

            textId.setText(pref.getString("id", ""));
            textPw.setText(pref.getString("pw", ""));

            login();

        }else{//자동 로그인 해제
            textId.setText("");
            textPw.setText("");
        }

    }

    /**
     * Google Play Service를 사용할 수 있는 환경이지를 체크한다.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //Log.i(TAG, "This device is ot supported.");
                finish();
            }
            return false;
        }
        return true;
    }



}
