package com.example.use.mapapimark.MapAPI;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.use.mapapimark.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.Collection;

/**
 * 클러스트 기능 관리 및 제공 클래스
 */
public class ClusterMaker implements ClusterManager.OnClusterClickListener<SNSInfoMaker>,
                                     ClusterManager.OnClusterItemClickListener<SNSInfoMaker> {

    private Context mContext;
    private GoogleMap mGoogleMap;
    private ClusterManager<SNSInfoMaker> mClusterManager;       // 클러스터 관리 객체

    // 생성자
    public ClusterMaker(Context mContext, GoogleMap mGoogleMap) {
        this.mContext = mContext;
        this.mGoogleMap = mGoogleMap;

        mClusterManager = new ClusterManager<SNSInfoMaker>(mContext,mGoogleMap);    // 클러스터링 관리 객체
        mGoogleMap.setOnCameraIdleListener(mClusterManager);            // 구글맵 카메라 움직임 리스너로 클러스터링 기능 적용
        mGoogleMap.setOnMarkerClickListener(mClusterManager);           // 구글맵 마커 클릭 리스너

        mClusterManager.setRenderer(new MakerClusterRenderer());        // 클러스터링 랜더링
        mClusterManager.setOnClusterClickListener(this);                // 클러스터링 클릭시 리스너
        mClusterManager.setOnClusterItemClickListener(this);            // 마커 클릭시 리스너
        mClusterManager.cluster();
    }

    // 클러스링 된 마커 클릭한 경우 리스너
    @Override
    public boolean onClusterClick(Cluster<SNSInfoMaker> cluster) {
        Toast.makeText(mContext,"onClusterClick" + cluster.getSize(), Toast.LENGTH_SHORT).show();
        return true;
    }

    // 일반 마커 클릭한 경우 리스너
    @Override
    public boolean onClusterItemClick(SNSInfoMaker snsInfoMaker) {
        Toast.makeText(mContext,"onClusterItemClick", Toast.LENGTH_SHORT).show();
        return true;
    }

    // 한개 마커 추가
    public void addSNSInfoMaker(SNSInfoMaker snsInfoMaker) { mClusterManager.addItem(snsInfoMaker); }

    // 리스트로 만든것을 마커 추가
    public void addSNSInfoMakerList(Collection<SNSInfoMaker> snsInfoMaker) { mClusterManager.addItems(snsInfoMaker); }

    // 마커들 전부 삭제
    public void clerMakerAll() { mClusterManager.clearItems(); }

    public void resetCluster() { mClusterManager.cluster(); }

    /**
    * 클러스터링 마커 랜더링 기능 제공 객체
    * */
    private class MakerClusterRenderer extends DefaultClusterRenderer<SNSInfoMaker> {

        View mMakerView;
        TextView mTextMarker;

        public MakerClusterRenderer() {
            super(mContext, mGoogleMap, mClusterManager);
            mMakerView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.maker_view,null);
            mTextMarker = (TextView) mMakerView.findViewById(R.id.textmarker);
        }

/*        // 클러스터링 아닌 일반 마커 랜더링
        @Override
        protected void onBeforeClusterItemRendered(SNSInfoMaker item, MarkerOptions markerOptions) {
            setContentImage(false);     // 말풍선 이미지 올리기
            mTextMarker.setText("1");     // 클러스터링 갯수 TextView 출력
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createBitmap(mMakerView)));       // 마커 아이콘 올리기
        }*/

        // 클러터링 하는 마커 랜더링
        @Override
        protected void onBeforeClusterRendered(Cluster<SNSInfoMaker> cluster, MarkerOptions markerOptions) {
            setContentImage(false);                     // 말풍선 이미지 올리기
            mTextMarker.setText(String.valueOf(cluster.getSize()));     // 클러스터링 갯수 TextView 출력
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createBitmap(mMakerView)));       // 마커 아이콘 올리기
        }

        // 말풍선 이미지 View 올리기 (secret : 울타리 글이면 빨간 말풍선)
        private void setContentImage(boolean secret) {
            // false : 일반 말풍선, true : 울타리 글 말풍선
            int _drawableRes = (!secret) ? R.drawable.content_balloon : R.drawable.secret_balloon;

            // 젤리빈 이상은 setBackground, 미만은 setBackgroundDrawable 사용 권장
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mTextMarker.setBackground(ContextCompat.getDrawable(mContext, _drawableRes));
            } else {
                mTextMarker.setBackgroundDrawable(ContextCompat.getDrawable(mContext, _drawableRes));
            }
        }

        // View를 Bitmap으로 변환
        private Bitmap createBitmap(View view) {

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
            view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
            view.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);

            return bitmap;
        }

    }   // MakerClusterRenderer class -- Line END --

}   // ClusterMaker class -- Line END --
