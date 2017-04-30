package com.example.use.mapapimark.MapAPI;

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

    Location mCurrentLocation;

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
        Log.d("on?","create");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("on?","destroy");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent != null) {
            if (LocationResult.hasResult(intent)) {
                LocationResult locationResult = LocationResult.extractResult(intent);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    mCurrentLocation = location;
                    Log.d("mCurrentLocation","x : "+ mCurrentLocation.getLatitude() + "y : " + mCurrentLocation.getLongitude());
                }
            }
        }
    }
}
