package whoim.leaveout.MapAPI;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import whoim.leaveout.Loading.LoadingSQLDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.MainActivity;
import whoim.leaveout.PreferencesNoticeActivity;
import whoim.leaveout.R;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.Services.SharedDatabase;
import whoim.leaveout.StartSetting.Permission;
import whoim.leaveout.ViewArticleActivity;

/**
 * 구글 맵에 있는 기능 들은 MapAPIActivity 클래스에 인터페이스 구현을 하도록 한다.
 */
public abstract class MapAPIActivity extends AppCompatActivity implements OnMapReadyCallback,
                               GoogleApiClient.ConnectionCallbacks,
                               GoogleApiClient.OnConnectionFailedListener,
                                GoogleMap.OnMyLocationButtonClickListener,
                                ClusterManager.OnClusterClickListener<SNSInfoMaker>,
                                ClusterManager.OnClusterItemClickListener<SNSInfoMaker>{

    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance();

    protected static final int GPS_ENABLE_REQUEST_CODE = 1000;      // GPS 서비스 요청 코드

    protected MapFragment mMapFragment;       // 맵 프래그먼트(맵 띄우는 것)

    protected GoogleMap mGoogleMap;                 // 구글 맵
    protected GoogleApiClient mGoogleApiClient;     // 구글 맵 기능을 적용하는 서비스

    protected boolean mLocationPermissionGranted;   // GPS 권한 체크 확인
    protected LocationRequest mLocationRequest;     // 디바이스 GPS 위치요청 정보
    protected PendingIntent mLocationPendingIntent; // 다른 컴포넌트에게 인텐트 권한 주기(백그라운드 GPS 위치 서비스)
    protected Intent mLocationIntent;               // 로케이션 백그라운드 서비스

    public static Location mCurrentLocation;            // 디바이스 위치
    protected CameraPosition mCameraPosition;       // 지도  카메라 위치 정보(상태 저장용)

    private LocationManager mLocationManager;       // 로케이션 매니저(GPS 활성화 여부를 위해 사용)

    protected int distance = 250;                     // 제한 거리(울타리 용)
    protected ClusterMaker mClusterMaker;           // 클러스터 기능 관리 및 제공 객체
    private Circle mCircle;                         // 울타리글에 필요한 원그리기

    private String mFenceSQL = "select content_num,loc_x,loc_y,fence,visibility," +
            "Cast((6371*acos(cos(radians(?))*cos(radians(loc_x))*cos(radians(loc_y) - radians(?))+sin(radians(?))*sin(radians(loc_x)))*1000) as signed integer) AS distance " +
            "from content " +
            "where visibility = 1 and fence = true " +
            "Having distance <= "+ distance;        // 울타리글 sql

    SharedDatabase database;        // 상태 저장 데이터베이스

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationIntent = new Intent(this, LocationBackground.class);

        database = new SharedDatabase(getApplicationContext(),null,1);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        updateLocationUI();     // 구글맵 자신 GPS UI 셋팅
        mClusterMaker = new ClusterMaker(this, mGoogleMap); // 클러스터 기능 관리 및 제공 객체
        mClusterMaker.setOnClusterClickListener(this);          // 클러스터링 클릭시 리스너 셋팅
        mClusterMaker.setOnClusterItemClickListener(this);          // 마커 클릭시 리스너

        if(mCameraPosition != null)     // 카메라 위치 정보가 있을경우
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));      // 초기 화면 셋팅
        else if(mCurrentLocation != null) {     // 위치 정보가 있을경우
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),16));       // 초기 화면 셋팅
        } else {        // 상태정보가 없을경우 (기본 값 셋팅)
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.56, 126.97),16));
//            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        ContentMarkerSQLData();             // 미커 데이터베이스 SQL 문
        circleSet();
        fenceSQLStart();

       /*// 테스트 중(마커 생성)
        mClusterMaker.clearMakerAll();
        Random mRandom = new Random(1984);
        List<SNSInfoMaker> item = new ArrayList<>();
        for(int i = 0; i< 100; i++) {
            item.add(new SNSInfoMaker(new LatLng(mRandom.nextDouble() * (37.56 - 37.78494009999999) + 37.78494009999999, mRandom.nextDouble() * (126.97 - 127.97) + 127.97), i));
        }
        mClusterMaker.addSNSInfoMakerList(item);*/


