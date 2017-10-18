package whoim.leaveout.MapAPI;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import whoim.leaveout.R;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.Services.FomatService;
import whoim.leaveout.User.UserInfo;
import whoim.leaveout.WritingActivity;

/**
 * 액티비티를 없어도 작동이 되게 서비스를 제공
 * */
public class LocationBackground extends IntentService {
    /* 구글맵있는 액티비티로 브로드캐스트 전송 */
    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance();          // sql에 필요한 데이터 그룹

    public static final String ACTION_LOCATION_BROADCAST = LocationBackground.class.getName() + "broadcast";
    public static final String EXTRA_CURRENT_LOCATION = "current_location";
    private Intent mBroadcastIntent;
    SharedPreferences mLocationNotice;
    SharedPreferences mSwNotice;
    public LocationBackground() {
        super("LocationBackground");
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("on","create");
        if(mBroadcastIntent == null) mBroadcastIntent = new Intent(ACTION_LOCATION_BROADCAST);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("on","destroy");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent != null) {
            if (LocationResult.hasResult(intent)) {
                LocationResult locationResult = LocationResult.extractResult(intent);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    Log.d("mCurrentLocation", "x : " + location.getLatitude() + "y : " + location.getLongitude());
                    mBroadcastIntent.putExtra(EXTRA_CURRENT_LOCATION, location);
                    sendBroadcast(mBroadcastIntent);
                    Location_Notice(location);

                }
            }
        }
    }

    //새로운 위치 알림
    public void Location_Notice(Location location) {
        mLocationNotice = getSharedPreferences("count", MODE_PRIVATE);
        SharedPreferences.Editor editor = mLocationNotice.edit();

        mSwNotice = getSharedPreferences("switch", MODE_PRIVATE);

        /**
         * -좌표 테스트용-
         * 울산 좌표
         *  (35.53889 129.31667)
         * 부산 좌표
         *  (35.17944, 129.07556)
         * 대구 좌표
         *  (35.87222, 128.60250)
         * 서울 좌표
         *  (37.56667, 126.97806)
         */


        //대구 위치 좌표
        if (FomatService.getCurrentAddress(this,location).contains("대구")) {
            //알람 한번만 울리게 하기
            if (mSwNotice.getBoolean("swLocation" + UserInfo.getInstance().getUserNum(), false) == true &&
                    mLocationNotice.getInt("DaeguCount" + UserInfo.getInstance().getUserNum(), 0) == 0) {
                editor.putInt("DaeguCount" + UserInfo.getInstance().getUserNum(), 1);
                editor.commit();
            }
            else {
                return;
            }
            Notice("대구",location);
        }

        else if (FomatService.getCurrentAddress(this,location).contains("서울")) {
            if (mSwNotice.getBoolean("swLocation" + UserInfo.getInstance().getUserNum(), false) == true &&
                    mLocationNotice.getInt("SeoulCount" + UserInfo.getInstance().getUserNum(), 0) == 0) {
                editor.putInt("SeoulCount" + UserInfo.getInstance().getUserNum(), 1);
                editor.commit();
            }
            else {
                return;
            }
            Notice("서울",location);
        }
        else if (FomatService.getCurrentAddress(this,location).contains("부산"))
        {
            if (mSwNotice.getBoolean("swLocation" + UserInfo.getInstance().getUserNum(), false) == true &&
                    mLocationNotice.getInt("BusanCount" + UserInfo.getInstance().getUserNum(), 0) == 0) {
                editor.putInt("BusanCount" + UserInfo.getInstance().getUserNum(), 1);
                editor.commit();
            }
            else {
                return;
            }
            Notice("부산",location);
        }
        else if (FomatService.getCurrentAddress(this,location).contains("인천"))
        {
            if (mSwNotice.getBoolean("swLocation" + UserInfo.getInstance().getUserNum(), false) == true &&
                    mLocationNotice.getInt("BusanCount" + UserInfo.getInstance().getUserNum(), 0) == 0) {
                editor.putInt("BusanCount" + UserInfo.getInstance().getUserNum(), 1);
                editor.commit();
            }
            else {
                return;
            }
            Notice("인천",location);
        }
        else if (FomatService.getCurrentAddress(this,location).contains("대전"))
        {
            if (mSwNotice.getBoolean("swLocation" + UserInfo.getInstance().getUserNum(), false) == true &&
                    mLocationNotice.getInt("BusanCount" + UserInfo.getInstance().getUserNum(), 0) == 0) {
                editor.putInt("BusanCount" + UserInfo.getInstance().getUserNum(), 1);
                editor.commit();
            }
            else {
                return;
            }
            Notice("대전",location);
        }
        else if (FomatService.getCurrentAddress(this,location).contains("광주"))
        {
            if (mSwNotice.getBoolean("swLocation" + UserInfo.getInstance().getUserNum(), false) == true &&
                    mLocationNotice.getInt("BusanCount" + UserInfo.getInstance().getUserNum(), 0) == 0) {
                editor.putInt("BusanCount" + UserInfo.getInstance().getUserNum(), 1);
                editor.commit();
            }
            else {
                return;
            }
            Notice("광주",location);
        }
        else if (FomatService.getCurrentAddress(this,location).contains("울산"))
        {
            if (mSwNotice.getBoolean("swLocation" + UserInfo.getInstance().getUserNum(), false) == true &&
                    mLocationNotice.getInt("BusanCount" + UserInfo.getInstance().getUserNum(), 0) == 0) {
                editor.putInt("BusanCount" + UserInfo.getInstance().getUserNum(), 1);
                editor.commit();
            }
            else {
                return;
            }
            Notice("울산",location);
       }
    }

    //알림
    public void Notice(String name,Location location)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

        Intent intent1 = new Intent(LocationBackground.this.getApplicationContext(),
                WritingActivity.class); //인텐트 생성.
        intent1.putExtra("address",FomatService.getCurrentAddress(getApplicationContext(),location));
        intent1.putExtra("loc",location);

        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        //현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를없앤다.
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(LocationBackground.this, 0,
                intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        //PendingIntent는 일회용 인텐트 같은 개념입니다.
        //FLAG_UPDATE_CURRENT - > 만일 이미 생성된 PendingIntent가 존재 한다면, 해당 Intent의 내용을 변경함.
        //FLAG_CANCEL_CURRENT - .이전에 생성한 PendingIntent를 취소하고 새롭게 하나 만든다.
        //FLAG_NO_CREATE -> 현재 생성된 PendingIntent를 반환합니다.
        //FLAG_ONE_SHOT - >이 플래그를 사용해 생성된 PendingIntent는 단 한번밖에 사용할 수 없습니다

        builder.setSmallIcon(R.drawable.preferences_icon).setTicker("HETT").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle(name +"에 처음 도착하였습니다.").setContentText("새로운 위치에 글을 등록 하세요.")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingNotificationIntent)
                .setAutoCancel(true);

        //해당 부분은 API 4.1버전부터 작동합니다.

        //setSmallIcon - > 작은 아이콘 이미지
        //setTicker - > 알람이 출력될 때 상단에 나오는 문구.
        //setWhen -> 알림 출력 시간.
        //setContentTitle-> 알림 제목
        //setConentText->푸쉬내용

        notificationManager.notify(1, builder.build()); // Notification send
    }
}
