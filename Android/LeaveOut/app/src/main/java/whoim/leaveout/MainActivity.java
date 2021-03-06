package whoim.leaveout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import whoim.leaveout.FCMPush.FCMInstanceIDService;
import whoim.leaveout.Loading.LoadingDialogBin;
import whoim.leaveout.Loading.LoadingSQLDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.MapAPI.LocationBackground;
import whoim.leaveout.MapAPI.MapAPIActivity;
import whoim.leaveout.MapAPI.SNSInfoMaker;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.Server.WebControll;
import whoim.leaveout.Services.FomatService;
import whoim.leaveout.StartSetting.Permission;
import whoim.leaveout.User.UserInfo;

public class MainActivity extends MapAPIActivity {

    private InputMethodManager imm;
    LinearLayout search_layout;

    // List view
    private ListView searchList;

    // Listview Adapter
    ArrayAdapter<String> adapter_search;

    // Search EditText
    EditText inputSearch;

    // ArrayList for Listview
    ArrayList<HashMap<String, String>> productList;

    /* 구글 맵 API 정보 */
    private static final String CAMERA_POSITION = "camera_position_state_save";        // 액티비티 정지시 상태 저장(구글맵 카메라 위치)
    private static final String LOCATION = "location_state_save";                      // 액티비티 정지시 상태 저장(구글맵 GPS 위치)

    private BroadcastReceiver mBroadcastLocation;   // 자신 위치 정보 브로드 캐스트 수신용
    private boolean mBroadcastCheck;                // 브로드 캐스트 활성화 상태 확인

    private TextView mAddressView;      // 주소 텍스트 뷰
    //-------------------------------------

    // 툴바
    Toolbar toolbar;

    // 메뉴 관련 인스턴스
    private ListView list;
    private DrawerLayout Drawer;
    private ImageButton menu_btn;
    private menu_Adapter adapter = null;

    // 메뉴 친구목록
    private ListView menu_friend_list;
    private menu_friend_Adapter menu_adapter = null;
    View header = null;
    View footer = null;
    ImageButton friend_open_list;

    UserInfo userInfo = UserInfo.getInstance();     // 유저 정보
    Bitmap profile = null;      // 프로파일

    Uri photoUri;
    File photoFile = null;
    String imageFileName;

    // SQL
    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance();          // sql에 필요한 데이터 그룹

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreState(savedInstanceState);     // 상태 불러오기

        FCMInstanceIDService.sendRegistrationToServer(String.valueOf(userInfo.getUserNum()),FirebaseInstanceId.getInstance().getToken());

        // 화면 캡쳐 방지
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main_layout);

        // 인스턴스들 셋팅
        setInstance();

        super.buildGoogleApiClient();           // GooglePlayServicesClient 객체를 생성
        super.mGoogleApiClient.connect();     // connect 메소드가 성공하면 onConnect() 콜백 메소드를 호출

        // 툴바 설정
        setToolbar();

        // 검색 셋팅
        setSerach();

        // 메뉴 셋팅
//        setMenuCustom();

        // 검색 리스트뷰 숨기기
        search_layout.setVisibility(View.GONE);

        // 매뉴 툴바 메뉴 버튼 누를 시 이벤트 처리
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            // 메뉴화면 펼치기(layout 앞으로 이동)
            public void onClick(View v) {

                // 포커스가 검색란에 있으면
                if(inputSearch.isFocused()) {
                    hideKeyboard();  // 키보드 숨기기
                    inputSearch.clearFocus();  // 포커스 해제
                } else {
                    search_layout.setVisibility(View.GONE); // 검색 창 리스트 숨기기
                    list.setVisibility(View.VISIBLE);        // 메뉴 보이게하기
                    Drawer.openDrawer(GravityCompat.START); // 펼치기
                }
            }
        });

