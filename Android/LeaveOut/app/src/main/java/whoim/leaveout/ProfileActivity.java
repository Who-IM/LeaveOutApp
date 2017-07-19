package whoim.leaveout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import whoim.leaveout.Adapter.CommentAdapter;
import whoim.leaveout.Adapter.ContentAdapter;
import whoim.leaveout.Loading.LoadingDialogBin;
import whoim.leaveout.Loading.LoadingSQLDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.MapAPI.ClusterMaker;
import whoim.leaveout.MapAPI.SNSInfoMaker;
import whoim.leaveout.Server.ImageDownLoad;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.Server.WebControll;
import whoim.leaveout.Services.FomatService;
import whoim.leaveout.User.UserInfo;

import static whoim.leaveout.MapAPI.MapAPIActivity.mCurrentLocation;

// 환경설정
public class ProfileActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ClusterManager.OnClusterClickListener<SNSInfoMaker>,
        ClusterManager.OnClusterItemClickListener<SNSInfoMaker> {

    protected GoogleMap mGoogleMap;                 // 구글 맵
    protected GoogleApiClient mGoogleApiClient;     // 구글 맵 기능을 적용하는 서비스
    protected MapFragment mMapFragment;       // 맵 프래그먼트(맵 띄우는 것)
    protected ClusterMaker mClusterMaker;           // 클러스터 기능 관리 및 제공 객체
    LatLngBounds.Builder builder;      // 위도경도 그룹

    private RadioGroup radioGroup;

    // 프로필 이미지 변경
    private ImageButton mProfileSetImage;

    // list 게시글
    private ListView mContentlist = null;
    private ContentAdapter mContentAdapter;

    // comment list
    View header = null; // 리스트뷰 헤더

    //tab
    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;
    profile_tab tab;

    //갤러리
    ImageView iv = null;
    ImageView profile_image = null;
    Uri photoUri;
    Bitmap thumbImage = null;

    int menuCount = 0;  //매뉴 옵션 아이템 순서

    //like 버튼
    UserInfo userInfo = UserInfo.getInstance();     // 유저 정보
    Bitmap bitmap = userInfo.getProfile();

    ArrayList<Integer> cate = null;
    boolean tab_flag = false;

    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance(); // sql에 필요한 데이터 그룹
    JSONObject request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        radioGroupInit();          // 라디오 버튼 초기화

        buildGoogleApiClient();       // GooglePlayServicesClient 객체를 생성
        mGoogleApiClient.connect();   // connect 메소드가 성공하면 onConnect() 콜백 메소드를 호출

        cate = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#00FFFFFF"));   //제목 투명하게
        setSupportActionBar(toolbar);   //액션바와 같게 만들어줌
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.


        mProfileSetImage = (ImageButton) findViewById(R.id.profile_set_Image);

        if(userInfo.getFacebookId() != null) mProfileSetImage.setVisibility(View.GONE);     // 페이스북 일 경우 이미지변경 버튼 없애기

        if(bitmap == null)  // 프로필 사진이 없을 경우
            bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.basepicture)).getBitmap();      // 기본값

        // 초기설정 (db필요)
        init(bitmap, userInfo.getName(), userInfo.getEmail());

        header = getLayoutInflater().inflate(R.layout.profile_header, null);  // 프로필 위의 지도(listview header지정하여 스크롤 가능하게함)

        // 모아보기 listview 셋팅
        setProfile();

        // tab layout 설정
        tabLayout = (TabLayout) findViewById(R.id.profile_tab);
        selectCategorySQLData();

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // ViewPager 설정
        viewPager = (ViewPager) findViewById(R.id.profile_pager);

        //tab layout 리스너 등록
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() != 0) {
                    int s = tab.getPosition() - 1;
                    meContentData(cate.get(s));
                    tab_flag = true;
                } else if(tab_flag) {
                    meContentData(0);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        meContentData(0);
    }

    private void radioGroupInit() {
        radioGroup = (RadioGroup) findViewById(R.id.profile_radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioMarkerSet();
            }
        });
    }

    private void radioMarkerSet() {
        mapMakerClear();
        if(radioGroup.getCheckedRadioButtonId() == R.id.profile_RadioContent) {
            ContentMarkerSQLData();             // 미커 데이터베이스 SQL 문
        }
        else if(radioGroup.getCheckedRadioButtonId() == R.id.profile_RadioCheck) {
            checkViewSQLData();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;

        mGoogleMap.setMyLocationEnabled(false);     // 자기 자신 위치 마커 셋팅 x
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);   // 자기 자신 위치 버튼 셋팅 x
        mGoogleMap.setOnMyLocationButtonClickListener(null);        // 자기 자신 위치 버튼 리스너 셋팅 x

        mClusterMaker = new ClusterMaker(this, mGoogleMap); // 클러스터 기능 관리 및 제공 객체
        mClusterMaker.setOnClusterClickListener(this);          // 클러스터링 클릭시 리스너 셋팅
        mClusterMaker.setOnClusterItemClickListener(this);          // 마커 클릭시 리스너
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(false);

        radioMarkerSet();

    }

    @Override
    public void onConnected(Bundle bundle) {
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.profile_google_map);   // 레이아웃에 구글 맵 프로그먼트 아이디 가져오기
        mMapFragment.getMapAsync(this);      // 구글 맵을 실행시켜 맵을 띄우기(onMapReady 콜백 메소드)
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    // GooglePlayServicesClient 객체를 생성 메서드(구글 맵 api 가져오기)
    public synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    // 카메라 이동 기본 값
    private void defaultmoveCamera() {
        if (mCurrentLocation != null) {     // 위치 정보가 있을경우
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 16));       // 초기 화면 셋팅
        } else {        // 상태정보가 없을경우 (기본 값 셋팅)
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.56, 126.97), 16));
        }
    }

    // 게시글 마커 지도에 표시 및 체크 한 것 가져오기
    private void ContentMarkerSQLData() {
        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 2;
            }
            @Override
            public JSONObject getSQLQuery() {
                return SQLDataService.getSQLJSONData("select content_num,loc_x,loc_y,fence,visibility from content where user_num = " + userInfo.getUserNum(),-1,"select");
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }
            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                if(responseData == null) {
                    Toast.makeText(getApplicationContext(),"서버에 접속할수 없습니다.",Toast.LENGTH_LONG).show();
                    return;
                }
                JSONArray result = responseData.get(0).getJSONArray("result");
                if(result.length() == 0) {
                    defaultmoveCamera();        // 카메라 이동 기본 값
                    return;        // 마커가 없을경우 리턴
                }
                addMarkerList(result);      // 마커 표시
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),200));      // 초기 화면 셋팅
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener,ProgressDialog.STYLE_SPINNER,null);
    }

    protected void addMarkerList(JSONArray result) throws JSONException {
        builder = LatLngBounds.builder();
        for(int i = 0; i < result.length(); i++) {
            JSONObject data = result.getJSONObject(i);
            int contentnum =  data.getInt("content_num");
            double locX = data.getDouble("loc_x");
            double locY = data.getDouble("loc_y");
            boolean fence = data.getBoolean("fence");
            int visibility = data.getInt("visibility");
            LatLng latLng = new LatLng(locX,locY);
            mClusterMaker.addSNSInfoMaker(new SNSInfoMaker(latLng,contentnum, fence, visibility));
            builder.include(latLng);
        }
        mClusterMaker.resetCluster();
    }

    private void checkViewSQLData() {

        final String sql = "select * " +
                "from checks " +
                "where (user_num = ?);";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(UserInfo.getInstance().getUserNum());
                return SQLDataService.getDynamicSQLJSONData(sql,mDataQueryGroup,-1,"select");
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                JSONArray jspn = responseData.get(0).getJSONArray("result");
                if(jspn.length() == 0) {
                    defaultmoveCamera();        // 카메라 이동 기본 값
                    return;        // 마커가 없을경우 리턴
                }
                builder = LatLngBounds.builder();
                Location location = new Location("checks");
                for(int i =0; i < jspn.length(); i++) {
                    JSONObject j = jspn.getJSONObject(i);
                    double x = j.getDouble("chk_x");
                    double y = j.getDouble("chk_y");
                    LatLng latLng = new LatLng(x,y);
                    location.setLatitude(x);
                    location.setLongitude(y);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(FomatService.getCurrentAddress(getApplicationContext(),location));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.checkimg));
                    mGoogleMap.addMarker(markerOptions);
                    builder.include(latLng);
                }
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),200));      // 초기 화면 셋팅
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener, ProgressDialog.STYLE_SPINNER,null);
    }

    // 클러스링 된 마커 클릭한 경우 리스너
    @Override
    public boolean onClusterClick(Cluster<SNSInfoMaker> cluster) {
        String query = SQLDataService.getDynamicQuery(cluster.getSize());       // ? string 만들기

        mDataQueryGroup.clear();
        Iterator iterator = cluster.getItems().iterator();      // 클러스터링 아이템(마커) 배열 꺼내기
        while (iterator.hasNext()) {
            SNSInfoMaker snsInfoMaker = (SNSInfoMaker) iterator.next();     // 한개씩 가져오기
            mDataQueryGroup.addInt(snsInfoMaker.getContentNum());
        }
        selectContentSQL(query);        //  마커 클릭시 이동
        return true;
    }

    // 일반 마커 클릭한 경우 리스너
    @Override
    public boolean onClusterItemClick(SNSInfoMaker snsInfoMaker) {
        String query = SQLDataService.getDynamicQuery(1);       // ? string 만들기
        mDataQueryGroup.clear();
        mDataQueryGroup.addInt(snsInfoMaker.getContentNum());
        selectContentSQL(query);        //  마커 클릭시 이동
        return true;
    }

    private void selectContentSQL(String query) {

        final String sql = "select content_num, name, view_cnt, rec_cnt, reg_time,address,files,profile " +
                "from content inner join user " +
                "on content.user_num = user.user_num " +
                "where content_num in (" + query + ")";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }
            @Override
            public JSONObject getSQLQuery() {
                JSONObject data = SQLDataService.getDynamicSQLJSONData(sql, mDataQueryGroup, -1, "select");
                SQLDataService.putBundleValue(data,"download","context","files");
                return SQLDataService.putBundleValue(data,"download","context2","profile");
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }
            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                if(responseData.get(0) != null) {
                    Intent intent = new Intent(getApplicationContext(), ViewArticleActivity.class);
                    intent.putExtra("responseData", responseData.get(0).toString());
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                else Toast.makeText(getApplicationContext(),"잠시후 다시 시도해 주십시오.",Toast.LENGTH_SHORT).show();
            }
        };
        LoadingSQLDialog.SQLSendStart(this, loadingSQLListener,ProgressDialog.STYLE_SPINNER, null);
    }

    @Override
    protected void onStop() {
        mapMakerClear();
        super.onStop();
    }

    // 구글맵 마커 전부 삭제
    private void mapMakerClear() {
        if(mGoogleMap != null) mGoogleMap.clear();
        if(mClusterMaker != null) mClusterMaker.clearMakerAll();
    }


    /*//옵션 버튼
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 메뉴버튼이 처음 눌러졌을 때 실행되는 콜백메서드
        // 메뉴버튼을 눌렀을 때 보여줄 menu 에 대해서 정의
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 옵션 메뉴가 화면에 보여지는 메서드

        if (menuCount == 0) //가까운 위치 누를시 버튼 비활성화 그외 다 활성화
        {
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(true);
            menu.getItem(2).setEnabled(true);
            menu.getItem(3).setEnabled(true);
        }

        else if(menuCount == 1) //최신글 누를시 버튼 비활성화 그외 다 활성화
        {
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(false);
            menu.getItem(2).setEnabled(true);
            menu.getItem(3).setEnabled(true);
        }

        else if(menuCount == 2) //조회수 누를시 버튼 비활성화 그외 다 활성화
        {
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(true);
            menu.getItem(2).setEnabled(false);
            menu.getItem(3).setEnabled(true);
        }

        else if(menuCount == 3) //추천수 누를시 버튼 비활성화 그외 다 활성화
        {
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(true);
            menu.getItem(2).setEnabled(true);
            menu.getItem(3).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 메뉴의 항목을 선택(클릭)했을 때 호출되는 콜백메서드
        int id = item.getItemId();

        switch (id) {
            case R.id.profile_menu_location:
                Toast.makeText(getApplicationContext(), "가까운 위치 순서대로", Toast.LENGTH_SHORT).show();
                menuCount = 0;
                return true;

            case R.id.profile_menu_time:
                Toast.makeText(getApplicationContext(), "최신글 순서대로", Toast.LENGTH_SHORT).show();
                menuCount = 1;
                return true;

            case R.id.profile_menu_view:
                Toast.makeText(getApplicationContext(), "조회수 순서대로", Toast.LENGTH_SHORT).show();
                menuCount = 2;
                return true;

            case R.id.profile_menu_recommended:
                Toast.makeText(getApplicationContext(), "추천수 순서대로", Toast.LENGTH_SHORT).show();
                menuCount = 3;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
    // 옵션 버튼 끝

    protected class profile_tab
    {
        protected profile_tab(String text)
        {
            tabLayout.addTab(tabLayout.newTab().setText(text));
        }
    }

    // 모아보기 listview 셋팅
    private void setProfile() {
        // 메뉴
        mContentlist = (ListView) findViewById(R.id.proflie_list);
        mContentlist.addHeaderView(header);

        mContentAdapter = new ContentAdapter(this);
        mContentlist.setAdapter(mContentAdapter);
    }

    /* 초기설정 첫번째는 사진 : db에서
                두번째는 이름 : db에서
                세번째는 이메일 : db에서  */
    public void init(Bitmap image, String name, String email) {
        // 프로필 사진
        profile_image = (ImageView) findViewById(R.id.profile_title_image);
        profile_image.setImageBitmap(image);

        // 프로필 이름
        TextView tx1 = (TextView) findViewById(R.id.profile_title_name);
        tx1.setText(name);

        // 이메일
        TextView tx2 = (TextView) findViewById(R.id.profile_title_email);
        tx2.setText(email);
    }

    // 뒤로가기
    public void Back(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(intent);
    }

    //갤러리 불러오기
    public void profileAddImage(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK); //ACTION_PICK 즉 사진을 고르겠다!
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, 1);
    }

    //갤러리 기능 활성화
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //갤러리 창을 종료 했을 경우
        if (resultCode != RESULT_OK) {
            Toast.makeText(ProfileActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }

        if(data==null){
            return;
        }
        photoUri = data.getData();
        try {
            imageExtraction();
        } catch (Exception e) {}
    }

    //이미지 추출
    protected void imageExtraction() throws IOException {
        if(thumbImage != null) thumbImage.recycle();
        ((BitmapDrawable) profile_image.getDrawable()).getBitmap().recycle();

        //bitmap 형태의 이미지로 가져오기 위해 Thumbnail을 추출.
        iv = (ImageView) findViewById(R.id.profile_title_image);
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
        thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 600, 400);  //사진 크기를 조절
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs); //이미지가 클 경우 OutOfMemoryException 발생이 예상되어 압축

        profile_image.setImageBitmap(thumbImage);
        bs.close();
        bitmap.recycle();
        bitmap = null;

        profileUpload();
    }

    private void profileUpload() {
        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }
            @Override
            public JSONObject getSQLQuery() {
                return null;
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return profileImageJson();
            }
            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                if(responseData != null) {
                    Toast.makeText(getApplicationContext(),"등록 되었습니다.",Toast.LENGTH_SHORT).show();
                    userInfo.setProfile(thumbImage);
                }
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener, ProgressDialog.STYLE_SPINNER,null);
    }

    private JSONObject profileImageJson() {
        final UserInfo userInfo = UserInfo.getInstance();
        JSONObject data = new JSONObject();
        SQLDataService.putBundleValue(data, "upload", "usernum", userInfo.getUserNum());                 // 번들 데이터 더 추가(유저 id)
        SQLDataService.putBundleValue(data, "upload", "path", "user");
        SQLDataService.putBundleValue(data,"upload","context","image");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(FomatService.getStringFromBitmap(thumbImage));
        SQLDataService.putBundleValue(data, "upload", "imagecount", 1);
        SQLDataService.putBundleValue(data, "upload", "array", jsonArray);
        return data;
    }

    private void meContentData(final int tab_position) {

        final String sql;

        if(tab_position == 0) {
            sql = "select content_num, name, view_cnt, rec_cnt, reg_time,address,files " +
                    "from content inner join user " +
                    "on content.user_num = user.user_num " +
                    "where user.user_num = " + userInfo.getUserNum();
        } else {
            sql = "select content_num, name, view_cnt, rec_cnt, reg_time,address,files " +
                  "from content inner join user " +
                  "on content.user_num = user.user_num " +
                  "where content.content_num in (select content_num " +
                                                 "from cate_data " +
                                                 "where cate_seq = ?);";
        }

        if(tab_position == 0) {
            request = SQLDataService.getSQLJSONData(sql, -1, "select");
        } else {
            mDataQueryGroup.clear();
            mDataQueryGroup.addInt(tab_position);
            request = SQLDataService.getDynamicSQLJSONData(sql, mDataQueryGroup, -1, "select");
            mContentAdapter.removeall();
        }

       new LoadingDialogBin(this) {

           @Override
           protected Void doInBackground(Void... params) {

               SQLDataService.putBundleValue(request, "download", "context", "files");
               result.add(new WebControll().WebLoad(request));
               try {
                   if (result.get(0) != null && result.get(0).getJSONArray("result").length() != 0) {            // 게시글 가져오기
                       JSONArray resultData = result.get(0).getJSONArray("result");   // 결과값
                       for (int i = 0; i < resultData.length(); i++) {
                           CommentAdapter commentAdapter = null;                   // 댓글 어댑터
                           JSONObject contentdata = resultData.getJSONObject(i);   // 게시글 데이터

                           ArrayList<String> imagelist = new ArrayList();          // 이미지 넣을 데이터
                           JSONArray imageArray = resultData.getJSONObject(i).getJSONArray("image");       // 게ik시글에서 이미지 가져오기
                           for (int j = 0; j < imageArray.length(); j++) {
                               imagelist.add(imageArray.getString(j));             // 이미지 배열에 넣기
                           }

                           int contentnum = contentdata.getInt("content_num");     // 게시글 번호 가져오기
                           String name = contentdata.getString("name");
                           String address = contentdata.getString("address");
                           String reg_time = contentdata.getString("reg_time");
                           reg_time = reg_time.substring(0,reg_time.length()-2);
                           String rec_cnt = contentdata.getString("rec_cnt");
                           String view_cnt = contentdata.getString("view_cnt");
                           String text = contentdata.getString("text");

                            // 댓글 부분
                           String commentsql = "select comm_num, rec_cnt, reg_time, files, name, profile, comment.user_num " +        // 댓글
                                   "from comment join user on comment.user_num = user.user_num " +
                                   "where content_num = " + contentnum;

                           request = SQLDataService.getSQLJSONData(commentsql, -1, "select");
                           SQLDataService.putBundleValue(request, "download", "context", "files");
                           SQLDataService.putBundleValue(request, "download", "context2", "profile");
                           JSONObject commentdata = new WebControll().WebLoad(request);     // SQL 돌리기
                           if (commentdata != null && commentdata.getJSONArray("result").length() != 0) {
                               Bitmap profile = null;                                   // 댓글 유저의 프로필
                               commentAdapter = new CommentAdapter(ProfileActivity.this);
                               JSONArray commentresult = commentdata.getJSONArray("result");
                               for (int j = 0; j < commentresult.length(); j++) {
                                   JSONObject resultdata = commentdata.getJSONArray("result").getJSONObject(j);
                                   profile = setProfile(resultdata);

                                   // 마지막에 줄띄우기 잘라내기

//                                   String temptext = resultdata.getString("text").substring(0,resultdata.getString("text").length()-2);
//                                   String temptext = resultdata.getString("text");
//                                   commentAdapter.addItem(contentnum, profile, resultdata.getString("name"), temptext, resultdata.getString("reg_time"), resultdata.getInt("user_num"));       // 어댑터 추가

//                                   String temptext = resultdata.getString("text").substring(0,resultdata.getString("text").length()-2);
                                   String temptext = resultdata.getString("text");
                                   temptext = temptext.substring(0, temptext.length()-2);
                                   String time = resultdata.getString("reg_time").substring(0,resultdata.getString("reg_time").length()-2);
                                   commentAdapter.addItem(contentnum, profile, resultdata.getString("name"), temptext, time, resultdata.getInt("user_num"));       // 어댑터 추가

                               }
                           }
                           Object[] objects = {contentnum, name, address, reg_time, rec_cnt, view_cnt, text, imagelist, commentAdapter};
                           publishProgress(objects);
                       }
                   }
               } catch (JSONException e) {
                   e.printStackTrace();
               }
               return null;
           }
           @Override
           protected void onProgressUpdate(Object... values) {

                   mContentAdapter.addItem(bitmap, (int) values[0], (String) values[1], (String) values[2], (String) values[3], (String) values[4],
                           (String) values[5], (String) values[6], (ArrayList<String>) values[7], bitmap, (CommentAdapter) values[8]);
           }
           @Override
           protected void onPostExecute(Void aVoid) {
               mContentlist.requestLayout();
               super.onPostExecute(aVoid);
           }
       }.execute();
    }

    public Bitmap setProfile(JSONObject data) throws JSONException {
        Bitmap bitmap = null;
        if(data.has("image2")) {      // 있는지 확인
            JSONArray profileUri = data.getJSONArray("image2");
            if (profileUri.length() != 0) {
                bitmap = ImageDownLoad.imageDownLoad(profileUri.getString(0));
            }
        }
        else {
            bitmap = ImageDownLoad.imageDownLoad(data.getString("profile"));
        }
        if (bitmap == null) {
            bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.basepicture, null)).getBitmap();
        }
        return bitmap;
    }

    @Override
    protected void onDestroy() {
        mContentAdapter.recycle();
        super.onDestroy();
    }


    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    private void selectCategorySQLData() {

        final String sql = "select * " +
                "from category " +
                "where (user_num = ?)";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(UserInfo.getInstance().getUserNum());
                return SQLDataService.getDynamicSQLJSONData(sql,mDataQueryGroup,-1,"select");
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                JSONArray jspn = responseData.get(0).getJSONArray("result");
                tab = new profile_tab("전체");
                for (int i = 0; i < jspn.length(); i++) {
                    JSONObject j = jspn.getJSONObject(i);
                    tab = new profile_tab(j.getString("cate_text"));
                    cate.add(j.getInt("cate_seq"));
                }
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener, ProgressDialog.STYLE_SPINNER,null);
    }
}