/*
        LatLng seoul = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(seoul);
        markerOptions.title("서울");
        markerOptions.snippet("한궁의 수도");
        mGoogleMap.addMarker(markerOptions);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        */

    }


/*    // LocationListener 의 함수(구현 중)
    // 로케이션 정보를 얻을 때 콜백 메소드
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.d("mCurrentLocation","x : "+ location.getLatitude() + "y : " + location.getLongitude());
    }*/

    // GoogleApiClient.ConnectionCallbacks 의 함수
    @Override
    public void onConnected(Bundle bundle) {
        getDeviceLocation();    // GPS 권한 확인

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.main_google_map);   // 레이아웃에 구글 맵 프로그먼트 아이디 가져오기
        mMapFragment.getMapAsync(this);      // 구글 맵을 실행시켜 맵을 띄우기(onMapReady 콜백 메소드)

    }

    // GoogleApiClient.onConnectionSuspended 의 함수
    @Override
    public void onConnectionSuspended(int i) {

    }

    // GoogleApiClient.OnConnectionFailedListener 의 함수
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    // 자기 자신 GPS 버튼을 눌렀을경우 콜백 메소드 호출
    @SuppressWarnings("MissingPermission")
    @Override
    public boolean onMyLocationButtonClick() {
        // GPS 위치 서비스 활성화 안 될시 하라는 화면 표시
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
            return true;        // 취소
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);       // 디바이스 위치 가져오기
        Toast.makeText(this,"버튼 누름",Toast.LENGTH_SHORT).show();
        circleSet();
        return false;
    }

    // 울타리글 전용 원그리기
    protected void circleSet() {
        if(mCurrentLocation == null) return;
        if(mCircle != null) mCircle.remove();
        // 반경 1KM원
        CircleOptions circle1KM = new CircleOptions().center(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude())) //원점
                .radius(distance)      //반지름 단위 : m
                .strokeWidth(0f)  //선너비 0f : 선없음
                .fillColor(Color.parseColor("#886e6efc")); //배경색
        mCircle = mGoogleMap.addCircle(circle1KM);
    }

    // GPS 권한 확인
    // 이 함수 안에 권한 체크로 인한 오류가 발생하므로 어노테이션을 설정해 오류를 제거
    @SuppressWarnings("MissingPermission")
    protected void getDeviceLocation() {
        if (Permission.checkPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mLocationPermissionGranted = true;
            // mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);       // 디바이스 위치 가져오기
            // LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);   // 내 위치의 업데이트 각 기능 주기 등 셋팅
            mLocationPendingIntent = PendingIntent.getService(this, 0, mLocationIntent, PendingIntent.FLAG_UPDATE_CURRENT);   // 다른 컴포넌트에게 인텐트 권한 주기
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationPendingIntent);   // 내 위치의 업데이트 각 기능 주기 등 셋팅 및 백그라운드에서 GPS 위치 찾기
        }
        else {
            // 권한이 없으니 다시 확인메세지 띄우기
            Permission.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }

    // GooglePlayServicesClient 객체를 생성 메서드(구글 맵 api 가져오기)
    public synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
    }

    // 디바이스 GPS 위치요청 하는 설정 객체 만들기
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);           // 업데이트 되는 주기(ms 단위)
        mLocationRequest.setFastestInterval(3000);  // 위치 획득 후 업데이트 되는 주기(ms 단위)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // 정확도
    }

    // 구글 맵에 각 GPS UI 기능 셋팅
    // 이 함수 안에 권한 체크로 인한 오류가 발생하므로 어노테이션을 설정해 오류를 제거
    @SuppressWarnings("MissingPermission")
    protected void updateLocationUI() {
        if(mGoogleMap == null) return;
        // 했을경우 UI 셋팅
        if(mLocationPermissionGranted) {
            mGoogleMap.setMyLocationEnabled(true);      // 자기 위치 마커 활성화
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);        // 자기 위치 찾기 버튼 활성화
            mGoogleMap.setOnMyLocationButtonClickListener(this);                // 자기 위치 찾기 버튼 리스너(사용자용)
            // GPS 위치 서비스 활성화 안 될시 하라는 화면 표시
            if (!checkLocationServicesStatus()) showDialogForLocationServiceSetting();
        }
        // 안 했을시 UI 셋팅 안하게
        else {
            mGoogleMap.setMyLocationEnabled(false);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            mGoogleMap.setOnMyLocationButtonClickListener(null);
        }
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(false);
    }

    @SuppressWarnings("MissingPermission")
    // GPS 위치 서비스 활성화 되어있는지 확인
    protected boolean checkLocationServicesStatus() {
        if (mLocationManager == null) mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);       // GPS 서비스 확인;
    }

    // GPS 활성화를 위한 화면 표시
    protected void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n 위치 설정을 수정하실래요?");
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
            public void onClick(DialogInterface dialog, int id){
                Toast.makeText(getApplicationContext(),"위치 서비스를 사용 할 수 없습니다.",Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    protected boolean GPSCheck() {
        if(mCurrentLocation == null)  {
            if(checkLocationServicesStatus())   // GPS 설정됬을 시
                Toast.makeText(this, "잠시후 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
            else         // GPS 설정 안됬을시
                showDialogForLocationServiceSetting();      // 설정 하라는 다이얼로그 띄우기
            return false;
        }
        return true;
    }

    // 울타리글 마커 확인 및 마커 추가 SQL
    protected void fenceSQLStart() {
        if(mCurrentLocation == null) return;
        mDataQueryGroup.clear();
        mDataQueryGroup.addDouble(mCurrentLocation.getLatitude());
        mDataQueryGroup.addDouble(mCurrentLocation.getLongitude());
        mDataQueryGroup.addDouble(mCurrentLocation.getLatitude());

        FenceThread fenceThread = new FenceThread(new FenceThread.FenceUIListener() {
            @Override
            public void FenceUI(JSONObject result) throws JSONException {
                if(result != null) {
                    addMarkerList(result.getJSONArray("result"));
                    for(int i = 0; i < mClusterMaker.getmFenceList().size(); i++) {
                        if(database.getFenceQuery(mClusterMaker.getmFenceList().get(i).getContentNum()) == false) {
                            fence_Notice();
                            break;
                        }
                    }
                }
            }
        });
        fenceThread.execute(SQLDataService.getDynamicSQLJSONData(mFenceSQL,mDataQueryGroup,5,"select"));
    }

    // 게시글 마커 지도에 표시 및 체크 한 것 가져오기
    private void ContentMarkerSQLData() {
        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 2;
            }
            @Override
            public JSONObject getSQLQuery() {
                return SQLDataService.getSQLJSONData("select content_num,loc_x,loc_y,fence,visibility from content where visibility = 1 and fence = false",-1,"select");
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }
            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                if(responseData == null) {
                    Toast.makeText(getApplicationContext(),"서버에 접속할수 없습니다.",Toast.LENGTH_LONG).show();
                    return;
                }
                JSONArray result = responseData.get(0).getJSONArray("result");
                if(result.length() == 0) return;        // 마커가 없을경우 리턴
                addMarkerList(result);      // 마커 표시
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener,ProgressDialog.STYLE_SPINNER,null);
    }

    protected void addMarkerList(JSONArray result) throws JSONException {
        for(int i = 0; i < result.length(); i++) {
            JSONObject data = result.getJSONObject(i);
            int contentnum =  data.getInt("content_num");
            double locX = data.getDouble("loc_x");
            double locY = data.getDouble("loc_y");
            boolean fence = data.getBoolean("fence");
            int visibility = data.getInt("visibility");
            mClusterMaker.addSNSInfoMaker(new SNSInfoMaker(new LatLng(locX,locY),contentnum, fence, visibility));
        }
        mClusterMaker.resetCluster();
    }

    // 클러스링 된 마커 클릭한 경우 리스너
    @Override
    public boolean onClusterClick(Cluster<SNSInfoMaker> cluster) {
        if(GPSCheck()) {
            String query = SQLDataService.getDynamicQuery(cluster.getSize());       // ? string 만들기

            mDataQueryGroup.clear();
            Iterator iterator = cluster.getItems().iterator();      // 클러스터링 아이템(마커) 배열 꺼내기
            while (iterator.hasNext()) {
                SNSInfoMaker snsInfoMaker = (SNSInfoMaker) iterator.next();     // 한개씩 가져오기
                mDataQueryGroup.addInt(snsInfoMaker.getContentNum());
            }
            selectContentSQL(query);        //  마커 클릭시 이동
        }
        return true;
    }

    // 일반 마커 클릭한 경우 리스너
    @Override
    public boolean onClusterItemClick(SNSInfoMaker snsInfoMaker) {
        if(GPSCheck()) {
            String query = SQLDataService.getDynamicQuery(1);       // ? string 만들기
            mDataQueryGroup.clear();
            mDataQueryGroup.addInt(snsInfoMaker.getContentNum());
            selectContentSQL(query);        //  마커 클릭시 이동
        }
        return true;
    }

    private void selectContentSQL(String query) {

        final String sql = "select content_num, name, view_cnt, rec_cnt, reg_time,address,files,profile,email " +
                "from content inner join user " +
                "on content.user_num = user.user_num " +
                "where content_num in (" + query + ")";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }
            @Override
            public JSONObject getSQLQuery() {
                JSONObject data = SQLDataService.getDynamicSQLJSONData(sql, mDataQueryGroup, -1, "select");
                SQLDataService.putBundleValue(data,"download","context","files");
                return SQLDataService.putBundleValue(data,"download","context2","profile");
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }
            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                if(responseData.get(0) != null) {
                    Intent intent = new Intent(getApplicationContext(), ViewArticleActivity.class);
                    intent.putExtra("responseData", responseData.get(0).toString());
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                else Toast.makeText(getApplicationContext(),"잠시후 다시 시도해 주십시오.",Toast.LENGTH_SHORT).show();
            }
        };
        LoadingSQLDialog.SQLSendStart(this, loadingSQLListener,ProgressDialog.STYLE_SPINNER, null);
    }

    public void fence_Notice() {
        if(PreferencesNoticeActivity.swFence == true) {
            for(int i = 0; i< mClusterMaker.getmFenceList().size(); i++) {
                database.fenceInsert(mClusterMaker.getmFenceList().get(i).getContentNum());
            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

            Intent intent1 = new Intent(getApplicationContext(),
                    MainActivity.class); //인텐트 생성.

            Notification.Builder builder = new Notification.Builder(getApplicationContext());

            //현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를없앤다.
            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(MapAPIActivity.this, 0,
                    intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            //PendingIntent는 일회용 인텐트 같은 개념입니다.
            //FLAG_UPDATE_CURRENT - > 만일 이미 생성된 PendingIntent가 존재 한다면, 해당 Intent의 내용을 변경함.
            //FLAG_CANCEL_CURRENT - .이전에 생성한 PendingIntent를 취소하고 새롭게 하나 만든다.
            //FLAG_NO_CREATE -> 현재 생성된 PendingIntent를 반환합니다.
            //FLAG_ONE_SHOT - >이 플래그를 사용해 생성된 PendingIntent는 단 한번밖에 사용할 수 없습니다

            builder.setSmallIcon(R.drawable.preferences_icon).setTicker("HETT").setWhen(System.currentTimeMillis())
                    .setNumber(1).setContentTitle("근처에 울타리글이 있습니다.").setContentText("울타리글을 확인하세요")
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingNotificationIntent).setAutoCancel(true);

            //setSmallIcon - > 작은 아이콘 이미지
            //setTicker - > 알람이 출력될 때 상단에 나오는 문구.
            //setWhen -> 알림 출력 시간.
            //setContentTitle-> 알림 제목
            //setConentText->푸쉬내용

            notificationManager.notify(1, builder.build()); // Notification send
        }
    }
}




