package org.sopt.nawa_103.Activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;

import org.lucasr.twowayview.TwoWayView;
import org.sopt.nawa_103.Adapter.FriendsAdapter;
import org.sopt.nawa_103.Adapter.SelectedFriendsAdapter;
import org.sopt.nawa_103.Model.DB.Contacts;
import org.sopt.nawa_103.R;

import java.util.ArrayList;

/**
 * Created by USER on 2016-01-11.
 */

public class FriendsActivity extends Activity {

    private ArrayList<Contacts> myContact, mySelectContact;
    private FriendsAdapter myAdapter;
    private SelectedFriendsAdapter mySelectAdapter;
    private ListView myListView;
    private TwoWayView mySelectListView;
    private ImageButton selectBtn;
    private Cursor c;
    private LinearLayout btnBack;
    private AutoCompleteTextView autoTextView;
    private ArrayAdapter<String> autoAdapter;
    private String[] tempContact;
    ArrayList<String>  exist_friend_list = new ArrayList<String>();
    InputMethodManager imm;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        initView();
        loadAutoTextView();

        selectBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Contacts[] temp = myAdapter.getCheckeditem_array(); //선택된 이름
                int size = mySelectContact.size();
                int j = 0;
                String[] selectedName=new String[size];
                String[] selectedNum = new String[size];
                for (int i = 0; i < temp.length; i++) { //전체 연락처 크기
                    if (myContact.get(i).isChecked()) { //체크된 연락처
                        c.moveToPosition(i);
                        String cID = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                        Cursor cur = getBaseContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{cID}, null);
                        cur.moveToFirst();
                        selectedName[j] = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String tempNum = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        selectedNum[j] = PhoneNumberUtils.formatNumber(tempNum, "KR");
                        j++;
                        //PhoneNumberUtils.formatNumber(tempNum);
                        cur.close();
                    }
                }

                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putStringArray("selectedName", selectedName);
                b.putStringArray("selectedNum", selectedNum);
                intent.putExtra("items", b);
                setResult(1, intent);
                finish();


            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(5);
                mySelectContact.clear();
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (exist_friend_list != null) {
            exist_loadContacts();
            mySelectAdapter.setSelectContacts(mySelectContact);
        }
    }

    private void initView() {

        btnBack = (LinearLayout) findViewById(R.id.friends_toolbar_btnBack);
        mySelectListView = (TwoWayView) findViewById(R.id.friends_checked_listView);
        autoTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        selectBtn = (ImageButton) findViewById(R.id.friends_toolbar_btnSubmit);
        myListView = (ListView) findViewById(R.id.friends_listView);

        myContact = new ArrayList<Contacts>();
        mySelectContact = new ArrayList<Contacts>();
        mySelectAdapter = new SelectedFriendsAdapter(getApplicationContext(), mySelectContact);
        mySelectListView.setAdapter(mySelectAdapter);

        loadContacts();
        myAdapter = new FriendsAdapter(getApplicationContext(), myContact, myContact.size(), mySelectAdapter);
        myListView.setAdapter(myAdapter);

        mySelectAdapter.setFriendsAdapter(myAdapter);
        exist_friend_list=getIntent().getStringArrayListExtra("exist_friend_list");


    }


    private void loadContacts() {
        ContentResolver cr = getContentResolver();
        c = cr.query(ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME},
                null, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");

        c.moveToFirst();
        int index = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        do {
            myContact.add(new Contacts(c.getString(index)));

        } while (c.moveToNext());
    }

    private void exist_loadContacts() {

        String tempPhone=null; String tempPhoneNumber=null;

        for (int i = 0; i <  myContact.size(); i++) {
            c.moveToPosition(i);
            String cID = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
            Cursor cur = getBaseContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{cID}, null);

            if(cur.getCount()==0) {
                break;
            }
                cur.moveToFirst();

                tempPhone = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                tempPhoneNumber = PhoneNumberUtils.formatNumber(tempPhone, "KR");
                for (int j = 0; j < exist_friend_list.size(); j++) {
                    if (exist_friend_list.get(j).equals(tempPhoneNumber)) {
                        mySelectContact.add(myContact.get(i));
                        myContact.get(i).setChecked(true);
                        myListView.setSelection(i);
                        myListView.requestFocus(i);
                        mySelectAdapter.notifyDataSetChanged();
                        myAdapter.notifyDataSetChanged();
                    }
                }

             cur.close();
    }
       /* mySelectAdapter.setSelectContacts(mySelectContact);
        mySelectAdapter.notifyDataSetChanged();*/
    }
    private void loadAutoTextView() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(autoTextView.getWindowToken(), 0);


        tempContact = new String[myContact.size()];
        for (int i = 0; i < myContact.size(); i++) {
            tempContact[i] = myContact.get(i).getName();
        }
        autoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, tempContact);
        autoTextView.setAdapter(autoAdapter);
        autoTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imm.hideSoftInputFromWindow(autoTextView.getWindowToken(), 0);
                String selectedItem = autoTextView.getText().toString();

                for (int i = 0; i < myContact.size(); i++) {
                    if (selectedItem.equals(myContact.get(i).getName())) {
                        myContact.get(i).setChecked(!myContact.get(i).isChecked());
                        myListView.requestFocusFromTouch();
                        myListView.setSelection(i);
                        myAdapter.notifyDataSetChanged();
                        break;
                    }
                }

                autoTextView.setText("");

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        setResult(5);
        Log.i("MyTag", "onPause");

        finish();

    }

    @Override
    protected void onStop() {
        super.onStop();
        setResult(5);
        Log.i("MyTag", "onStop");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(5);
        Log.i("MyTag", "onDestroy");


        c.close();

    }

}
