package org.sopt.nawa_103.Activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.sopt.nawa_103.Component.ApplicationController;
import org.sopt.nawa_103.Model.DB.MemberContent;
import org.sopt.nawa_103.Network.NetworkService;
import org.sopt.nawa_103.R;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by USER on 2016-01-10.
 */
public class JoinDialog extends Dialog {

    private NetworkService networkService;
    private EditText editID, editPwd, editName;
    private TextView txtPhone;
    private Button btnSubmit;
    private ImageButton btnCheck;
    private Context ctx;
    private boolean checkID; //ID 중복확인 해서 사용 가능한지 확인하기 전까지는 다이얼로그 dismiss 되지 않게 할 변수!!!
    private String formattedNum = "";

    public JoinDialog(Context context) {
        super(context);
        this.ctx = context;
        this.checkID = false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_join);

        initNetworkService();
        initView();

    }

    public void getPhoneNum(String formattedNum) {
        this.formattedNum = formattedNum;

    }

    private void initNetworkService() {
        networkService = ApplicationController.getInstance().getNetworkService();

    }

    private void initView() {
        editID = (EditText) findViewById(R.id.join_txtId);
        editPwd = (EditText) findViewById(R.id.join_txtPw);
        editName = (EditText) findViewById(R.id.join_txtName);

        txtPhone = (TextView) findViewById(R.id.join_txtPhone);
        txtPhone.setText(formattedNum);

        btnSubmit = (Button) findViewById(R.id.join_btnJoin);
        btnCheck = (ImageButton) findViewById(R.id.join_btnCheck);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkID == false) {
                    Toast.makeText(ctx, "사용 가능한 ID인지 확인하세요", Toast.LENGTH_SHORT).show();
                } else {
                    //이름, 패스워드가 빈칸이 아닌지 확인하고
                    MemberContent tempContent = new MemberContent();
                    tempContent.user_id = editID.getText().toString();
                    tempContent.pw = editPwd.getText().toString();
                    tempContent.name = editName.getText().toString();
                    tempContent.phone = txtPhone.getText().toString();
                    Call<MemberContent> membershipCall = networkService.joinMemberContent(tempContent);
                    membershipCall.enqueue(new Callback<MemberContent>() {

                        @Override
                        public void onResponse(Response<MemberContent> response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                Toast.makeText(ctx, "환영합니다", Toast.LENGTH_SHORT).show();
                                dismiss();//dismiss 여기서 하면 안됨!!editText가 다 빈칸이 아닌지 확인하고, 비밀번호도 체크, 아이디도 체크한 뒤에 가입 완료 ㄱ
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
            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempId = editID.getText().toString();
                Call<Void> checkIdCall = networkService.joinIdCheck(tempId);
                checkIdCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Response<Void> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            Toast.makeText(ctx, "사용 가능한 ID입니다.", Toast.LENGTH_SHORT).show();
                            checkID = true;//빈칸이 하나도 없는지 확인하고 나서!!!!!!!!!!!
                        } else {
                            Toast.makeText(ctx, "이미 사용중인 ID입니다.", Toast.LENGTH_SHORT).show();
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

}