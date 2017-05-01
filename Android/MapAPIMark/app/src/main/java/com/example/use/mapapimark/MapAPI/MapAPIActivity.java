package com.example.use.mapapimark.MapAPI;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.use.mapapimark.R;
import com.example.use.mapapimark.StartSetting.Permission;
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

/**
 * 구글 맵에 있는 기능 들은 MapAPIActivity 클래스에 인터페이스 구현을 하도록 한다.
 */
public class MapAPIActivity extends AppCompatActivity implements OnMapReadyCallback,
                               GoogleApiClient.ConnectionCallbacks,
                               GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMyLocationButtonClickListener {

    protected MapFragment mMapFragment;       // 맵 프래그먼트(맵 띄우는 것)

    protected GoogleMap mGoogleMap;                 // GPS UI
    protected GoogleApiClient mGoogleApiClient;     // GPS 기능을 적용하는 서비스
    protected boolean mLocationPermissionGranted;   // GPS 권한 체크 확인
    protected LocationRequest mLocationRequest;     // 디바이스 GPS 위치요청 정보
    protected Location mCurrentLocation;            // 디바이스 위치
    protected CameraPosition mCameraPosition;       // 지도 카메라 위치 정보(상태 저장용)
    protected PendingIntent mLocationIntent;        // 다른 컴포넌트에게 인텐트 권한 주기

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        updateLocationUI();     // 구글맵 자신 GPS UI 셋팅

        if(mCameraPosition != null)     // 카메라 위치 정보가 있을경우
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));      // 초기 화면 셋팅
        else if(mCurrentLocation != null) {     // 위치 정보가 있을경우
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),16));       // 초기 화면 셋팅
        } else {        // 상태정보가 없을경우 (기본 값 셋팅)
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.56, 126.97),16));
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

/*        LatLng seoul = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(seoul);
        markerOptions.title("서울");
        markerOptions.snippet("한궁의 수도");
        mGoogleMap.addMarker(markerOptions);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(10));*/

    }

/*    // LocationListener 의 함수
    // 로케이션 정보를 얻을 때 콜백 메소드
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Toast.makeText(this,"x : "+ mCurrentLocation.getLatitude() + "y : " + mCurrentLocation.getLongitude(),Toast.LENGTH_SHORT).show();
    }*/

    // GoogleApiClient.ConnectionCallbacks 의 함수
    @Override
    public void onConnected(Bundle bundle) {
        getDeviceLocation();    // GPS 권한 확인

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);   // 레이아웃에 구글 맵 프로그먼트 아이디 가져오기
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

    // GPS 권한 확인
    // 이 함수 안에 권한 체크로 인한 오류가 발생하므로 어노테이션을 설정해 오류를 제거
    @SuppressWarnings("MissingPermission")
    protected void getDeviceLocation() {
        if (Permission.checkPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mLocationPermissionGranted = true;
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);       // 디바이스 위치 가져오기
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);   // 내 위치의 업데이트 각 기능 주기 등 셋팅
            mLocationIntent = PendingIntent.getService(this, 0, new Intent(this, LocationBackground.class), PendingIntent.FLAG_UPDATE_CURRENT);   // 다른 컴포넌트에게 인텐트 권한 주기
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationIntent);   // 내 위치의 업데이트 각 기능 주기 등 셋팅
        }
        else {
            // 권한이 없으니 다시 확인메세지 띄우기
            Permission.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }

    // GooglePlayServicesClient 객체를 생성 메서드
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

    // 디바이스 GPS 위치요청 하는 객체 만들기
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
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.setOnMyLocationButtonClickListener(this);
        }
        // 안 했을시 UI 셋팅 안하게
        else {
            mGoogleMap.setMyLocationEnabled(false);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            mGoogleMap.setOnMyLocationButtonClickListener(null);
        }
    }

    // 자기 자신 GPS 버튼을 눌렀을경우 콜백 메소드 호출
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this,"버튼 누름",Toast.LENGTH_SHORT).show();
        return false;
    }
}
