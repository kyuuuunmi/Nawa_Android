package org.sopt.nawa_103.Adapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.sopt.nawa_103.Activity.CalendarActivity;
import org.sopt.nawa_103.Activity.DetailDialogActivity;
import org.sopt.nawa_103.Activity.UpdateActivity;
import org.sopt.nawa_103.Component.ApplicationController;
import org.sopt.nawa_103.Model.DB.ScheduleContent;
import org.sopt.nawa_103.Model.DB.UnderScheduleContent;
import org.sopt.nawa_103.Network.NetworkService;
import org.sopt.nawa_103.R;

import java.util.Calendar;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by jiyeon on 2016-02-24.
 */


public class ViewPagerAdapter extends PagerAdapter {

    LayoutInflater inflater;
    FrameLayout frameLayout;
    TextView under_tv_todo, under_tv_location, under_tv_friend, under_tv_time;
    ImageView under_icon_delete, under_icon_edit;
    NetworkService networkService;
    SharedPreferences preferences;
    List<UnderScheduleContent> under_schedulecontent_list;
    int current_p_id=0;
    CalendarActivity tmpContext;


    public ViewPagerAdapter(LayoutInflater inflater) {
        initNetworkService();
        this.inflater=inflater;
    }
    private void initNetworkService() {
        networkService = ApplicationController.getInstance().getNetworkService();
    }

    public void setUnder_schedulecontent_list( List<UnderScheduleContent> under_schedulecontent_list){
        this.under_schedulecontent_list=under_schedulecontent_list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        int count=1;

        if(under_schedulecontent_list!=null){
            if(under_schedulecontent_list.size()==0)
                count=1;
            else
                count=under_schedulecontent_list.size();
        }
        else if(under_schedulecontent_list == null){
            count=1;
        }
        return count;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View view=null;
        view= inflater.inflate(R.layout.calendar_view_pager, null);

        tmpContext=(CalendarActivity) inflater.getContext();
        frameLayout = (FrameLayout) view.findViewById(R.id.under_view_frame);
        under_icon_delete = (ImageView) view.findViewById(R.id.under_delete_icon);
        under_icon_edit = (ImageView) view.findViewById(R.id.under_edit_icon);
        under_tv_todo = (TextView) view.findViewById(R.id.under_textview_todo);
        under_tv_friend = (TextView) view.findViewById(R.id.under_textview_friend);
        under_tv_location = (TextView) view.findViewById(R.id.under_textview_location);
        under_tv_time = (TextView) view.findViewById(R.id.under_textview_time);

        if(under_schedulecontent_list!=null) {

            if(under_schedulecontent_list.size()==0){
                setUnderViewNull();

            }else {
                setUnderTextView(under_schedulecontent_list.get(position).title, under_schedulecontent_list.get(position).place, under_schedulecontent_list.get(position).friends, String.valueOf(under_schedulecontent_list.get(position).hour + ":" + under_schedulecontent_list.get(position).minute + under_schedulecontent_list.get(position).amorpm));
            }
        }else if (under_schedulecontent_list==null)
        {
            setUnderViewNull();

        }
        container.addView(view);

        under_icon_edit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    under_icon_edit.setBackgroundResource(R.mipmap.edit_icon_click);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    under_icon_edit.setBackgroundResource(R.mipmap.edit_icon_unclick);
                }
                Intent intent = new Intent(inflater.getContext(), UpdateActivity.class);
                intent.putExtra("current_s_id", under_schedulecontent_list.get(position).s_id);
                inflater.getContext().startActivity(intent);
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

                Call<ScheduleContent> scheduleContentCall = networkService.deleteScheduleContent(current_p_id, under_schedulecontent_list.get(position).s_id);
                scheduleContentCall.enqueue(new Callback<ScheduleContent>() {
                    @Override
                    public void onResponse(Response<ScheduleContent> response, Retrofit retrofit) {
                        if (response.isSuccess()) {

                            tmpContext.initDB(tmpContext.selected_day_calendar.get(Calendar.MONTH) + 1, tmpContext.selected_day_calendar.get(Calendar.YEAR));
                            tmpContext.setListener();
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
                Intent intent_detail = new Intent(inflater.getContext(), DetailDialogActivity.class);
                intent_detail.putExtra("current_s_id",  under_schedulecontent_list.get(position).s_id);
                Log.i("MyTagCalendar", "" +  under_schedulecontent_list.get(position).s_id);
                inflater.getContext().startActivity(intent_detail);
            }
        });
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View v, Object obj) {

        return v==obj;
    }
    public void setUnderViewNull() {
        under_tv_todo.setText("");
        under_tv_location.setText("");
        under_tv_friend.setText("");
        under_tv_time.setText("");
        under_icon_delete.setVisibility(View.GONE);
        under_icon_edit.setVisibility(View.GONE);
        frameLayout.setClickable(false);
    }

    public void setUnderTextView(String todo, String location, String friend, String time) {
        under_tv_todo.setText(todo);
        under_tv_location.setText(location);
        under_tv_friend.setText(friend);
        under_tv_time.setText(time);
        under_icon_delete.setVisibility(View.VISIBLE);
        under_icon_edit.setVisibility(View.VISIBLE);
        frameLayout.setClickable(true);
    }

}
