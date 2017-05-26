package org.sopt.nawa_103.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.sopt.nawa_103.Model.DB.Contacts;
import org.sopt.nawa_103.R;

import java.util.ArrayList;

/**
 * Created by USER on 2016-01-12.
 */
public class SelectedFriendsAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private FriendsAdapter friendsAdapter;
    private ArrayList<Contacts> selectContacts;

    public FriendsAdapter getFriendsAdapter() {
        return friendsAdapter;
    }

    public void setFriendsAdapter(FriendsAdapter friendsAdapter) {
        this.friendsAdapter = friendsAdapter;
    }


    public ArrayList<Contacts> getSelectContacts() {
        return selectContacts;
    }

    public void setSelectContacts(ArrayList<Contacts> selectContacts) {
        this.selectContacts = selectContacts;
    }

    private Contacts[] checkItems;

    public Contacts[] getCheckItems() {
        return checkItems;
    }

    public void setCheckItems(Contacts[] checkItems) {
        this.checkItems = checkItems;
    }


    public SelectedFriendsAdapter(Context context, ArrayList<Contacts> myContact) {
        //super();
        this.selectContacts = myContact;
        this.context = context;
    }

    @Override
    public int getCount() {
        return selectContacts != null ? selectContacts.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return (selectContacts != null && position >= 0 && position < selectContacts.size())
                ? selectContacts.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return (selectContacts != null && position >= 0 && position < selectContacts.size())
                ? position : 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_friends_checked, parent, false);
            holder = new Holder();
            holder.selectName = (TextView) convertView.findViewById(R.id.selectedName);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final Contacts item = selectContacts.get(position);
        holder.selectName.setText(item.getName());
        event(position, convertView);


        return convertView;
    }

    private void event(final int position, View convertview) {
        TextView textView = (TextView) convertview.findViewById(R.id.selectedName);

        final Contacts item = selectContacts.get(position);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendsAdapter.removeCheckedItems(item);
                friendsAdapter.notifyDataSetChanged();
                selectContacts.remove(item);
                notifyDataSetChanged();
            }
        });

    }


    class Holder {
        TextView selectName;
    }

}
