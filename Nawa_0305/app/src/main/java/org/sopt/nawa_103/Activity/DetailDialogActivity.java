package org.sopt.nawa_103.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.sopt.nawa_103.Component.ApplicationController;
import org.sopt.nawa_103.Model.DB.Friend;
import org.sopt.nawa_103.Model.DB.ScheduleContent;
import org.sopt.nawa_103.Model.DB.ScheduleTmp;
import org.sopt.nawa_103.Model.Element.MapApiConst;
import org.sopt.nawa_103.Network.NetworkService;
import org.sopt.nawa_103.R;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class DetailDialogActivity extends FragmentActivity implements MapView.MapViewEventListener {
    private double Latitude = 0;
    private double Longitude = 0;
    private int current_s_id = 0;
    private int current_p_id = 0;

    private TextView textViewtitle;
    private TextView textViewfriends;
    private TextView textViewtime;
    private TextView textViewlocation;
    private TextView textViewaddress;
    private TextView textViewcallnumber;

    private ImageButton imageButtondelete;
    private ImageButton imageButtonedit;
    private ImageButton imageButtoncall;

    private MapView mapview;
    private NetworkService networkService;

    List<ScheduleTmp> current_scheduletmp_list;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_dialog);
        initNetworkService();
        initView();

        Intent intent1 = getIntent();
        current_s_id = intent1.getIntExtra("current_s_id", -1);
        preferences = getSharedPreferences("setting", 0);
        current_p_id = preferences.getInt("current_p_id", -1);

        Log.i("MyTagDetailS", "" + current_s_id);
        Log.i("MyTagDetailP", "" + current_p_id);


        Call<List<ScheduleTmp>> InfoScheduleContent = networkService.getInfoScheduleContent(current_s_id);

        InfoScheduleContent.enqueue(new Callback<List<ScheduleTmp>>() {
            @Override
            public void onResponse(Response<List<ScheduleTmp>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    current_scheduletmp_list = response.body();
                    String friends = "";
                    ArrayList<Friend> tmpFriend = new ArrayList<Friend>();

                    for (int i = 0; i < current_scheduletmp_list.size(); i++) {
                        if (current_scheduletmp_list.get(i).p_id != current_p_id) {
                            tmpFriend.add(new Friend(current_scheduletmp_list.get(i).name, current_scheduletmp_list.get(i).phone));
                            friends += current_scheduletmp_list.get(i).name + " ";
                        }
                    }

                    textViewtitle.setText(current_scheduletmp_list.get(0).title);
                    textViewfriends.setText(friends);
                    textViewlocation.setText(current_scheduletmp_list.get(0).place);
                    textViewaddress.setText(current_scheduletmp_list.get(0).place_address);
                    Log.i("MyTagaddress", current_scheduletmp_list.get(0).place_address);
                    textViewcallnumber.setText(current_scheduletmp_list.get(0).place_tel);
                    Log.i("MyTagplace_tel", current_scheduletmp_list.get(0).place_tel);
                    textViewtime.setText(current_scheduletmp_list.get(0).hour + " : " + current_scheduletmp_list.get(0).minute + current_scheduletmp_list.get(0).amorpm);
                    Log.i("MyTagplacehour", Integer.toString(current_scheduletmp_list.get(0).hour));

                    Latitude = current_scheduletmp_list.get(0).place_latitude;
                    Longitude = current_scheduletmp_list.get(0).place_longitude;

                } else {
                    Log.i("MyTag", "응답코드: " + response.code());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("MyTag", "에러: " + t.getMessage());
            }
        });

        mapview.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mapview.setMapViewEventListener(this);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyCurrentLoctionListener locationListener = new MyCurrentLoctionListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);

        imageButtoncall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + current_scheduletmp_list.get(0).place_tel));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                intent_call.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_call);
            }
        });

        imageButtondelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ScheduleContent> scheduleContentCall = networkService.deleteScheduleContent(current_p_id,current_s_id);
                scheduleContentCall.enqueue(new Callback<ScheduleContent>() {
                    @Override
                    public void onResponse(Response<ScheduleContent> response, Retrofit retrofit) {
                        if (response.isSuccess()) {
                            //finish();
                            //onResume();
                            Intent intent_delete = new Intent(getApplicationContext(),CalendarActivity.class);
                            Log.i("MyTag", "삭제성공 ");
                            finish();
                            startActivity(intent_delete);
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
        });

        imageButtonedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_edit = new Intent(getApplicationContext(),UpdateActivity.class);
                intent_edit.putExtra("current_s_id", current_s_id);
                startActivity(intent_edit);
            }
        });

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

        mapview.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(Latitude, Longitude), 2, true);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("Default Marker");
        marker.setTag(0);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Latitude, Longitude);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    public void initView()
    {
        mapview = (MapView) findViewById(R.id.map);
        textViewcallnumber = (TextView) findViewById(R.id.dialog_textview_callnumber);
        textViewtitle = (TextView) findViewById(R.id.dialog_textview_todo);
        textViewfriends = (TextView) findViewById(R.id.dialog_textview_friend);
        textViewtime = (TextView) findViewById(R.id.dialog_textview_time);
        textViewlocation = (TextView) findViewById(R.id.dialog_textview_location);
        textViewaddress = (TextView) findViewById(R.id.dialog_textview_address);
        imageButtondelete = (ImageButton) findViewById(R.id.dialog_imagebtn_delete);
        imageButtonedit = (ImageButton) findViewById(R.id.dialog_imagebtn_edit);
        imageButtoncall = (ImageButton) findViewById(R.id.dialog_imagebtn_call);

    }
    private void initNetworkService() {
        networkService = ApplicationController.getInstance().getNetworkService();
    }
    class MyCurrentLoctionListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}
