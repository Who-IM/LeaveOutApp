package whoim.leaveout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

import whoim.leaveout.MapAPI.LocationBackground;
import whoim.leaveout.MapAPI.MapAPIActivity;
import whoim.leaveout.StartSetting.Permission;

public class MainActivity extends MapAPIActivity {

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

    DataAdapter adapter; // 데이터를 연결할 Adapter
    ArrayList<MenuData> alist; // 데이터를 담을 자료구조


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreState(savedInstanceState);     // 상태 불러오기
        // 화면 캡쳐 방지?
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main_layout);

        mAddressView = (TextView) findViewById(R.id.main_location);

        // 툴바 설정
        setToolbar();

        // 메뉴 커스텀
        setMenuCustom();

        super.buildGoogleApiClient();         // GooglePlayServicesClient 객체를 생성
        super.mGoogleApiClient.connect();     // connect 메소드가 성공하면 onConnect() 콜백 메소드를 호출
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
        // 매뉴 구성
        list = (ListView)findViewById(R.id.main_menu);
        Drawer = (DrawerLayout)findViewById(R.id.main_drawer);

        // 툴바 메뉴 버튼
        menu_btn = (ImageButton)findViewById(R.id.menu_btn);

        // 매뉴 툴바 메뉴 버튼 누를 시 이벤트 처리
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            // 메뉴화면 펼치기(layout 앞으로 이동)
            public void onClick(View v) {
                Drawer.openDrawer(GravityCompat.START); // 펼치기
            }
        });

        // ArrayList객체를 생성합니다
        alist = new ArrayList<MenuData>();
        // 데이터를 받기위해 데이터어댑터 객체 선언
        adapter = new DataAdapter(this, alist);
        // 리스트뷰에 어댑터 연결
        list.setAdapter(adapter);

        // 메뉴에 글 목록 등록
        list.setOnItemClickListener(new DrawerItemClickListener());

        // 자기 프로필(사진, 이름, email)
        adapter.add(new MenuData(getApplicationContext(), R.drawable.basepicture, "허 성 문", "gjtjdans123@naver.com"));  // 안의 데이터는 db받아서
        adapter.add(new MenuData(getApplicationContext(), R.drawable.profile_icon, "프로필")); // 프로필아이콘 & 프로필(text)
        adapter.add(new MenuData(getApplicationContext(), R.drawable.friends_icon, "친구목록")); // 친구아이콘 & 친구(text)
        adapter.add(new MenuData(getApplicationContext(), R.drawable.preferences_icon, "환경설정")); // 환경설정아이콘 & 환경설정(text)
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
                case 0:

                    break;
                case 1:
                    button = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(button);
                    break;
                case 2: // 친구목록으로 이동
                    button = new Intent(getApplicationContext(), Friend_listActivity.class);
                    startActivity(button);
                    break;
                case 3: // 환경설정으로 이동
                    button = new Intent(getApplicationContext(), PreferenceActivity.class);
                    startActivity(button);
                    break;
            }
            Drawer.closeDrawer(list);
        }
    }

    // 메뉴 커스텀
    private class DataAdapter extends ArrayAdapter<MenuData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public DataAdapter(Context context, ArrayList<MenuData> object) {
            // 상위 클래스의 초기화 과정
            // context, 0, 자료구조
            super(context, 0, object);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        // 보여지는 스타일을 자신이 만든 xml로 보이기 위한 구문
        @Override
        public View getView(int position, View v, ViewGroup parent) {
            View view = null;
            // 현재 리스트의 하나의 항목에 보일 컨트롤 얻기

            // view 구성하기 (0 : 자기 프로필 화면, 1 : 프로필 아이콘 & text, 2 : 친구아이콘 & text)
            if (v == null && position == 0) {
                view = mInflater.inflate(R.layout.menu_profile_title, null);
            } else  {
                view = mInflater.inflate(R.layout.menu_item, null);
            }

            // 자료를 받는다.
            final MenuData data = this.getItem(position);

            // 자기 프로필
            if (data != null && position == 0) {
                // 자기 사진
                ImageView iv = (ImageView) view.findViewById(R.id.menu_home_icon);
                iv.setImageResource(data.getImage());

                // 이름. email
                TextView tv = (TextView) view.findViewById(R.id.menu_profile_myname);
                TextView tv2 = (TextView) view.findViewById(R.id.menu_profile_myemail);
                tv.setText(data.getLabel());
                tv2.setText(data.getLabel2());
            }
            else {
                ImageView iv = (ImageView) view.findViewById(R.id.menu_icon);
                iv.setImageResource(data.getImage());

                TextView tv = (TextView) view.findViewById(R.id.menu_text);
                tv.setText(data.getLabel());
            }
            return view;
        }
    }       // write_DataAdapter class -- END --

    // menuData안에 받은 값을 직접 할당
    private class MenuData {
        private String label1; // text 처리
        private String label2; // text 처리2
        private int menu_image; // 이미지 처리

        public MenuData(Context context, int image, String label1) {
            menu_image = image;
            this.label1 = label1;
        }

        public MenuData(Context context, int image, String label1, String label2) {
            menu_image = image;
            this.label1 = label1;
            this.label2 = label2;
        }

        public MenuData(Context context, String label) { label1 = label; }
        public String getLabel() { return label1; }
        public String getLabel2() { return label2; }
        public int getImage() { return menu_image; }
    }    // bitMapData class -- END --

    // 글쓰기, 체크, 모아보기 메뉴 onClick 메소드
    public void nextActivityButton(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.main_write:       // 글쓰기 버튼(글쓰기 액티비티 이동)
                intent = new Intent(getApplicationContext(), WritingActivity.class);
                intent.putExtra("address",mAddressView.getText().toString());
                startActivity(intent);
                break;
            case R.id.main_check:       // 체크 버튼(토스트 출력)
                Toast toastView = Toast.makeText(getApplicationContext(), "체크되었습니다.", Toast.LENGTH_LONG);
//                toastView.setGravity(Gravity.TOP, 0, 1300);
                toastView.show();
                break;
            case R.id.main_collect:     // 모아보기 버튼(모아보기 액티비티 이동)
                intent = new Intent(getApplicationContext(), CollectActivity.class);
                startActivity(intent);
                break;
        }
    }

    // 뒤로가기
    @Override
    public void onBackPressed() {
        // 메뉴 화면이 open 되있을 경우
        if (Drawer.isDrawerOpen(GravityCompat.START)) {
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
        super.onResume();
        this.registerReceiver();        // 사용자 위치 브로드 캐스트 수신 설정
/*        if(super.mGoogleApiClient.isConnected()) {
            getDeviceLocation();
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
/*        // 백그라운드 브로드 캐스트 죽일때 사용
        if(mBroadcastCheck) {
            unregisterReceiver(mBroadcastLocation);
            mBroadcastCheck = false;
        }*/

/*        // 다른 액티비티로 넘어가거나 액티비티 종료시 버리기
        if(super.mGoogleApiClient.isConnected()) {
//            PendingIntent LocationIntent = PendingIntent.getService(this, 0, new Intent(this, LocationBackground.class), PendingIntent.FLAG_UPDATE_CURRENT);   // 다른 컴포넌트에게 인텐트 권한 주기
            LocationServices.FusedLocationApi.removeLocationUpdates(super.mGoogleApiClient, super.mLocationPendingIntent);
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this,"종료",Toast.LENGTH_SHORT).show();
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
                }
                else Toast.makeText(this, "GPS 승인 거부", Toast.LENGTH_SHORT).show();

                super.updateLocationUI();       // 구글맵 자신 GPS UI 셋팅
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // startActivityForResult 함수를 호출한 액티비티에서 종료했을때 콜백 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case MapAPIActivity.GPS_ENABLE_REQUEST_CODE: // GPS 서비스 요청 코드
                if(!checkLocationServicesStatus()) Toast.makeText(getApplicationContext(),"위치 서비스를 사용 할 수 없습니다.",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // 브로드 캐스트 만들기(로케이션)
    private void registerReceiver() {

        // 이미 브로드캐스트 수신 설정 했으면 안하기
        if(mBroadcastCheck == true) return;

        if(mBroadcastLocation == null) {
            // 사용자 위치 정보 브로드캐스트 수신 객체
            mBroadcastLocation = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    MainActivity.super.mCurrentLocation = intent.getExtras().getParcelable(LocationBackground.EXTRA_CURRENT_LOCATION);
                    mAddressView.setText(MainActivity.super.getCurrentAddress(mCurrentLocation));
                    Log.d(LocationBackground.ACTION_LOCATION_BROADCAST, "mLatitudelocation" + mCurrentLocation.getLatitude());
                    Log.d(LocationBackground.ACTION_LOCATION_BROADCAST, "mLongitudelocation" + mCurrentLocation.getLongitude());
                }
            };
        }
        // 사용자 위치 정보 브로드캐스트 수신 설정
        if(mBroadcastLocation != null) {
            registerReceiver(mBroadcastLocation, new IntentFilter(LocationBackground.ACTION_LOCATION_BROADCAST));
            mBroadcastCheck = true; // 브로드캐스트 설정 완료
        }
    }

    //글보기 임시버튼(테스트)
    public void view_button(View v)
    {
        Intent intent = new Intent(getApplicationContext(), View_articleActivity.class);
        startActivity(intent);
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }



}
