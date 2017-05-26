package org.sopt.nawa_103.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sopt.nawa_103.Activity.WeeklyDialog;
import org.sopt.nawa_103.Model.DB.Data;
import org.sopt.nawa_103.Model.Element.ItemData;
import org.sopt.nawa_103.Model.Element.ViewHolder;
import org.sopt.nawa_103.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by yunmi on 2015-11-04.
 */
public class WeeklyAdapter extends BaseAdapter {


    private ArrayList<ItemData> itemDatas = null;
    private LayoutInflater layoutInflater = null;
    private Activity activity;
    int[] p_id_array;

    public WeeklyDialog weeklyDialog;

    public WeeklyDialog getWeeklyDialog() {
        return weeklyDialog;
    }


    public WeeklyAdapter(ArrayList<ItemData> itemDatas, Context ctx, Activity activity) {
        this.itemDatas = itemDatas;
        this.activity = activity;
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void getp_id(int[] p_id_ary) {
        p_id_array = p_id_ary;
    }

    @Override
    public int getCount() {
        return itemDatas != null ? itemDatas.size() : 0;
    }


    @Override
    public Object getItem(int position) {
        return (itemDatas != null && position >= 0 && position < itemDatas.size())
                ? itemDatas.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return (itemDatas != null && position >= 0 && position < itemDatas.size())
                ? position : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_weekly, parent, false);

            viewHolder.setTextDay_item((TextView) convertView.findViewById(R.id.textDay));
            viewHolder.setTextDate_item((TextView) convertView.findViewById(R.id.textDate));
            viewHolder.setTextTodo_item((TextView) convertView.findViewById(R.id.textToDo));
            viewHolder.setTextName_item((TextView) convertView.findViewById(R.id.textName));
            viewHolder.setBtnMsg_item((ImageButton) convertView.findViewById(R.id.btnMsg));
            viewHolder.setLinear_item((LinearLayout) convertView.findViewById(R.id.list_item));

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ItemData itemData_temp = itemDatas.get(position);

        viewHolder.getTextDay_item().setText(itemData_temp.getDay());
        viewHolder.getTextDate_item().setText("" + itemData_temp.getDate());


        Log.i("MyTag", "" + ((Data) itemData_temp.getData()).getTodo());

        if (((Data) itemData_temp.getData()).getTodo()!="") {
            viewHolder.getTextTodo_item().setText(((Data) itemData_temp.getData()).getTodo());
            viewHolder.getTextName_item().setText(((Data) itemData_temp.getData()).getName());
        }
        else {
            viewHolder.getTextTodo_item().setText("");
            viewHolder.getTextName_item().setText("");
        }

        setBg(position, convertView);
        btnEvnt(position, convertView);
/*
        viewHolder.getBtnMsg_item().setBackgroundResource(itemData_temp.getBg);*/

        return convertView;
    }

    private void setBg(int position, View convertView) {

        ItemData itemData = itemDatas.get(position);

        LinearLayout bg = (LinearLayout) convertView.findViewById(R.id.list_item);
        TextView txtDay = (TextView) convertView.findViewById(R.id.textDay);
        TextView txtDate = (TextView) convertView.findViewById(R.id.textDate);

        ImageButton btnSend = (ImageButton) convertView.findViewById(R.id.btnMsg);


        if (itemData.getMonth() == Calendar.getInstance().get(Calendar.MONTH)
                && itemData.getDate() == Calendar.getInstance().get(Calendar.DATE)) {
            if (((Data)itemData.getData()).getName()!="") {
                bg.setBackgroundResource(R.mipmap.message_today);
                btnSend.setVisibility(View.VISIBLE);
            } else {
                btnSend.setVisibility(View.INVISIBLE);
                bg.setBackgroundResource(R.mipmap.message_today_untodo);
            }
            txtDay.setTextColor(Color.parseColor("#ffffff"));
            txtDate.setTextColor(Color.parseColor("#ffffff"));
        } else {
            // cal != today
            if (((Data) itemData.getData()).getName()!="") {
                btnSend.setVisibility(View.VISIBLE);
                bg.setBackgroundResource(R.mipmap.message_white_todo);
            } else {
                btnSend.setVisibility(View.INVISIBLE);
                bg.setBackgroundResource(R.mipmap.message_white);
            }

            txtDay.setTextColor(Color.parseColor("#527B8F"));
            txtDate.setTextColor(Color.parseColor("#527B8F"));
        }


    }


    public void btnEvnt(final int position, final View convertView) {

        // TODO: 2016-01-15  친구리스트를 Dialog에 보냄 (생성자로)
        //
        final ItemData itemData = itemDatas.get(position);

        final ImageButton btnMsg = (ImageButton) convertView.findViewById(R.id.btnMsg);

        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //푸시 또는 쪽지

                WeeklyDialog weeklyDialog = new WeeklyDialog(activity, position, itemData.getFriendList(), ((Data)(itemData.getData())).getCurrent_s_id());
                weeklyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                weeklyDialog.show();


            }
        });
        if (itemData.getMonth() == Calendar.getInstance().get(Calendar.MONTH)
                && itemData.getDate() == Calendar.getInstance().get(Calendar.DATE)) {
            btnMsg.setBackgroundResource(R.mipmap.message_todayicon_unclick);
            btnMsg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        btnMsg.setBackgroundResource(R.mipmap.message_todayicon_click);
                    if (event.getAction() == MotionEvent.ACTION_UP)
                        btnMsg.setBackgroundResource(R.mipmap.message_todayicon_unclick);
                    return false;
                }

            });
        } else {
            btnMsg.setBackgroundResource(R.mipmap.message_icon_unclick);
            btnMsg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        btnMsg.setBackgroundResource(R.mipmap.message_icon_click);
                    if (event.getAction() == MotionEvent.ACTION_UP)
                        btnMsg.setBackgroundResource(R.mipmap.message_icon_unclick);
                    return false;
                }

            });
        }

    }
}