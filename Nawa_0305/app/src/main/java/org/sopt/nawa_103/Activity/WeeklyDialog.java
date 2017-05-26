package org.sopt.nawa_103.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.sopt.nawa_103.Adapter.DialogAdapter;
import org.sopt.nawa_103.Component.ApplicationController;
import org.sopt.nawa_103.Model.DB.Friend;
import org.sopt.nawa_103.Model.DB.MsgModel;
import org.sopt.nawa_103.Model.Element.DialogItem;
import org.sopt.nawa_103.Network.NetworkService;
import org.sopt.nawa_103.R;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by yunmi on 2016-01-11.
 */
public class WeeklyDialog extends Dialog {

    private ArrayList<DialogItem> itemDatas = null;
    ListView dialog_list;
    DialogAdapter adapter;
    Context context;
    TextView textAlert;
    EditText edit_message;
    ImageButton btnCancel;
    ImageButton btnOk;
    int position;
    List<Friend> friendList;

    public int[] p_id_array={1,2,3};

    SharedPreferences preference;
    int current_p_id = 0;

    int current_s_id = 0;

    MsgModel msgModel;

    DialogItem[] items;

    NetworkService networkService;

    public WeeklyDialog(Context context, int position, List<Friend> friendList, int s_id) {
        super(context);
        this.context = context;
        this.position = position;
        this.friendList=friendList;
        this.current_s_id = s_id;
    }


    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.dialog_weekly);

        preference = context.getSharedPreferences("setting",0);
        current_p_id= preference.getInt("current_p_id",-1);


        p_id_array = new int[0];
        Init();
        aboutView();
        btnEvnt();


    }

    private void btnEvnt() {
        btnOk.setBackgroundResource(R.mipmap.message_sendok_click);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  DialogItem[] str = adapter.getCheckedItems();
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < str.length; i++)
                    if(str[i]!=null)
                        stringBuffer.append(str[i].getName_dialog());
                Toast.makeText(context, stringBuffer, Toast.LENGTH_SHORT).show();
*/

                //// TODO: 2016-02-24  친구리스트 보내서 쪽지보내기~~
                //String msg = edit_message.getText().toString();

                msgModel.s_id = current_s_id;
                msgModel.p_id = current_p_id;
                msgModel.message = edit_message.getText().toString();

                DialogItem[] items = adapter.getCheckedItems();
                msgModel.p_list = new ArrayList<Integer>();
                String tmp = "받는이 리스트: ";
                for(int i=0; i<items.length; i++) {
                    msgModel.p_list.add(items[i].getP_id());
                    tmp += items[i].getP_id() + " ";
                }
                Log.i("mytag", "쪽지 전송! s_id: " + msgModel.s_id + " p_id: " + msgModel.p_id + " msg: " + msgModel.message);
                Log.i("mytag",tmp);

                networkService = ApplicationController.getInstance().getNetworkService();
                Call<MsgModel> msgModelCall = networkService.sendMsg(msgModel);
                msgModelCall.enqueue(new Callback<MsgModel>() {
                    @Override
                    public void onResponse(Response<MsgModel> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            Toast.makeText(context, "쪽지가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            int statusCode = response.code();
                            Log.i("MyTag", "쪽지 응답코드: " + statusCode);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i("MyTag", "쪽지 에러내용: " + t.getMessage());
                    }
                });




                /*
                DialogItem[] items = adapter.getCheckedItems();
                */
                dismiss();
            }
        });
        btnOk.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    btnOk.setBackgroundResource(R.mipmap.message_sendok_click);
                if (event.getAction() == MotionEvent.ACTION_UP)
                    btnOk.setBackgroundResource(R.mipmap.message_sendok_unclick);
                return false;
            }
        });
        btnCancel.setBackgroundResource(R.mipmap.message_sendn_click);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "cancel", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        btnCancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    btnCancel.setBackgroundResource(R.mipmap.message_sendn_click);
                if (event.getAction() == MotionEvent.ACTION_UP)
                    btnCancel.setBackgroundResource(R.mipmap.message_sendn_unclick);
                return false;
            }
        });
    }

    private void aboutView() {

        for(int i=0;i<friendList.size();i++){
            DialogItem dialogItem = new DialogItem(friendList.get(i).name, friendList.get(i).p_id);
            itemDatas.add(dialogItem);
        }

    }

    private void Init() {

        dialog_list = (ListView) findViewById(R.id.dialog_list);
        btnCancel = (ImageButton) findViewById(R.id.dialog_cancel);
        btnOk = (ImageButton) findViewById(R.id.dialog_ok);

        edit_message = (EditText) findViewById(R.id.edit_message);
        itemDatas = new ArrayList<>();
        adapter = new DialogAdapter(itemDatas, context);

        dialog_list.setAdapter(adapter);

        msgModel = new MsgModel();
        items = adapter.getCheckedItems();
        msgModel.p_list = new ArrayList<Integer>();

    }

}
