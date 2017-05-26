package org.sopt.nawa_103.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import org.sopt.nawa_103.Component.ApplicationController;
import org.sopt.nawa_103.Model.DB.MsgInfo;
import org.sopt.nawa_103.Model.DB.PushSender;
import org.sopt.nawa_103.Model.DB.ScheduleTmp;
import org.sopt.nawa_103.Network.NetworkService;
import org.sopt.nawa_103.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by kimhj on 2016-01-14.
 */public class PushStorageAdapter extends BaseAdapter {

    Context context;
    int layout;
    ArrayList<PushSender> pushlist;
    LayoutInflater inflater;


    public PushStorageAdapter(Context context, int layout, ArrayList<PushSender> pushlist) {
        this.context = context;
        this.layout = layout;
        this.pushlist = pushlist;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return pushlist.size();
    }

    @Override
    public Object getItem(int position) {
        return pushlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long)pushlist.get(position).getPs_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final NetworkService networkService = ApplicationController.getInstance().getNetworkService();

        final int pos = position;

        if(convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }

        ImageButton btn_push_detail = (ImageButton)convertView.findViewById(R.id.btn_push_detail);
       final ImageView iv1_push_item_p = (ImageView)convertView.findViewById(R.id.iv1_push_item_p);
       // ImageView iv1_push_item_m = (ImageView)convertView.findViewById(R.id.iv1_push_item_m);
        TextView tvFrom_push_item = (TextView)convertView.findViewById(R.id.tvFrom_push_item);
        final LinearLayout iv2_push_item = (LinearLayout)convertView.findViewById(R.id.iv2_push_item);
        final TextView tvTitle_push_item = (TextView)convertView.findViewById(R.id.tvTitle_push_item);
        final TextView tvLoc_push_item = (TextView)convertView.findViewById(R.id.tvLoc_push_item);
        final TextView tvPeople_push_item = (TextView)convertView.findViewById(R.id.tvPeople_push_item);
        final TextView tvTime_push_item = (TextView)convertView.findViewById(R.id.tvTime_push_item);
     //   final Space space = (Space)convertView.findViewById(R.id.space);


        if(pushlist.get(pos).getPmcheck() == 0) { //푸시
            iv1_push_item_p.setImageResource(R.mipmap.push_schedule_list);
            tvFrom_push_item.setText(pushlist.get(pos).getName() + "님과의 약속");

            btn_push_detail.setOnClickListener(new View.OnClickListener() { //푸시 아이템 클릭 리스터
                boolean selected = false;


                @Override
                public void onClick(View v) {

                    //NetworkService networkService = ApplicationController.getInstance().getNetworkService();

                    if (!selected) {

                        Call<List<ScheduleTmp>> PushItem = networkService.getInfoScheduleContent(pushlist.get(pos).getS_id());
                        PushItem.enqueue(new Callback<List<ScheduleTmp>>() {
                            @Override
                            public void onResponse(Response<List<ScheduleTmp>> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    List<ScheduleTmp> pushItem = response.body();

                                    String friends = "";
                                    for (int i = 0; i < pushItem.size(); i++) {
                                        friends += pushItem.get(i).name + " ";
                                    }

                                    tvTitle_push_item.setText(pushItem.get(0).title);
                                    tvLoc_push_item.setText(pushItem.get(0).place);
                                    tvPeople_push_item.setText(friends);
                                    tvTime_push_item.setText(pushItem.get(0).year + "/" + pushItem.get(0).month + "/" + pushItem.get(0).date + " "
                                            + pushItem.get(0).hour + ":" + pushItem.get(0).minute);


                                    selected = true;
                                    iv2_push_item.setVisibility(View.VISIBLE);
                                    tvTitle_push_item.setVisibility(View.VISIBLE);
                                    tvLoc_push_item.setVisibility(View.VISIBLE);
                                    tvPeople_push_item.setVisibility(View.VISIBLE);
                                    tvTime_push_item.setVisibility(View.VISIBLE);
                                    //        space.setVisibility(View.VISIBLE);


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

                    } else {
                        selected = false;
                        iv2_push_item.setVisibility(View.GONE);
                        tvTitle_push_item.setVisibility(View.GONE);
                        tvLoc_push_item.setVisibility(View.GONE);
                        tvPeople_push_item.setVisibility(View.GONE);
                        tvTime_push_item.setVisibility(View.GONE);
                        //   space.setVisibility(View.GONE);
                    }
                }
            });


        } else if(pushlist.get(pos).getPmcheck() == 1) { //쪽지
            iv1_push_item_p.setImageResource(R.mipmap.push_send_list);
            tvFrom_push_item.setText(pushlist.get(pos).getName() + "님의 쪽지");
            btn_push_detail.setOnClickListener(new View.OnClickListener() { //쪽지 아이템 클릭 리스너
                boolean selected = false;

                @Override
                public void onClick(View v) {

                    if (!selected) {

                        Call<MsgInfo> MsgItem = networkService.getMsgItem(pushlist.get(pos).getP_id(), pushlist.get(pos).getPush_id());
                        MsgItem.enqueue(new Callback<MsgInfo>() {
                            @Override
                            public void onResponse(Response<MsgInfo> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    MsgInfo msgItem = response.body();

                                    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

                                    //Date date = msgItem.pushtime;
                                    msgItem.pushtime.setHours(msgItem.pushtime.getHours() + 9);

                                    tvTitle_push_item.setText(msgItem.title);
                                    tvLoc_push_item.setText(msgItem.msg);
                                    tvPeople_push_item.setText(pushlist.get(pos).getName());
                                    tvTime_push_item.setText(transFormat.format(msgItem.pushtime));

                                    Log.i("mytag", "쪽지 상세 " + msgItem.title + " " + msgItem.msg + " " + transFormat.format(msgItem.pushtime));

                                    selected = true;
                                    iv2_push_item.setVisibility(View.VISIBLE);
                                    tvTitle_push_item.setVisibility(View.VISIBLE);
                                    tvLoc_push_item.setVisibility(View.VISIBLE);
                                    tvPeople_push_item.setVisibility(View.VISIBLE);
                                    tvTime_push_item.setVisibility(View.VISIBLE);
                                    //   space.setVisibility(View.VISIBLE);

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

                    } else {
                        selected = false;
                        iv2_push_item.setVisibility(View.GONE);
                        tvTitle_push_item.setVisibility(View.GONE);
                        tvLoc_push_item.setVisibility(View.GONE);
                        tvPeople_push_item.setVisibility(View.GONE);
                        tvTime_push_item.setVisibility(View.GONE);
                        // space.setVisibility(View.GONE);
                    }

                }
            });
        }


        //뷰 다 안보이게 설정
        iv2_push_item.setVisibility(View.GONE);
        tvTitle_push_item.setVisibility(View.GONE);
        tvLoc_push_item.setVisibility(View.GONE);
        tvPeople_push_item.setVisibility(View.GONE);
        tvTime_push_item.setVisibility(View.GONE);
    //    space.setVisibility(View.GONE);





        return convertView;
    }

}