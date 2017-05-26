package org.sopt.nawa_103.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPoint.GeoCoordinate;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import org.sopt.nawa_103.Model.DB.Item;
import org.sopt.nawa_103.Model.DB.OnFinishSearchListener;
import org.sopt.nawa_103.Model.Element.MapApiConst;
import org.sopt.nawa_103.Model.Element.Searcher;
import org.sopt.nawa_103.R;

import java.util.HashMap;
import java.util.List;


public class MapActivity extends FragmentActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener {

    private static final String LOG_TAG = "MapActivity";
    private MapView mMapView;
    private EditText PromisePlace;
    private ImageButton MylocationButton,TasteButton, CafeButton, DistanceButton, SearchButton, CallButton, CompleteButton;
    private TextView txtdistance, t_name, t_address, t_tel;
    private String placetxt;
    private int count = 0, radius;
    double myLatitude, myLongitude;
    LinearLayout SearchLayout,BackButton;
    LinearLayout DetailLayout,SeekbarLayout;
    Context context;
    SeekBar seekbar;
    String send_name,send_address,send_phonenum;
    double send_Latitude,send_Longitude;
    private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initView();

        DetailLayout.setVisibility(View.INVISIBLE);
        context = getApplicationContext();

        mMapView = (MapView) findViewById(R.id.map_mapview);
        mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);
        mMapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyCurrentLoctionListener locationListener = new MyCurrentLoctionListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastLocation != null) {
            myLatitude = lastLocation.getLatitude();
            myLongitude = lastLocation.getLongitude();
        }

        //mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(myLatitude, myLongitude), 2, true);
        DistanceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ++count;
                if (count % 2 == 1) {
                    SearchLayout.setVisibility(View.INVISIBLE);
                    SeekbarLayout.setVisibility(View.VISIBLE);

                } else {
                    SearchLayout.setVisibility(View.VISIBLE);
                    SeekbarLayout.setVisibility(View.INVISIBLE);

                }
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                radius = i;
                txtdistance.setText(radius + " M");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        BackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(5);
                finish();
            }
        });

        MylocationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                MapPointBounds mapPointBounds2 = new MapPointBounds();
                MapPoint mapPoint2 = MapPoint.mapPointWithGeoCoord(myLatitude, myLongitude);
                mapPointBounds2.add(mapPoint2);
                mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds2));
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            }
        });

        TasteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                placetxt = PromisePlace.getText().toString();
                hideSoftKeyboard(); // 키보드 숨김
                GeoCoordinate geoCoordinate = mMapView.getMapCenterPoint().getMapPointGeoCoord();
                double latitude = geoCoordinate.latitude; // 위도
                double longitude = geoCoordinate.longitude; // 경도
                int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
                String apikey = MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY;

                Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
                if (radius == 0) {
                    Toast.makeText(getApplicationContext(), "반경을 설정하세요", Toast.LENGTH_SHORT).show();
                } else {
                    searcher.searchKeyword(getApplicationContext(), placetxt + " 맛집", latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
                        @Override
                        public void onSuccess(List<Item> itemList) {
                            mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
                            if (itemList.size() == 0) {
                                showToast("검색 결과가 없습니다.");
                            } else {
                                showResult(itemList); // 검색 결과 보여줌
                            }
                        }
                        @Override
                        public void onFail() {
                            showToast("검색 결과가 없습니다.");
                        }
                    });
                }
            }
        });

        CafeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                placetxt = PromisePlace.getText().toString();
                hideSoftKeyboard(); // 키보드 숨김
                GeoCoordinate geoCoordinate = mMapView.getMapCenterPoint().getMapPointGeoCoord();
                double latitude = geoCoordinate.latitude; // 위도
                double longitude = geoCoordinate.longitude; // 경도
                int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
                String apikey = MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY;

                Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
                if (radius == 0) {
                    Toast.makeText(getApplicationContext(), "반경을 설정하세요", Toast.LENGTH_SHORT).show();
                } else {
                    searcher.searchKeyword(getApplicationContext(), placetxt + " 카페", latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
                        @Override
                        public void onSuccess(List<Item> itemList) {
                            mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
                            if (itemList.size() == 0) {
                                showToast("검색 결과가 없습니다.");
                            } else {
                                showResult(itemList); // 검색 결과 보여줌
                            }

                        }

                        @Override
                        public void onFail() {
                            showToast("검색 결과가 없습니다.");
                        }
                    });
                }
            }
        });

        SearchButton.setOnClickListener(new OnClickListener() { // 검색버튼 클릭 이벤트 리스너
            @Override
            public void onClick(View v) {
                placetxt = PromisePlace.getText().toString();
                hideSoftKeyboard(); // 키보드 숨김
                GeoCoordinate geoCoordinate = mMapView.getMapCenterPoint().getMapPointGeoCoord();
                double latitude = geoCoordinate.latitude; // 위도
                double longitude = geoCoordinate.longitude; // 경도
                int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
                String apikey = MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY;

                Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
                if (radius == 0) {
                    Toast.makeText(getApplicationContext(), "반경을 설정하세요", Toast.LENGTH_SHORT).show();
                } else {
                    searcher.searchKeyword(getApplicationContext(), placetxt, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
                        @Override
                        public void onSuccess(List<Item> itemList) {
                            mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
                            if (itemList.size() == 0) {
                                showToast("검색 결과가 없습니다.");
                            } else {
                                showResult(itemList); // 검색 결과 보여줌
                            }
                        }
                        @Override
                        public void onFail() {
                            showToast("검색 결과가 없습니다.");
                        }
                    });
                }
            }
        });

        CompleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putString("map_name", send_name);
                b.putString("map_address", send_address);
                b.putString("map_phone", send_phonenum);
                b.putDouble("map_Latitude", send_Latitude);
                b.putDouble("map_Longitude", send_Longitude);
                intent.putExtra("map_items", b);
                /*
                intent.putExtra("map_name",send_name);
                intent.putExtra("map_address",send_address);
                intent.putExtra("map_phone",send_phonenum);
                intent.putExtra("map_imageurl",send_imageurl);*/
                setResult(2, intent);
                finish();
            }
        });

    }

    private void initView()
    {
        SeekbarLayout = (LinearLayout)findViewById(R.id.seekbarlayout);
        BackButton = (LinearLayout)findViewById(R.id.toolbar_btnBack);            //뒤로가기버튼
        CompleteButton = (ImageButton)findViewById(R.id.completebtn);  //완료버튼
        PromisePlace = (EditText) findViewById(R.id.editTextQuery); // 약속장소검색창
        SearchButton = (ImageButton) findViewById(R.id.buttonSearch); // 약속장소검색버튼
        SearchLayout = (LinearLayout) findViewById(R.id.searchlayout);    //seekbar와 검색창이 클릭에 따라 번갈아가면서 뜨기위해 검색창을 하나의 레이아웃으로 묶음
        txtdistance = (TextView) findViewById(R.id.txtdistance);         // seekbar 움직일때에 따라 반경 m를 옆에 표시해주는 txt
        MylocationButton = (ImageButton) findViewById(R.id.myLocation); // 내 위치 검색버튼
        TasteButton = (ImageButton) findViewById(R.id.taste);            //맛집검색
        CafeButton = (ImageButton) findViewById(R.id.cafe);              // 카페검색
        DistanceButton = (ImageButton) findViewById(R.id.distance);    //  반경설정하는 버튼
        DetailLayout = (LinearLayout) findViewById(R.id.clickpoint); // 장소에 대한 맵 포인트를 누르면 세부사항에 관하여 나타나게 되는 레이아웃
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        SeekbarLayout.setVisibility(View.INVISIBLE);
    }

    public void onMapViewInitialized(MapView mapView) {
        Log.i(LOG_TAG, "MapView had loaded. Now, MapView APIs could be called safely");

        mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(myLatitude,myLongitude), 2, true);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("Default Marker");
        marker.setTag(0);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(myLatitude,myLongitude);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);
    /*    Searcher searcher = new Searcher();
        String query = PromisePlace.getText().toString();
        double latitude = 37.537229;
        double longitude = 127.005515;
        int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
        int page = 1;
        String apikey = MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY;*/

  /*      searcher.searchKeyword(getApplicationContext(), query, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
            @Override
            public void onSuccess(final List<Item> itemList) {
                showResult(itemList);
            }

            @Override
            public void onFail() {
                //showToast("API_KEY의 제한 트래픽이 초과되었습니다.");
            }
        });*/
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(PromisePlace.getWindowToken(), 0);
    }



    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MapActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showResult(List<Item> itemList) {
        MapPointBounds mapPointBounds = new MapPointBounds();

        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);

            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(item.title);
            poiItem.setTag(i);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);
            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomImageResourceId(R.mipmap.map_pin_blue);
            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomSelectedImageResourceId(R.mipmap.map_pin_red);
            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f, 1.0f);


      /*      poiItem.setCustomPressedCalloutBalloon(getLayoutInflater().inflate(R.layout.custom_callout_balloon,null));
            poiItem.setCustomCalloutBalloon(getLayoutInflater().inflate(R.layout.custom_callout_balloon,null));*/
            Log.i("MyTag", "똥색 막기!!!11" + item.title + "    "+poiItem.getItemName());
            mMapView.addPOIItem(poiItem);
            mTagItemMap.put(poiItem.getTag(), item);

        }

        mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));

        MapPOIItem[] poiItems = mMapView.getPOIItems();
        if (poiItems.length > 0) {
            mMapView.selectPOIItem(poiItems[0], false);
        }
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        final Item item = mTagItemMap.get(mapPOIItem.getTag());


        DetailLayout.setVisibility(View.VISIBLE);

        send_name=item.title;
        send_address=item.address;
        send_phonenum=item.phone;
        send_Latitude=item.latitude;
        send_Longitude=item.longitude;

        t_name = (TextView) findViewById(R.id.txtname);                 //상세정보에서 가게이름
        t_address = (TextView) findViewById(R.id.txtadress);           //상세정보에서 가게주소
        t_tel = (TextView) findViewById(R.id.txttel);                   //상세정보에서 전화번호
        CallButton = (ImageButton) findViewById(R.id.detailbtn);      //상세정보에서 전화걸기

        t_name.setText(item.title);
        t_address.setText(item.address);
        t_tel.setText(item.phone);

        CallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.phone));
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
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    @Deprecated
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint) {
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
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

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
    }

    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {

        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {

            if (poiItem == null) return null;
            Item item = mTagItemMap.get(poiItem.getTag());
            if (item == null) return null;


            // ImageView imageViewBadge = (ImageView) mCalloutBalloon.findViewById(R.id.badge);
            TextView textViewTitle = (TextView) mCalloutBalloon.findViewById(R.id.title);
            textViewTitle.setText(item.title);
            Log.i("MyTag", "BalloonAdapter 글씨나와라ㅡㅡ "+ item.title);



            //  TextView textViewDesc = (TextView) mCalloutBalloon.findViewById(R.id.desc);
            // textViewDesc.setText(item.address);
            //imageViewBadge.setImageDrawable(createDrawableFromUrl(item.imageUrl));
            return mCalloutBalloon;


        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }

    }

    class MyCurrentLoctionListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            myLatitude = location.getLatitude();
            myLongitude = location.getLongitude();
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