/*        // 메뉴화면켯을때 빈화면 클릭시
        Drawer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                list.setVisibility(View.GONE);
                search_layout.setVisibility(View.GONE);
                Drawer.closeDrawer(GravityCompat.START); // 펼치기
                return true;
            }
        });*/
        Permission.cameraCheckPermissions(this);

        // 푸시(알림)로/으로 진입 했을시 어떤 액티비티로 향할지 판단하여 이동
        if(getIntent() != null) {
            String action = getIntent().getStringExtra("moveAction");
            if(action != null) {
                if (action.equals("FriendRequestActivity")) {       // 친구 추가 알림
                    Intent intent = new Intent(getApplicationContext(), FriendRequestActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }
            if(getIntent().getExtras() != null) {
                getIntent().removeExtra("moveAction");
            }
        }

    }

    // 인스턴스 셋팅
    private void setInstance() {
        mAddressView = (TextView) findViewById(R.id.main_location); // 지도 중간에 있는 자기위치
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //키보드 숨기기위해 인풋메니저 등록

        // 매뉴 관련 인스턴스
        list = (ListView)findViewById(R.id.main_menu);
        Drawer = (DrawerLayout)findViewById(R.id.main_drawer);
        menu_btn = (ImageButton)findViewById(R.id.menu_btn); // 메뉴 버튼
        header = getLayoutInflater().inflate(R.layout.menu_friend_list_header, null);
        footer = getLayoutInflater().inflate(R.layout.menu_friend_list_footer, null);

        // 메뉴에 글 목록 등록
        list.setOnItemClickListener(new DrawerItemClickListener());

        // 검색 관련 인스턴스
        searchList = (ListView) findViewById(R.id.main_search_list);
        inputSearch = (EditText) findViewById(R.id.main_search_text);
        search_layout = (LinearLayout) findViewById(R.id.main_search_layout);
        String products[] = {"Dell Inspiron", "HTC One X", "HTC Wildfire S", "HTC Sense", "HTC Sensation XE",
                "iPhone 4S", "Samsung Galaxy Note 800",
                "Samsung Galaxy S3", "MacBook Air" };

        // 검색 리스트 뷰
        adapter_search = new ArrayAdapter<String>(this, R.layout.main_search_item, R.id.product_name, products);
    }

    // 검색관련 셋팅
    private void setSerach() {

        // editText 터치시(포커스 갔을 시)
        inputSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                list.setVisibility(View.GONE);
                return false;
            }
        });

        // editText 글자 쳤을 시
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if(cs.toString().equals("")) {
                    searchList.setAdapter(null);
                    list.setVisibility(View.GONE);
                } else {
                    search_layout.setVisibility(View.VISIBLE);
                    searchList.setAdapter(adapter_search);
                    MainActivity.this.adapter_search.getFilter().filter(cs);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) { }
            @Override
            public void afterTextChanged(Editable arg0) {     }
        });
    }

    // 키보드 숨기기
    private void hideKeyboard(){
        imm.hideSoftInputFromWindow(inputSearch.getWindowToken(), 0);
    }

    // 폰트 설정
    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#00FFFFFF"));   //제목 투명하게
        setSupportActionBar(toolbar);//액션바와 같게 만들어줌
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
    }

    // 메뉴 커스텀 (나중에 DB받아서 수정)
    private void setMenuCustom() {

        adapter = new menu_Adapter(this);
        list.setAdapter(adapter);

        // 여기서 db데이터 넣기
//        adapter.addItem(((BitmapDrawable)getResources().getDrawable(R.drawable.basepicture, null)).getBitmap(),"허성문", "gjtjdans123@naver.com");

        if(userInfo.getProfile() != null)     // 프로필 사진이 있을경우
            profile = userInfo.getProfile();
        else      // 없을 경우
            profile = ((BitmapDrawable)getResources().getDrawable(R.drawable.basepicture, null)).getBitmap();

        adapter.addItem(profile, userInfo.getName(), userInfo.getEmail());
        adapter.addItem(((BitmapDrawable)getResources().getDrawable(R.drawable.profile_icon, null)).getBitmap(),"프로필", null);
        adapter.addItem(((BitmapDrawable)getResources().getDrawable(R.drawable.friends_icon, null)).getBitmap(),"친구목록", null);
        adapter.addItem(((BitmapDrawable)getResources().getDrawable(R.drawable.addfriend_icon, null)).getBitmap(),"친구요청", null);
        adapter.addItem(((BitmapDrawable)getResources().getDrawable(R.drawable.preferences_icon, null)).getBitmap(),"환경설정", null);
    }

    /* 매뉴 눌렀을 시 이벤트 처리
        0 : 자기 프로필(아무것도 처리 x)
        1 : 프로필로 이동
        2 : 친구 보기 이동
        3 : 환경설정 이동                  */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        Intent button;  //환경설정 버튼
        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            switch (position) {

/*                case 0:
                    // 임시
                    button = new Intent(getApplicationContext(), ViewArticleActivity.class);
                    startActivity(button);
                    break;*/
                case 1:     // 프로필으로 이동
                    button = new Intent(getApplicationContext(), ProfileActivity.class);
                    button.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(button);
                    break;
                case 2: // 친구목록으로 이동
                    button = new Intent(getApplicationContext(), FriendListActivity.class);
                    button.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(button);
                    break;
                case 3: // 친구요청으로 이동
                    button = new Intent(getApplicationContext(), FriendRequestActivity.class);
                    button.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(button);
                    break;
                case 4: // 환경설정으로 이동
                    button = new Intent(getApplicationContext(), PreferencesActivity.class);
                    button.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(button);
                    break;
            }
            Drawer.closeDrawer(list);
            list.setVisibility(View.GONE);
            search_layout.setVisibility(View.VISIBLE);
        }
    }

    // ------------ menu listview -------------
    private class menu_ViewHolder {
        public ImageView Image;
        public TextView name;
        public TextView count;
        public TextView email;
    }

    // 리스트뷰 어뎁터
    private class menu_Adapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<menu_ListData> mListData = new ArrayList<menu_ListData>();

        public menu_Adapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public ArrayList<menu_ListData> getListData() {return mListData;}

        // 생성자로 값을 받아 셋팅
        public void addItem(Bitmap image, String name, String email) {
            menu_ListData addInfo = null;
            addInfo = new menu_ListData();
            addInfo.Image = image;
            addInfo.name = name;
            addInfo.email = email;

            mListData.add(addInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            menu_ViewHolder holder = null;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null && position == 0) {
                holder = new menu_ViewHolder();
                convertView = inflater.inflate(R.layout.menu_profile_title, null);

                holder.Image = (ImageView) convertView.findViewById(R.id.menu_home_icon);
                holder.name = (TextView) convertView.findViewById(R.id.menu_profile_myname);
                holder.email = (TextView) convertView.findViewById(R.id.menu_profile_myemail);
            } else {
                holder = new menu_ViewHolder();
                convertView = inflater.inflate(R.layout.menu_item, null);

                holder.name = (TextView) convertView.findViewById(R.id.menu_profile_myname);
                holder.Image = (ImageView) convertView.findViewById(R.id.menu_icon);
                holder.name = (TextView) convertView.findViewById(R.id.menu_text);
                holder.email = (TextView) convertView.findViewById(R.id.menu_profile_myemail);


                //친구 추가
                if(position == 3) {
                    holder.count = (TextView) convertView.findViewById(R.id.menu_friend_count);
                    // 친구 요청 갯수 DB에서 확인
                    final menu_ViewHolder tempholder = holder;
                    new LoadingDialogBin(MainActivity.this) {
                        int friendcount = 0;

                        @Override
                        protected Void doInBackground(Void... params) {
                            String sql = "select count(user_num) as friendcount " +
                                    "from friend " +
                                    "where request = 1 and friend_num = " + userInfo.getUserNum();
                            JSONObject result = new WebControll().WebLoad(SQLDataService.getSQLJSONData(sql,-1,"select"));
                            try {
                                JSONArray responeArr = result.getJSONArray("result");
                                if(responeArr != null && responeArr.length() != 0) {
                                    friendcount = responeArr.getJSONObject(0).getInt("friendcount");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            if(friendcount != 0) {
                                tempholder.count.setVisibility(View.VISIBLE);
                                tempholder.count.setText(String.valueOf(friendcount));   //친구 추가 카운트
                            }
                            super.onPostExecute(aVoid);
                        }
                    }.execute();

                }
            }

            menu_ListData mData = mListData.get(position);

            // 이미지 처리
            if (mData.Image != null) {
                holder.Image.setVisibility(View.VISIBLE);
                holder.Image.setImageBitmap(mData.Image);
            }else{
                holder.Image.setVisibility(View.GONE);
            }

            // textView 처리
            holder.name.setText(mData.name);
            if(position == 0) {  // 매뉴 프로필 email
                holder.email.setText(mData.email);
            }
            return convertView;
        }
    }

    // 메뉴의 실제 데이터를 저장할 class
    class menu_ListData {
        public Bitmap Image;
        public String name;
        public String email;
    }
    // -------------------------------------- End menu listview -----------------------


    // ------------ menu listview -------------
    private class menu_friend_ViewHolder {
        public ImageView Image;
        public TextView name;
    }

    // 리스트뷰 어뎁터
    private class menu_friend_Adapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<menu_friend_ListData> ListData = new ArrayList<menu_friend_ListData>();

        public menu_friend_Adapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return ListData.size();
        }

        @Override
        public Object getItem(int position) {
            return ListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        // 생성자로 값을 받아 셋팅
        public void addItem(Drawable image, String name) {
            menu_friend_ListData addInfo = null;
            addInfo = new menu_friend_ListData();
            addInfo.Image = image;
            addInfo.name = name;

            ListData.add(addInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            menu_friend_ViewHolder holder;

            if (convertView == null) {
                holder = new menu_friend_ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.menu_friend_list_item, null);

                holder.Image = (ImageView) convertView.findViewById(R.id.menu_friend_list_image);
                holder.name = (TextView) convertView.findViewById(R.id.menu_friend_list_name);

                convertView.setTag(holder);
            }else{
                holder = (menu_friend_ViewHolder) convertView.getTag();
            }

            menu_friend_ListData mData = ListData.get(position);

            // 이미지 처리
            if (mData.Image != null) {
                holder.Image.setVisibility(View.VISIBLE);
                holder.Image.setImageDrawable(mData.Image);
            }else{
                holder.Image.setVisibility(View.GONE);
            }

            // textView 처리
            holder.name.setText(mData.name);

            return convertView;
        }
    }

    // 메뉴의 실제 데이터를 저장할 class
    class menu_friend_ListData {
        public Drawable Image;
        public String name;
        public String email;
    }
    // -------------------------------------- End menu listview -----------------------

    // 리스트뷰 펼처보기(한화면에)
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    // 뒤로가기
    @Override
    public void onBackPressed() {
        if(inputSearch.isFocused()) {
            inputSearch.clearFocus();  // 포커스 해제
        }

        // 메뉴 화면이 open 되있을 경우
        if (Drawer.isDrawerOpen(GravityCompat.START)) {
            list.setVisibility(View.GONE);
            search_layout.setVisibility(View.VISIBLE);
            Drawer.closeDrawer(GravityCompat.START); // 펼치기
        } else {
            // 나가기 버튼 눌렀을 시 구글 맵 기능 종료
            if(super.mGoogleApiClient.isConnected()) {
//            PendingIntent LocationIntent = PendingIntent.getService(this, 0, new Intent(this, LocationBackground.class), PendingIntent.FLAG_UPDATE_CURRENT);   // 다른 컴포넌트에게 인텐트 권한 주기
                LocationServices.FusedLocationApi.removeLocationUpdates(super.mGoogleApiClient, super.mLocationPendingIntent);
                super.mGoogleApiClient.disconnect();
                this.finish();
            }
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        // 메뉴 셋팅
        setMenuCustom();

        this.registerReceiver();        // 사용자 위치 브로드 캐스트 수신 설정

        // GPS 위치 가져오는 서비스 켜기
        if(super.mGoogleApiClient.isConnected()) {
            getDeviceLocation();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        if(mGoogleMap != null) mGoogleMap.clear();
        if(mClusterMaker != null) mClusterMaker.clearMakerAll();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
/*        // 백그라운드 브로드 캐스트 죽일때 사용
        if(mBroadcastCheck) {
            unregisterReceiver(mBroadcastLocation);
            mBroadcastCheck = false;
        }*/

        // 다른 액티비티로 넘어가거나 액티비티 종료시 버리기
        if(super.mGoogleApiClient.isConnected()) {
//            PendingIntent LocationIntent = PendingIntent.getService(this, 0, new Intent(this, LocationBackground.class), PendingIntent.FLAG_UPDATE_CURRENT);   // 다른 컴포넌트에게 인텐트 권한 주기
            LocationServices.FusedLocationApi.removeLocationUpdates(super.mGoogleApiClient, super.mLocationPendingIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
/*        for(menu_ListData menu_listData : adapter.getListData()) {
            if(menu_listData.Image != null) menu_listData.Image.recycle();
        }*/
/*        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());*/

        unregisterReceiver(mBroadcastLocation);     // 브로드캐스트 종료
    }

    // 전에 상태 저장 한 것 불러오기
    private void restoreState(Bundle savedInstanceState) {
        if(savedInstanceState != null) {        // 상태 저장 한 것이 있으면 불러오기
            super.mCurrentLocation = savedInstanceState.getParcelable(LOCATION);
            super.mCameraPosition = savedInstanceState.getParcelable(CAMERA_POSITION);
        }
    }

    // 액티비티 정지시 상태 저장
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(super.mGoogleMap != null) {
            outState.putParcelable(CAMERA_POSITION, super.mGoogleMap.getCameraPosition());
            outState.putParcelable(LOCATION, super.mCurrentLocation);
        }
        super.onSaveInstanceState(outState);
    }

    // 권한 요청 한뒤 어떻게 되어있는지 판단 (권한 확인을 메세지로 표시시)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            // 초기 셋팅 해서 확인 (테스트중)
            case Permission.REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "승인 완료", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this,"승인 거부",Toast.LENGTH_SHORT).show();
                }
                break;

            // 초기 셋팅때 거부한것을 다시 확인
            case Permission.DENIED_REQUEST_CODE:
                super.mLocationPermissionGranted = false;
                if (Permission.verifyPermission(grantResults)) {
                    super.mLocationPermissionGranted = true;
                    Toast.makeText(this, "GPS 승인 완료", Toast.LENGTH_SHORT).show();
                    getDeviceLocation();
                }
                else Toast.makeText(this, "GPS 승인 거부", Toast.LENGTH_SHORT).show();

                super.updateLocationUI();       // 구글맵 자신 GPS UI 셋팅
                break;

            case Permission.MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(Permission.CAMERA_PERMISSIONS[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(Permission.CAMERA_PERMISSIONS[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(Permission.CAMERA_PERMISSIONS[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                break;
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // 브로드 캐스트 만들기(로케이션)
    private void registerReceiver() {
        // 이미 브로드캐스트 수신 설정 했으면 안하기
        if (mBroadcastCheck == true) return;

        if (mBroadcastLocation == null) {
            // 사용자 위치 정보 브로드캐스트 수신 객체
            mBroadcastLocation = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, final Intent intent) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Location tempLocation = intent.getExtras().getParcelable(LocationBackground.EXTRA_CURRENT_LOCATION);        // 주소 가져오기
                            if (mCurrentLocation == null) {
                                MainActivity.super.mCurrentLocation = tempLocation;         // 위치 최신으로
                                circleSet();        // 원그리기
                                mAddressView.setText(FomatService.getCurrentAddress(getApplicationContext(), mCurrentLocation));       // View에 주소 표시
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 16));       // 초기 화면 셋팅
                                fenceSQLStart();
                            } else if (mCurrentLocation.getLatitude() != tempLocation.getLatitude() || mCurrentLocation.getLongitude() != tempLocation.getLongitude()) {   // 전과 후의 위치가 다르면 바꾸기
                                MainActivity.super.mCurrentLocation = tempLocation;         // 위치 최신으로
                                circleSet();        // 원그리기
                                mAddressView.setText(FomatService.getCurrentAddress(getApplicationContext(), mCurrentLocation));       // View에 주소 표시

                                if (mClusterMaker.getmFenceList().size() == 0) {     // 전에 울타리글 마커가 없었을 경우
                                    fenceSQLStart();        // 울타리글 가져와서 마커 추가하기
                                }
                                else {  // 전에 있었을 경우
                                    // 만약 마커 표시된 울타리글이 현재 위치에서 거리가 멀어진경우 지우고 다시 셋팅
                                    for (SNSInfoMaker snsInfoMaker : mClusterMaker.getmFenceList()) {
                                        Location fenceLocation = new Location("snsInfo");
                                        fenceLocation.setLatitude(snsInfoMaker.getPosition().latitude);
                                        fenceLocation.setLongitude(snsInfoMaker.getPosition().longitude);
                                        if (distance < mCurrentLocation.distanceTo(fenceLocation)) {        // 설정한 반경 100m 안에 울타리글이 벗어나면 다시 울타리글 만들기
                                            mClusterMaker.removeFenceAll();     // 울타리글 마커 지우기
                                            fenceSQLStart();        // 울타리글 가져와서 마커 추가하기
                                            break;
                                        }   // sub if -- END --
                                    }   // for -- END --
                                }
                            }   //if -- END --
                            else if(mAddressView.getText().toString().equals("")) {      // 그대로면 기본 셋팅
                                MainActivity.super.mCurrentLocation = tempLocation;         // 위치 최신으로
                                mAddressView.setText(FomatService.getCurrentAddress(getApplicationContext(), mCurrentLocation));       // View에 주소 표시
//                                circleSet();        // 원그리기
//                                fenceSQLStart();
                            }
                        }
                    });
                }   // onReceive -- END --
            };  // new BroadcastReceiver -- END --
        }
        // 사용자 위치 정보 브로드캐스트 수신 설정
        if(mBroadcastLocation != null) {
            registerReceiver(mBroadcastLocation, new IntentFilter(LocationBackground.ACTION_LOCATION_BROADCAST));
            mBroadcastCheck = true; // 브로드캐스트 설정 완료
        }
    }

    // 체크 db검색
    private void checkSelectAndInsertSQL() {
        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }
            @Override
            public JSONObject getSQLQuery() {
                String sql = "select chk_x, chk_y, expare_date, check_image from checks " +
                             "where user_num = ? AND check_image is null;";
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
                if(responseData.get(0).getJSONArray("result").length() < 3) {
                    checkInsertSQLData(false, "");
                }
                else {
                    Toast.makeText(getApplicationContext(), "체크는 최대 3개 까지만 가능합니다.", Toast.LENGTH_LONG).show();
                }
            }
        };

        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener,ProgressDialog.STYLE_SPINNER,null);
    }

    // 체크 넣기
    private void checkInsertSQLData(boolean image, String image_path) {
        String sql = "";

        // 사진체크
        if(image) {
            sql = "insert into checks(user_num,chk_x,chk_y,expare_date,check_image) values(?,?,?,curdate(),?)";
            mDataQueryGroup.clear();
            mDataQueryGroup.addInt(UserInfo.getInstance().getUserNum());
            mDataQueryGroup.addDouble(mCurrentLocation.getLatitude());
            mDataQueryGroup.addDouble(mCurrentLocation.getLongitude());
            mDataQueryGroup.addString(image_path);
        // 체크
        } else {
            sql = "insert into checks(user_num,chk_x,chk_y,expare_date) values(?,?,?,curdate())";
            mDataQueryGroup.clear();
            mDataQueryGroup.addInt(UserInfo.getInstance().getUserNum());
            mDataQueryGroup.addDouble(mCurrentLocation.getLatitude());
            mDataQueryGroup.addDouble(mCurrentLocation.getLongitude());
        }

        final String finalSql = sql;
        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                return SQLDataService.getDynamicSQLJSONData(finalSql,mDataQueryGroup,0,"update");
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                if(responseData.get(0).getInt("result") == 1)
                    Toast.makeText(getApplicationContext(), "체크되었습니다.", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), "잠시후 다시 시도해 주십시오.", Toast.LENGTH_LONG).show();
            }
        };

        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener,ProgressDialog.STYLE_SPINNER,null);
    }
      
    //사진 체크
   private void imagecheckSelectAndInsertSQL(final String image_path) {
        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }
            @Override
            public JSONObject getSQLQuery() {
                String sql = "select chk_x, chk_y, expare_date, check_image from checks " +
                             "where user_num = ? AND check_image like '/%';";
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
                if(responseData.get(0).getJSONArray("result").length() < 3) {
                    checkInsertSQLData(true, image_path);
                }
                else {
                    Toast.makeText(getApplicationContext(), "사진체크는 최대 3개 까지만 가능합니다.", Toast.LENGTH_LONG).show();
                }
            }
        };

        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener,ProgressDialog.STYLE_SPINNER,null);
    }

    // 위도좌측 상단,       위도우측 하단,       경도좌측 상단,       경도우측 하단
    // Double northeastLat, Double northeastLng, Double southwestLat, Double southwestLng
    private void contentsLocationSelectSQLData() {
        LatLng northeastLatLng = mGoogleMap.getProjection().getVisibleRegion().latLngBounds.northeast; // 화면 좌측상단부분의 LatLng
        LatLng southwestLatLng = mGoogleMap.getProjection().getVisibleRegion().latLngBounds.southwest; // 화면 우측하단부분의 LatLng
        final double northeastLat = northeastLatLng.latitude; // 화면 좌측상단부분의 위도
        final double northeastLng = northeastLatLng.longitude; // 화면 좌측상단부분의 경도
        final double southwestLat = southwestLatLng.latitude; //화면 우측하단부분의 위도
        final double southwestLng = southwestLatLng.longitude; //화면 우측하단부분의 경도
        mDataQueryGroup.clear();        // 초기화
        mDataQueryGroup.addDouble(southwestLat);
        mDataQueryGroup.addDouble(northeastLat);
        mDataQueryGroup.addDouble(southwestLng);
        mDataQueryGroup.addDouble(northeastLng);

        int count = 0;
        for(SNSInfoMaker snsInfoMaker : mClusterMaker.getmFenceList()) {
            count++;
            mDataQueryGroup.addInt(snsInfoMaker.getContentNum());
        }
        if(count == 0) {
            count = 1;
            mDataQueryGroup.addInt(0);
        }
        String query = SQLDataService.getDynamicQuery(count);       // sql 동적으로 ? 만들기


        final String mSelectSQL = "select content_num, name, view_cnt, rec_cnt, reg_time,address,files, profile, email " +
                "from content inner join user " +
                "on content.user_num = user.user_num " +
                "where (loc_x >= ? && loc_x <= ?) AND (loc_y >= ? && loc_y <= ?) AND (fence = false OR content_num in ("+ query +"))";     // 모아보기 sql

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }
            @Override
            public JSONObject getSQLQuery() {
                JSONObject data = SQLDataService.getDynamicSQLJSONData(mSelectSQL, mDataQueryGroup, -1, "select");             // select SQL 제이슨
                SQLDataService.putBundleValue(data,"download","context","files");
                SQLDataService.putBundleValue(data,"download","context2","profile");
                return data;
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }
            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                JSONArray result = responseData.get(0).getJSONArray("result");     // 결과 값 가져오기
                if(result.length() == 0) {
                    Toast.makeText(MainActivity.this,"자료가 없습니다.",Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), CollectActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("responseData",responseData.get(0).toString());
                startActivity(intent);
            }
        };
        LoadingSQLDialog.SQLSendStart(this, loadingSQLListener, ProgressDialog.STYLE_SPINNER, null);       // sql 시작
    }

    // 글쓰기, 체크, 모아보기 메뉴 onClick 메소드
    public void nextActivityButton(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.main_write:       // 글쓰기 버튼(글쓰기 액티비티 이동)
                if (GPSCheck()) {        // GPS서비스 확인
                    intent = new Intent(getApplicationContext(), WritingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("address", mAddressView.getText().toString());           // 주소 이름
                    intent.putExtra("loc", mCurrentLocation);                                // 주소 값 (위도,경도)
                    startActivity(intent);
                }
                break;
            case R.id.main_check:       // 체크 버튼(토스트 출력)
                if (GPSCheck())          // GPS서비스 확인
                    checkSelectAndInsertSQL();
                break;
            case R.id.main_imagecheck:  // 이미지체크 버튼
                if (GPSCheck())          // GPS서비스 확인
                {
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    if (photoFile != null) {
                        photoUri = FileProvider.getUriForFile(MainActivity.this, "whoim.leaveout.provider", photoFile); //FileProvider의 경우 이전 포스트를 참고하세요.
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //사진을 찍어 해당 Content uri를 photoUri에 적용시키기 위함
                        startActivityForResult(intent, 1);
                    }
                }
                break;
            case R.id.main_collect:     // 모아보기 버튼(모아보기 액티비티 이동)
                if(GPSCheck())          // GPS서비스 확인
                    contentsLocationSelectSQLData();
                break;
        }
    }

    // startActivityForResult 함수를 호출한 액티비티에서 종료했을때 콜백 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case MapAPIActivity.GPS_ENABLE_REQUEST_CODE: // GPS 서비스 요청 코드
                if(!checkLocationServicesStatus()) Toast.makeText(getApplicationContext(),"위치 서비스를 사용 할 수 없습니다.",Toast.LENGTH_SHORT).show();
                break;
            case 1: //앨범에 사진을 보여주기 위해 Scan을 합니다.
                if(resultCode ==  Activity.RESULT_OK) {
                    // 비트맵 이미지로 가져온다
                    String imagePath = photoFile.getPath();
                    Bitmap image = BitmapFactory.decodeFile(imagePath);

                    // 이미지를 상황에 맞게 회전시킨다
                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(imagePath);
                        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int exifDegree = exifOrientationToDegrees(exifOrientation);
                        rotate(image, exifDegree);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // 회전한 이미지 앨범에 띄우기
                    this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(photoFile)) );
                    imagecheckSelectAndInsertSQL(photoFile.getPath()); // db에 삽입
                }
                // 뒤로가기
                else {
                    File file = new File(photoFile.getPath());
                    // 이미지 삭제
                    if(file.delete()) { // 성공시
                        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file))); // 이미지 스캔해서 띄우기
                    }
                }
                break;
        }
    }

    // 파일 경로 생성
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(new Date());
        imageFileName = "Photo_" + timeStamp;

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(image)) );
        return image;
    }

    // 이미지 회전각도 설정
    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    // 이미지 회전
    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    //권한 획득에 동의를 하지 않았을 경우 아래 Toast 메세지를 띄우며 해당 Activity를 종료시킵니다.
    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}