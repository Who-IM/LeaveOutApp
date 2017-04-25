package com.example.use.mapapimark.StartSetting;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


/**
 * 어플을 실행 할때 맨 처음 권한이 되어있는지 확인한다.
 * */
public class Permission {

    public static final int REQUEST_CODE = 100;       // 권한 요청 코드

    // 권한 되어있는지 요청 하여 없을 시 셋팅
    public static void permissionSet(Activity activity, String[] permissionValues) {

        // 여러개 권한 체크
        for(String permission : permissionValues) {
            // 사용 권한 체크( 사용권한이 없을경우 -1 )
            if(ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없을경우
                // 최초 권한 요청인지, 혹은 사용자에 의한 재요청인지 확인
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    // 사용자가 임의로 권한을 취소시킨 경우
                    // 권한 재요청
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, REQUEST_CODE);

                } else {
                    // 최초로 권한을 요청하는 경우(첫실행)
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, REQUEST_CODE);
                }
            }
        }
    }
}
