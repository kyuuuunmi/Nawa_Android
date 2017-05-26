package org.sopt.nawa_103.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import org.sopt.nawa_103.Component.ApplicationController;
import org.sopt.nawa_103.Network.NetworkService;
import org.sopt.nawa_103.R;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by USER on 2016-01-15.
 */
public class SettingActivity extends PreferenceActivity {

    private NetworkService networkService;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        networkService = ApplicationController.getInstance().getNetworkService();

        Preference logoutPref = (Preference) findPreference("prefLogout");
        logoutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                pref = getSharedPreferences("setting", 0);
                editor = pref.edit();

                int current_p_id = pref.getInt("current_p_id", -1);
                Call<Void> logoutCall = networkService.logout(current_p_id);
                logoutCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Response<Void> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            editor.remove("current_p_id");
                            editor.commit();
                        }

                    }
                    @Override
                    public void onFailure(Throwable t) {
                        Log.i("MyTag", "에러: " + t.getMessage());
                    }
                });


                setResult(9);
                finish();
                return false;

            }
        });


    }




    /*
    로그아웃 클릭했을 때 종료
    '<' 버튼 눌렀을 때 setResult(5);finish();
    Back버튼 눌렀을 때 resultCode=0으로 돌아감!!! '<'버튼 눌렀을 때랑 똑같은 효과 주려면 onStop()에서 setResult(5);finish();

    @Override
    protected void onStop(){//< 버튼 눌렀을 때!!!! 레이아웃 바뀌면 일반 메소드로 바꾸기
        super.onStop();

        setResult(5);
        finish();

    }
    */

}
