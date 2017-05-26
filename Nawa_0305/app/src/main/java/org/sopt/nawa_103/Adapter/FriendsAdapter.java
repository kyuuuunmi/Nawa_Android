package org.sopt.nawa_103.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.sopt.nawa_103.Model.DB.Contacts;
import org.sopt.nawa_103.R;

import java.util.ArrayList;

/**
 * Created by USER on 2016-01-11.
 */
public class FriendsAdapter extends BaseAdapter {
    private ArrayList<Contacts> myContact;
    private LayoutInflater inflater;
    private ArrayList<Contacts> checkedItems;
    private Contacts[] checkeditem_array;

    public ArrayList<Contacts> getCheckedItems() {
        return checkedItems;
    }

    public Contacts[] getCheckeditem_array() {
        return checkeditem_array;
    }

    public void setCheckedItems(ArrayList<Contacts> checkedItems) {
        this.checkedItems = checkedItems;
    }

    public void removeCheckedItems(Contacts item) {
        checkedItems.remove(item);

        for (int i = 0; i < checkeditem_array.length; i++) {
            if (checkeditem_array[i] == item) {
                checkeditem_array[i] = null;
                myContact.get(i).setChecked(false);

                return;
            }
        }
    }

    public ArrayList<Contacts> getMyContact() {
        return myContact;
    }

    public void setMyContact(ArrayList<Contacts> myContact) {
        this.myContact = myContact;
    }

    private SelectedFriendsAdapter checkAdaper;

    public FriendsAdapter(Context context, ArrayList<Contacts> myContact, int listSize, SelectedFriendsAdapter checkAdapter) {
        this.myContact = myContact;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        checkedItems = new ArrayList<Contacts>();
        this.checkAdaper = checkAdapter;
        this.checkeditem_array = new Contacts[listSize];
    }


    @Override
    public int getCount() {
        return myContact != null ? myContact.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return (myContact != null && position >= 0 && position < myContact.size())
                ? myContact.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return (myContact != null && position >= 0 && position < myContact.size())
                ? position : 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_friends, parent, false);
            holder = new Holder();
            holder.myContactCheck = (CheckBox) convertView.findViewById(R.id.mCheckBox);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }


        final Contacts item = myContact.get(position);
        holder.myContactCheck.setText(item.getName());

        holder.myContactCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    item.setChecked(true);
                    checkedItems.add(item);
                    checkeditem_array[position] = item;
                    if (!checkAdaper.getSelectContacts().contains(item)) {
                        checkAdaper.getSelectContacts().add(item);
                    }
                    checkAdaper.notifyDataSetChanged();

                } else {
                    if (checkeditem_array[position] != null) {
                        item.setChecked(false);
                        checkedItems.remove(item);
                        checkeditem_array[position] = null;

                        if (checkAdaper.getSelectContacts().contains(item))
                            checkAdaper.getSelectContacts().remove(item);
                        checkAdaper.notifyDataSetChanged();

                    }
                }
            }
        });

        if (item.isChecked()) {
            holder.myContactCheck.setChecked(true);

        } else holder.myContactCheck.setChecked(false);


        setChecked(position, convertView);
        return convertView;
    }

    private void setChecked(final int position, View convertview) {

        final Contacts item = myContact.get(position);
        final CheckBox cb = (CheckBox) convertview.findViewById(R.id.mCheckBox);

        if (item.isChecked()) {
            cb.setChecked(true);
            checkedItems.add(item);
            if (!checkAdaper.getSelectContacts().contains(item)) {
                checkAdaper.getSelectContacts().add(item);
            }
            checkAdaper.notifyDataSetChanged();
        }
        else {
            cb.setChecked(false);

        }

    }


    class Holder {

        CheckBox myContactCheck;

        public CheckBox getCheckName() {
            return myContactCheck;
        }

        public void setCheckName(CheckBox checkName) {
            this.myContactCheck = checkName;
        }

    }

}
