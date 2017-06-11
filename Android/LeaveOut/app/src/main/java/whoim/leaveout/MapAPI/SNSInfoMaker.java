package whoim.leaveout.MapAPI;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * 마커에 필요한 정보
 */

public class SNSInfoMaker implements ClusterItem {

    private int mContentNum;            // 마커 번호(게시글 번호)
    private LatLng mMakerLocation;      // 마커의 좌표 위치

    public SNSInfoMaker(LatLng mMakerLocation, int mContentNum) {
        this.mMakerLocation = mMakerLocation;
        this.mContentNum = mContentNum;
    }

    public LatLng getMakerLocation() {
        return mMakerLocation;
    }

    public void setMakerLocation(LatLng mMakerLocation) {
        this.mMakerLocation = mMakerLocation;
    }

    public int getContentNum() { return mContentNum; }

    public void setContentNum(int mContentNum) { this.mContentNum = mContentNum; }

    @Override
    public LatLng getPosition() {
        return mMakerLocation;
    }
}
