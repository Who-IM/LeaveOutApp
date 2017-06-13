package whoim.leaveout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import whoim.leaveout.Adapter.ContentAdapter;
import whoim.leaveout.Loading.LoadingSQLDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.Server.ImageDownLoad2;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.Services.FomatService;
import whoim.leaveout.User.UserInfo;

// 환경설정
public class ProfileActivity extends AppCompatActivity {

    // 프로필 이미지 변경
    private ImageButton mProfileSetImage;

    // list
    private ListView list = null;

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

    ContentAdapter mContentAdapter;

    //like 버튼
    UserInfo userInfo = UserInfo.getInstance();     // 유저 정보
    Bitmap bitmap = userInfo.getProfile();

    HashMap<Integer,ArrayList<Bitmap>> mImageMap = new HashMap<>();     // 이미지 해시맵

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

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
        tab = new profile_tab("전체");
        tab = new profile_tab("맛집");
        tab = new profile_tab("여행지");
        tab = new profile_tab("서울");
        tab = new profile_tab("대구");
        tab = new profile_tab("전체");
        tab = new profile_tab("맛집");
        tab = new profile_tab("여행지");
        tab = new profile_tab("서울");
        tab = new profile_tab("대구");
        tab = new profile_tab("전체");
        tab = new profile_tab("맛집");
        tab = new profile_tab("여행지");
        tab = new profile_tab("서울");
        tab = new profile_tab("대구");

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // ViewPager 설정
        viewPager = (ViewPager) findViewById(R.id.profile_pager);

        //tab layout 리스너 등록
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        meContentData();
    }

    //옵션 버튼
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
    }
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
        list = (ListView) findViewById(R.id.proflie_list);
        list.addHeaderView(header);

        mContentAdapter = new ContentAdapter();
        list.setAdapter(mContentAdapter);


        // 여기서 db데이터 넣기
/*        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"허성문", "대구 수성구 범어동", "2017.05.08 19:12","250","511","놀러와라");
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"김창석", "대구 수성구 만촌역", "2017.05.21 20:00","500","1000","ddd");*/
    }

    // 댓글 listview 셋팅
    private void setComment(int position, int image, String name, String comment) {
        // 실제 데이터 삽입
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM월 dd일 HH:mm:ss");
        String time = sdfNow.format(new Date(System.currentTimeMillis()));

    }

    // 리스트뷰 펼처보기(한화면에)
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    // 리스트뷰 펼처보기(한화면에)
    public static void setListViewHeightBasedOnChildren(GridView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        int count = listAdapter.getCount();
        if(count > 2) {
            count = count/2 + 1;
        }
        else {
            count = 1;
        }
        for (int i = 0; i < count; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
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

    private void meContentData() {
        final String sql ="select content_num, name, view_cnt, rec_cnt, reg_time,address,files " +
                "from content inner join user " +
                "on content.user_num = user.user_num " +
                "where user.user_num = " + userInfo.getUserNum();

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                JSONObject data = SQLDataService.getSQLJSONData(sql,-1,"select");
                SQLDataService.putBundleValue(data,"download","context","files");
                return data;
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                if(responseData.get(0) != null) {
                    JSONArray resultData = responseData.get(0).getJSONArray("result");
                    for (int i = 0; i < resultData.length(); i++) {
                        JSONObject data = resultData.getJSONObject(i);
                        int contentnum = data.getInt("content_num");
                        String name = data.getString("name");
                        String address = data.getString("address");
                        String reg_time = data.getString("reg_time");
                        String rec_cnt = data.getString("rec_cnt");
                        String view_cnt = data.getString("view_cnt");
                        String text = data.getString("text");

                        ArrayList<String> imagelist = new ArrayList();
                        JSONArray imageArray = resultData.getJSONObject(i).getJSONArray("image");
                        for(int j = 0; j < imageArray.length(); j++) {
                            imagelist.add(imageArray.getString(j));
                        }
                        mContentAdapter.addItem(bitmap,contentnum,name, address, reg_time,rec_cnt,view_cnt,text, imagelist);
//                        contetntImageDownLoad(resultData.getJSONObject(i).getJSONArray("image"), i);
                    }
                }
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener,ProgressDialog.STYLE_SPINNER,null);
    }
/*    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            for(GridAdapter grid : gridAdapter.values()) {
                if(grid != null) grid.notifyDataSetChanged();
            }
            adapter.notifyDataSetChanged();
        }
    };*/

    private void contetntImageDownLoad(final JSONArray imagedata, final int num) {

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    ArrayList<Bitmap> bitmaps = new ArrayList();
                    for(int i = 0; i < imagedata.length(); i++) {
                        bitmaps.add(ImageDownLoad2.imageDownLoad(imagedata.getString(i)));
                    }
                        mImageMap.put(num,bitmaps);
//                        handler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}