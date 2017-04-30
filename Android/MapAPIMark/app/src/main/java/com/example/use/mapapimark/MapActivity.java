package com.example.use.mapapimark;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.use.mapapimark.MapAPI.MapAPIActivity;
import com.example.use.mapapimark.StartSetting.Permission;

/**
 * 구글 맵 액티비티
 * MapAPIActivity 있는것을 상속 하여 부모 클래스에는 Map 구현을 한 것으로 한번 필터
 * */
public class MapActivity extends MapAPIActivity {

    private static final String CAMERA_POSITION = "camera_position";
    private static final String LOCATION = "location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {        // 상태 저장 한 것이 있으면 불러오기
            super.mCurrentLocation = savedInstanceState.getParcelable(LOCATION);
            super.mCameraPosition = savedInstanceState.getParcelable(CAMERA_POSITION);
        }

        setContentView(R.layout.activity_main);

        /* 나중에 처음에 실행될 액티비티에 한꺼번에 묶을 예정 */
        /*
        Permission.permissionSetting(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });     // GPS 권한 설정 하기(함수)
        */

        super.buildGoogleApiClient();         // GooglePlayServicesClient 객체를 생성
        super.mGoogleApiClient.connect();     // connect 메소드가 성공하면 onConnect() 콜백 메소드를 호출

    }

/*    @Override
    protected void onPause() {
        super.onPause();
        if(super.mGoogleApiClient.isConnected()) {
            PendingIntent LocationIntent = PendingIntent.getService(this, 0, new Intent(this, LocationBackground.class), PendingIntent.FLAG_UPDATE_CURRENT);   // 다른 컴포넌트에게 인텐트 권한 주기
            LocationServices.FusedLocationApi.removeLocationUpdates(super.mGoogleApiClient, super.mLocationIntent);
        }
    }

    @Override
    protected void onResume() {
        if(super.mGoogleApiClient.isConnected()) {
            getDeviceLocation();
        }
        super.onResume();
    }*/

    // 액티비티 정지시 상태 저장
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(super.mGoogleMap != null) {
            outState.putParcelable(CAMERA_POSITION, super.mGoogleMap.getCameraPosition());
            outState.putParcelable(LOCATION, super.mCurrentLocation);
        }
        super.onSaveInstanceState(outState);
    }

    // 권한 요청 한뒤 어떻게 되어있는지 판단 (권한 확인을 메세지로 표시시)
   @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            // 초기 셋팅 해서 확인 (테스트중)
            case Permission.REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "승인 완료", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this,"승인 거부",Toast.LENGTH_SHORT).show();
                }
                break;

            // 초기 셋팅때 거부한것을 다시 확인
            case Permission.DENIED_REQUEST_CODE:
                super.mLocationPermissionGranted = false;
                if (Permission.verifyPermission(grantResults)) {
                    super.mLocationPermissionGranted = true;
                    Toast.makeText(this, "GPS 승인 완료", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(this, "GPS 승인 거부", Toast.LENGTH_SHORT).show();

                super.updateLocationUI();       // 구글맵 자신 GPS UI 셋팅
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
