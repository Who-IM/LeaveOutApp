package whoim.leaveout;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // 툴바
    Toolbar toolbar;

    // 구글맵 현재 위치 찍기
    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    private static final LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
    private static final String TAG = "googlemap";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000; // 1초

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    // ---------------------------------------------------------------------

    // 메뉴 관련 인스턴스
    private ListView list;
    private FrameLayout Container;
    private DrawerLayout Drawer;
    private ImageButton menu_btn;

    private LinearLayout buttonbox;
    private RelativeLayout main_map;
    private EditText main_editext;
    private ImageButton main_search;
    private TextView main_location;

    DataAdapter adapter; // 데이터를 연결할 Adapter
    ArrayList<menuData> alist; // 데이터를 담을 자료구조
    // ----------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main);

        mActivity = this;

        // google map
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.main_google_map);
        mapFragment.getMapAsync(this);

        // 폰트 설정
        setFont();

        // 메뉴 커스텀
        setMenuCustom();
    }

    // 폰트 설정
    private void setFont() {
        //버튼 폰트
        Typeface typeface = Typeface.createFromAsset(getAssets(), "RixToyGray.ttf");
        Button button = (Button) findViewById(R.id.main_write);
        Button button1 = (Button) findViewById(R.id.main_check);
        Button button2 = (Button) findViewById(R.id.main_collect);
        button.setTypeface(typeface);
        button1.setTypeface(typeface);
        button2.setTypeface(typeface);

        toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#00FFFFFF"));   //제목 투명하게
        setSupportActionBar(toolbar);//액션바와 같게 만들어줌
    }

    // 메뉴 커스텀 (나중에 DB받아서 수정)
    private void setMenuCustom() {
        // 매뉴 구성
        list = (ListView)findViewById(R.id.main_menu);
        Container = (FrameLayout)findViewById(R.id.main_menu_container);
        Drawer = (DrawerLayout)findViewById(R.id.main_drawer);
        menu_btn = (ImageButton)findViewById(R.id.menu_btn);

        // 메인화면 (글쓰기, 체크, 모아보기, 맵화면, location, search, search icon)
        buttonbox = (LinearLayout) findViewById(R.id.main_button_layout);
        main_map = (RelativeLayout) findViewById(R.id.main_map);
        main_editext = (EditText) findViewById(R.id.main_search);
        main_search = (ImageButton) findViewById(R.id.search_icon);
        main_location = (TextView) findViewById(R.id.main_location);

        // 매뉴 imagebutton 누를 시 이벤트 처리
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            // 메뉴화면 펼치기(layout 앞으로 이동)
            public void onClick(View v) {
                Drawer.bringToFront();
                Container.bringToFront();
                list.bringToFront();
                Drawer.openDrawer(list); // 펼치기
            }
        });

        // 빈화면 터치시 이벤트 처리
        Container.setOnClickListener(new View.OnClickListener() {
            @Override
            // 메뉴화면 닫기면 메인화면의 위젯, 레이아웃들 앞으로 이동
            public void onClick(View v) {
                buttonbox.bringToFront();     // 글쓰기, 체크, 모아보기 앞으로
                main_map.bringToFront();      // 맵 layout 앞으로
                main_editext.bringToFront();  // search editext 앞으로
                main_search.bringToFront();   // search icon 앞으로
                main_location.bringToFront(); // 위치 text 앞으로
                Drawer.closeDrawer(list);      // 메뉴 종료
            }
        });

        // ArrayList객체를 생성합니다
        alist = new ArrayList<menuData>();
        // 데이터를 받기위해 데이터어댑터 객체 선언
        adapter = new DataAdapter(this, alist);
        // 리스트뷰에 어댑터 연결
        list.setAdapter(adapter);

        // 메뉴에 글 목록 등록
        list.setOnItemClickListener(new DrawerItemClickListener());

        // 자기 프로필(사진, 이름, email)
        adapter.add(new menuData(getApplicationContext(), R.drawable.basepicture, "허 성 문", "gjtjdans123@naver.com"));  // 안의 데이터는 db받아서
        adapter.add(new menuData()); // 프로필아이콘 & 프로필(text)
        adapter.add(new menuData()); // 친구아이콘 & 친구(text)
        adapter.add(new menuData()); // 환경설정아이콘 & 환경설정(text)
    }

    /* 매뉴 눌렀을 시 이벤트 처리
        0 : 자기 프로필(아무것도 처리 x)
        1 : 프로필로 이동 (미구현)
        2 : 친구 보기 펼치기 (미구현)
        3 : 환경설정                        */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        Intent button;  //환경설정 버튼
        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            switch (position) {
                case 1:

                    break;
                case 2: // 친구목록으로 이동
                    // 폰트 설정
                    button = new Intent(getApplicationContext(), Friend_list.class);
                    startActivity(button);
                    break;
                case 3: // 환경설정으로 이동
                    button = new Intent(getApplicationContext(), Preferences.class);
                    startActivity(button);
                    break;
            }
            Drawer.closeDrawer(list);
        }
    }

    // 메뉴 커스텀
    private class DataAdapter extends ArrayAdapter<menuData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public DataAdapter(Context context, ArrayList<menuData> object) {
            // 상위 클래스의 초기화 과정
            // context, 0, 자료구조
            super(context, 0, object);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        // 보여지는 스타일을 자신이 만든 xml로 보이기 위한 구문
        @Override
        public View getView(int position, View v, ViewGroup parent) {
            View view = null;
            // 현재 리스트의 하나의 항목에 보일 컨트롤 얻기

            // view 구성하기 (0 : 자기 프로필 화면, 1 : 프로필 아이콘 & text, 2 : 친구아이콘 & text)
            if (v == null && position == 0) {
                view = mInflater.inflate(R.layout.menu_profile_title, null);
            } else if(position == 1) {
                view = mInflater.inflate(R.layout.menu_profile, null);
            } else if(position == 2) {
                view = mInflater.inflate(R.layout.menu_friends, null);
            } else if(position == 3) {
                view = mInflater.inflate(R.layout.menu_preferences, null);
            }

            // 자료를 받는다.
            final menuData data = this.getItem(position);

            // 자기 프로필
            if (data != null && position == 0) {
                // 자기 사진
                ImageView iv = (ImageView) view.findViewById(R.id.menu_home_icon);
                iv.setImageResource(data.getImage());

                // 이름. email
                TextView tv = (TextView) view.findViewById(R.id.menu_profile_myname);
                TextView tv2 = (TextView) view.findViewById(R.id.menu_profile_myemail);
                tv.setText(data.getLabel());
                tv2.setText(data.getLabel2());
            }
            else if (position == 1) {

            }
            else if (position == 2) {

            }
            else if (position == 3) {

            }

            return view;
        }
    }

    // menuData안에 받은 값을 직접 할당
    class menuData {
        private String label1; // text 처리
        private String label2; // text 처리2
        private int menu_image; // 이미지 처리

        public menuData() {}

        public menuData(Context context, int image, String label1, String label2) {
            menu_image = image;
            this.label1 = label1;
            this.label2 = label2;
        }

        public menuData(Context context, String label) { label1 = label; }
        public String getLabel() { return label1; }
        public String getLabel2() { return label2; }
        public int getImage() { return menu_image; }
    }

    // 뒤로가기
    @Override
    public void onBackPressed() {
        // 메뉴 화면이 open 되있을 경우
        if (Drawer.isDrawerOpen(list)) {
            buttonbox.bringToFront();     // 글쓰기, 체크, 모아보기 앞으로
            main_map.bringToFront();      // 맵 layout 앞으로
            main_editext.bringToFront();  // search editext 앞으로
            main_search.bringToFront();   // search icon 앞으로
            main_location.bringToFront(); // 위치 text 앞으로
            Drawer.closeDrawer(list);      // 메뉴 종료
        } else {
            super.onBackPressed();
        }
    }

    //체크 버튼 눌렀을시 토스트 작동
    public void checkButton(View v) {
        Toast toastView = Toast.makeText(getApplicationContext(), "체크되었습니다.", Toast.LENGTH_LONG);
        toastView.setGravity(Gravity.TOP, 0, 1300);
        toastView.show();
    }

    //글쓰기 버튼 누르면 글쓰는창 띄우기
    public void writeButton(View v) {
        Intent intent = new Intent(getApplicationContext(), Writing.class);
        startActivity(intent);
    }

    //글보기 임시버튼
    public void view_button(View v)
    {
        Intent intent = new Intent(getApplicationContext(), View_article.class);
        startActivity(intent);
    }

    public void collectButton(View v)
    {
        Intent intent = new Intent(getApplicationContext(), Collect.class);
        startActivity(intent);
    }

    // 여기부터 현재위치 받아오기
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();

        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;
                checkPermissions();
            }
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        //위치 업데이트 중지
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.unregisterConnectionCallbacks(this);
            mGoogleApiClient.unregisterConnectionFailedListener(this);

            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi
                        .removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //API 23 이상이면 런타임 퍼미션 처리 필요

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                }
                mGoogleMap.setMyLocationEnabled(true);
            }
        } else {
            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    // 위도 경도 찍기(현재위치에서 누를시 위도 경도나옴)
    @Override
    public void onLocationChanged(Location location) {

        String markerTitle = getCurrentAddress(location);
        String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                + " 경도:" + String.valueOf(location.getLongitude());

        // 현재위치 받아오기
        TextView v = (TextView) findViewById(R.id.main_location);
        //v.setText("위도 : " + location.getLongitude() +" 경도 : " + location.getLatitude());

        v.setText(markerTitle); //현재 위치

        //---------------------------------

        //현재 위치에 마커 생성
        setCurrentLocation(location, markerTitle, markerSnippet);
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnected(Bundle connectionHint) {

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL_MS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Location location = null;
        location.setLatitude(DEFAULT_LOCATION.latitude);
        location.setLongitude(DEFAULT_LOCATION.longitude);

        setCurrentLocation(location, "위치정보 가져올 수 없음",
                "위치 퍼미션과 GPS 활성 요부 확인하세요");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }

    public String getCurrentAddress(Location location) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)  // GPS 정보 가져오기
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); // 현재 네트워크 상태 값 알아오기

    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        if (currentMarker != null) currentMarker.remove();
        if (location != null) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            //마커를 원하는 이미지로 변경해줘야함
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            currentMarker = mGoogleMap.addMarker(markerOptions);

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            return;
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));
    }

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

        else if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) 체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permsRequestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {
            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {
                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                }

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mGoogleMap.setMyLocationEnabled(true);
                }
            } else {
                checkPermissions();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        if (ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mGoogleMap.setMyLocationEnabled(true);
                        }
                        return;
                    }
                } else {
                    setCurrentLocation(null, "위치정보 가져올 수 없음", "위치 퍼미션과 GPS 활성 요부 확인하세요");
                }
                break;
        }
    }
}
