package whoim.leaveout.MapAPI;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import whoim.leaveout.Loading.LoadingSQLDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.R;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.StartSetting.Permission;
import whoim.leaveout.ViewArticleActivity;

/**
 * 구글 맵에 있는 기능 들은 MapAPIActivity 클래스에 인터페이스 구현을 하도록 한다.
 */
public class MapAPIActivity extends AppCompatActivity implements OnMapReadyCallback,
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

    protected Location mCurrentLocation;            // 디바이스 위치
    protected CameraPosition mCameraPosition;       // 지도 카메라 위치 정보(상태 저장용)

    private LocationManager mLocationManager;       // 로케이션 매니저(GPS 활성화 여부를 위해 사용)

    protected ClusterMaker mClusterMaker;           // 클러스터 기능 관리 및 제공 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationIntent = new Intent(this, LocationBackground.class);
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

       /*// 테스트 중(마커 생성)
        mClusterMaker.clerMakerAll();
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
        return false;
    }

    // GPS 권한 확인
    // 이 함수 안에 권한 체크로 인한 오류가 발생하므로 어노테이션을 설정해 오류를 제거
    @SuppressWarnings("MissingPermission")
    protected void getDeviceLocation() {
        if (Permission.checkPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mLocationPermissionGranted = true;
//            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);       // 디바이스 위치 가져오기
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);   // 내 위치의 업데이트 각 기능 주기 등 셋팅
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
        mLocationRequest.setFastestInterval(5000);  // 위치 획득 후 업데이트 되는 주기(ms 단위)
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
    }

    // GPS 위치 서비스 활성화 되어있는지 확인
    protected boolean checkLocationServicesStatus() {
        if (mLocationManager == null) mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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

    // GPS를 주소로 변환
    public String getCurrentAddress(Location location){
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
        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return addressToken(address.getAddressLine(0).toString());
        }
    }

    // 주소 토큰
    private String addressToken(String address) {
        String token1 = "대한민국 ";
        return address.substring(token1.length());
    }

    // 게시글 마커 지도에 표시
    private void ContentMarkerSQLData() {
        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }
            @Override
            public JSONObject getSQLQuery() {
                return SQLDataService.getSQLJSONData("select content_num,loc_x,loc_y from content where visibility = 1 and fence = false",-1,"select");
            }
            @Override
            public JSONObject getUpLoad() {
                return null;
            }
            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                mGoogleMap.clear();
                mClusterMaker.clerMakerAll();
                JSONArray result = responseData.get(0).getJSONArray("result");
                if(result.length() == 0) return;
                for(int i = 0; i < result.length(); i++) {
                    JSONObject data = result.getJSONObject(i);
                    int contentnum =  data.getInt("content_num");
                    double locX = data.getDouble("loc_x");
                    double locY = data.getDouble("loc_y");
                    mClusterMaker.addSNSInfoMaker(new SNSInfoMaker(new LatLng(locX,locY),contentnum));
                }
                mClusterMaker.resetCluster();
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener,ProgressDialog.STYLE_SPINNER,null);
    }


    // 클러스링 된 마커 클릭한 경우 리스너
    @Override
    public boolean onClusterClick(Cluster<SNSInfoMaker> cluster) {
        String query = SQLDataService.getDynamicQuery(cluster.getSize());       // ? string 만들기

        mDataQueryGroup.clear();
        Iterator iterator = cluster.getItems().iterator();      // 클러스터링 아이템(마커) 배열 꺼내기
        while (iterator.hasNext()) {
            SNSInfoMaker snsInfoMaker = (SNSInfoMaker) iterator.next();     // 한개씩 가져오기
            mDataQueryGroup.addInt(snsInfoMaker.getContentNum());
        }
        selectContentSQL(query);        //  마커 클릭시 이동
        Toast.makeText(this,"클러스터링 클릭",Toast.LENGTH_SHORT).show();
        return true;
    }

    // 일반 마커 클릭한 경우 리스너
    @Override
    public boolean onClusterItemClick(SNSInfoMaker snsInfoMaker) {
        String query = SQLDataService.getDynamicQuery(1);       // ? string 만들기
        mDataQueryGroup.clear();
        mDataQueryGroup.addInt(snsInfoMaker.getContentNum());
        selectContentSQL(query);        //  마커 클릭시 이동
        Toast.makeText(this,"마커 클릭",Toast.LENGTH_SHORT).show();
        return true;
    }

    private void selectContentSQL(String query) {

        final String sql = "select name, view_cnt, rec_cnt, reg_time,address,files " +
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
                return SQLDataService.getDynamicSQLJSONData(sql, mDataQueryGroup, -1, "select");
            }
            @Override
            public JSONObject getUpLoad() {
                return null;
            }
            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                Intent intent = new Intent(getApplicationContext(), ViewArticleActivity.class);
                intent.putExtra("responseData", responseData.get(0).toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        };
        LoadingSQLDialog.SQLSendStart(this, loadingSQLListener,ProgressDialog.STYLE_SPINNER, null);
    }
}




