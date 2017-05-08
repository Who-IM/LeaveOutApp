package whoim.leaveout.MapAPI;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

/**
 * 액티비티를 없어도 작동이 되게 서비스를 제공
 * */
public class LocationBackground extends IntentService {

    /* 구글맵있는 액티비티로 브로드캐스트 전송 */
    public static final String ACTION_LOCATION_BROADCAST = LocationBackground.class.getName() + "broadcast";
    public static final String EXTRA_CURRENT_LOCATION = "current_location";
    private Intent mBroadcastIntent;

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
                    Log.d("mCurrentLocation","x : "+ location.getLatitude() + "y : " + location.getLongitude());
                    mBroadcastIntent.putExtra(EXTRA_CURRENT_LOCATION,location);
                    sendBroadcast(mBroadcastIntent);
                }
            }
        }
    }
}
