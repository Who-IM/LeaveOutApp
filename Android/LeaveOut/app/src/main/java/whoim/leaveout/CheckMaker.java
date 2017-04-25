package whoim.leaveout;


import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class CheckMaker implements ClusterItem {

    private LatLng location;
    private String title;
    private String snippet;

    public CheckMaker(LatLng location, String title, String snippet) {
        this.location = location;
        this.title = title;
        this.snippet = snippet;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return this.location;
    }

}

class OwnRenring extends DefaultClusterRenderer<CheckMaker> {

    public OwnRenring(Context context, GoogleMap map, ClusterManager<CheckMaker> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(CheckMaker item, MarkerOptions markerOptions) {
        markerOptions.title(item.getTitle());
        markerOptions.snippet(item.getSnippet());

        super.onBeforeClusterItemRendered(item,markerOptions);
    }
}