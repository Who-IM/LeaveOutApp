package whoim.leaveout.MapAPI;

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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;

import whoim.leaveout.R;

/**
 * 클러스트 기능 관리 및 제공 클래스
 */
public class ClusterMaker {

    private Context mContext;
    private GoogleMap mGoogleMap;
    private ClusterManager<SNSInfoMaker> mClusterManager;       // 클러스터 관리 객체
    private ArrayList<SNSInfoMaker> mFenceList = new ArrayList();        // 울타리글 리스트

    // 생성자
    public ClusterMaker(Context mContext, GoogleMap mGoogleMap) {
        this.mContext = mContext;
        this.mGoogleMap = mGoogleMap;

        mClusterManager = new ClusterManager<SNSInfoMaker>(mContext,mGoogleMap);    // 클러스터링 관리 객체
        mGoogleMap.setOnCameraIdleListener(mClusterManager);            // 구글맵 카메라 움직임 리스너로 클러스터링 기능 적용
        mGoogleMap.setOnMarkerClickListener(mClusterManager);           // 구글맵 마커 클릭 리스너

        mClusterManager.setRenderer(new MakerClusterRenderer());        // 클러스터링 랜더링
        mClusterManager.cluster();
    }

    // 클러스터링 클릭시 리스너 셋팅
    public void setOnClusterClickListener(ClusterManager.OnClusterClickListener<SNSInfoMaker> onClusterClickListener) {mClusterManager.setOnClusterClickListener(onClusterClickListener);}

    // 마커 클릭시 리스너
    public void setOnClusterItemClickListener(ClusterManager.OnClusterItemClickListener<SNSInfoMaker> onClusterItemClickListener) {mClusterManager.setOnClusterItemClickListener(onClusterItemClickListener);}

    // 한개 마커 추가
    public void addSNSInfoMaker(SNSInfoMaker snsInfoMaker) {
        if(snsInfoMaker.isFence()) mFenceList.add(snsInfoMaker);
        mClusterManager.addItem(snsInfoMaker);
    }

    // 마커들 전부 삭제
    public void clearMakerAll() {
        mClusterManager.clearItems();           // 클러스터링 마커 아이템 삭제
        mFenceList.clear();                     // 울타리 리스트 아이템 삭제
    }

    public void resetCluster() {mClusterManager.cluster(); }

    public void removeFenceAll() {
        for(SNSInfoMaker snsInfoMaker : mFenceList) {
            mClusterManager.removeItem(snsInfoMaker);
        }
        mFenceList.clear();
        mClusterManager.cluster();
    }

    public ArrayList<SNSInfoMaker> getmFenceList() {
        return mFenceList;
    }

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

        // 클러스터링 아닌 일반 마커 랜더링
        @Override
        protected void onBeforeClusterItemRendered(SNSInfoMaker item, MarkerOptions markerOptions) {
            setContentImage(item.isFence());     // 말풍선 이미지 올리기(true : 빨간 풍선(울타리글) false : 일반 풍선(일반 게시글))
            mTextMarker.setText("1");     // 클러스터링 갯수 TextView 출력
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createBitmap(mMakerView)));       // 마커 아이콘 올리기
        }

        // 클러터링 하는 마커 랜더링
        @Override
        protected void onBeforeClusterRendered(Cluster<SNSInfoMaker> cluster, MarkerOptions markerOptions) {
            boolean fence = false;
            for (SNSInfoMaker snsInfoMaker : cluster.getItems()) {
                if (snsInfoMaker.isFence()) {
                    fence = true;
                    break;
                }
            }
            setContentImage(fence);                     // 말풍선 이미지 올리기
            mTextMarker.setText(String.valueOf(cluster.getSize()));     // 클러스터링 갯수 TextView 출력
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createBitmap(mMakerView)));       // 마커 아이콘 올리기
        }

        // 클러스터링 몇개부터 할것인지 여부
        @Override
        protected boolean shouldRenderAsCluster(Cluster<SNSInfoMaker> cluster) {
            return cluster.getSize() > 1;
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