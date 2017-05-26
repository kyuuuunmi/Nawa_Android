package org.sopt.nawa_103.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.sopt.nawa_103.Model.Element.DialogItem;
import org.sopt.nawa_103.Model.Element.DialogViewHolder;
import org.sopt.nawa_103.R;

import java.util.ArrayList;

/**
 * Created by yunmi on 2016-01-11.
 */
public class DialogAdapter extends BaseAdapter {

    private ArrayList<DialogItem> itemDatas = null;
    private LayoutInflater layoutInflater = null;
    private DialogItem checkedItems[] = new DialogItem[8];

    public DialogAdapter(ArrayList<DialogItem> itemDatas, Context ctx) {

        this.itemDatas = itemDatas;
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public DialogItem[] getCheckedItems() {
        int size=0;
        for(int i=0;i<checkedItems.length;i++){
            if(checkedItems[i]!=null){
                size++;
            }
        }
        DialogItem finalitems[] = new DialogItem[size];
        int j=0;
        for(int i=0;i<checkedItems.length;i++){
            if(checkedItems[i]!=null){
                finalitems[j]=checkedItems[i];
                j++;
            }
        }
        return finalitems;
        //return checkedItems;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        DialogViewHolder viewHolder = new DialogViewHolder();

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_dialog_weekly, parent, false);

            viewHolder.setCheckName((CheckBox) convertView.findViewById(R.id.checkName));

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (DialogViewHolder) convertView.getTag();
        }

        final DialogItem itemData_temp = itemDatas.get(position);

        viewHolder.getCheckName().setText(itemData_temp.getName_dialog());
        viewHolder.getCheckName().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    checkedItems[position] = new DialogItem(itemData_temp.getName_dialog(), itemData_temp.getP_id());
                else
                if(checkedItems[position] != null)
                    checkedItems[position] = null;


            }
        });

        return convertView;
    }
}
