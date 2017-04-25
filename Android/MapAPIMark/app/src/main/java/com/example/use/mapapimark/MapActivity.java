package com.example.use.mapapimark;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.use.mapapimark.MapAPI.MapAPIActivity;
import com.example.use.mapapimark.StartSetting.Permission;
import com.google.android.gms.maps.MapFragment;

/**
 * 구글 맵 액티비티
 * MapAPIActivity 있는것을 상속 하여 부모 클래스에는 Map 구현을 한 것으로 한번 필터
 * */
public class MapActivity extends MapAPIActivity {

    MapFragment mMapFragment;       // 맵 프래그먼트(맵 띄우는 것)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Permission.permissionSet(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });     // GPS 권한 설정 하기(함수)

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);   // 레이아웃에 구글 맵 프로그먼트 아이디 가져오기
        mMapFragment.getMapAsync(this);      // 구글 맵을 실행시켜 맵을 띄우기

        buildGoogleApiClient();         // GooglePlayServicesClient 객체를 생성
        mGoogleApiClient.connect();     // connect 메소드가 성공하면 onConnect() 콜백 메소드를 호출

    }

    // 권한 요청 한뒤 어떻게 되어있는지 판단 (권한 확인을 메세지로 표시시)
   @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case Permission.REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "승인 완료", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this,"승인 거부",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
