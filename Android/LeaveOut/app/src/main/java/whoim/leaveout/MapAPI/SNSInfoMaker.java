package whoim.leaveout.MapAPI;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * 마커에 필요한 정보
 */

public class SNSInfoMaker implements ClusterItem {

    private LatLng mMakerLocation;      // 마커의 좌표 위치

    public SNSInfoMaker(LatLng mMakerLocation) {
        this.mMakerLocation = mMakerLocation;
    }

    public LatLng getmMakerLocation() {
        return mMakerLocation;
    }

    public void setmMakerLocation(LatLng mMakerLocation) {
        this.mMakerLocation = mMakerLocation;
    }

    @Override
    public LatLng getPosition() {
        return mMakerLocation;
    }

}
