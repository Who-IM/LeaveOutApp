package whoim.leaveout;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;
import nl.changer.polypicker.model.Image;
import nl.changer.polypicker.utils.ImageInternalFetcher;
import whoim.leaveout.Loading.LoadingSQLDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.Services.FomatService;
import whoim.leaveout.SingleClick.OnSingleClickListener;
import whoim.leaveout.User.UserInfo;

import static whoim.leaveout.Services.FomatService.getCurrentAddress;

// 글쓰기
public class WritingActivity extends AppCompatActivity {

    // 게시글 등록 SQL
    private String mContentUpdateSQL = "insert into content(user_num,reg_time,visibility,fence,loc_x,loc_y,address) " +
            "values(?,now(),?,?,?,?,?)";
    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance();
    private int chk_n;

    private Toolbar toolbar;
    private TextView mAddressText;          // 주소 이름
    private Location mCurrentLocation;      // GPS 주소 객체

    // spinner로 나중에 시간나면 바꿀예정
    // checkList
    LinearLayout writing_search_layout;
    private ListView writing_searchList;
    ArrayAdapter<String> writing_adapter_search;
    ImageButton writing_inputSearch;

    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}; //권한 설정 변수

    private static final int MULTIPLE_PERMISSIONS = 101; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수
    //카메라 앨범 끝

    //글 공개 여부 변수
    boolean ispageOpen = false;     //글 공개여부 레이어 표시 여부
    Animation translateLeftAnim;    //왼쪽으로 이동 애니메이션 객체
    Animation translateRightAnim;   //오른쪽으로 이동 애니메이션 객체
    RelativeLayout page;    //슬라이딩 애니메이션으로 보여줄 레이아웃
    ImageButton whether_button;
    //글 공개 여부 변수 끝

    // 메뉴 관련 인스턴스
    private ListView list;
    write_DataAdapter adapter; // 데이터를 연결할 Adapter
    private int imagecount;

    // 입력공간
    private EditText write_input;

    // 공개여부
    private RadioGroup mSecurityRadioGroup;     // 공개여부 그룹
    private CheckBox mSecretCheckBox;         // 울타리글 체크박스

    // 친구태그
    private ImageButton friendtag = null;
    private String tagText = null;

    ArrayList<String> product = null;
    ArrayList<Double> x = null;
    ArrayList<Double> y = null;

    // 카메라 관련
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int INTENT_REQUEST_GET_N_IMAGES = 14;

    private Context mContext;

    HashSet<Uri> mMedia = new HashSet<Uri>();
    HashSet<Image> mMediaImages = new HashSet<Image>();

    // 사진 위치정보
    ArrayList<ExifInterface> image_exif;
    ArrayAdapter location_adapter;
    Location first_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_layout);
        // 인스턴스 셋팅
        setInstance();

        Intent data = getIntent();      // 데이터 가져오기
        mAddressText = (TextView) findViewById(R.id.write_address);
        if (data != null) {
            mAddressText.setText(data.getStringExtra("address"));  // 주소 창 표시
            mCurrentLocation = data.getParcelableExtra("loc");     // GPS 주소 객체
            first_location = new Location("first");
            first_location.setLatitude(mCurrentLocation.getLatitude());
            first_location.setLongitude(mCurrentLocation.getLongitude());
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#00FFFFFF"));   //제목 투명하게
        setSupportActionBar(toolbar);   //액션바와 같게 만들어줌
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.

        checkPermissions(); //권한 묻기

        // 매뉴 구성
        list = (ListView) findViewById(R.id.Image_listview);
        adapter = new write_DataAdapter(WritingActivity.this);  // 데이터를 받기위해 데이터어댑터 객체 선언

        page = (RelativeLayout) findViewById(R.id.write_whether_layout);
        whether_button = (ImageButton) findViewById(R.id.whether_open);

        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        //슬라이딩 에니메이션 감지
        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);

        whether_open_button();   // 공개여부 버튼 작동

        writing_search_layout.setVisibility(View.GONE);
        check_list_open();

        Intent tagintent = getIntent();
        tagText = tagintent.getStringExtra("tag");
        if(tagText != null) {
            // 여기다 db 내용 + tagText 하면됨
            write_input.setText(tagText);
        }

        mContext = WritingActivity.this;
        image_exif = new ArrayList<>();

        // 이미지 롱클릭시 삭제
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 위치정보 있는지 확인하고 이름 받아오기
                GeoDegree geoDegree = new GeoDegree();
                String lo_name = geoDegree.getLocationName(adapter.getItem(position).exif);

                // 이미지 삭제
                adapter.getList().remove(position);
                adapter.notifyDataSetChanged();
                list.setAdapter(adapter);

                // 위치정보 이름 : 현재 위치정보 ---> 처음 위치정보로 셋팅
                if(lo_name.equals(mAddressText.getText().toString())) {
                    mAddressText.setText(getCurrentAddress(getApplicationContext(),first_location));
                    mCurrentLocation.setLatitude(first_location.getLatitude());
                    mCurrentLocation.setLongitude(first_location.getLongitude());
                }

                return false;
            }
        });
    }

    // 인스턴스 셋팅
    private void setInstance() {
        // 검색 관련 인스턴스
        writing_searchList = (ListView) findViewById(R.id.write_search_list);
        writing_inputSearch = (ImageButton) findViewById(R.id.open_list);
        writing_search_layout = (LinearLayout) findViewById(R.id.write_search_layout);

        product = new ArrayList<>();
        x = new ArrayList<>();
        y = new ArrayList<>();
        checkInsertSQLData();

        // 검색 리스트 뷰
        writing_adapter_search = new ArrayAdapter<String>(this, R.layout.main_search_item, R.id.product_name, product);

        writing_searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAddressText.setText(writing_adapter_search.getItem(position));
                mCurrentLocation.setLatitude(x.get(position));  // 위도
                mCurrentLocation.setLongitude(y.get(position));
            }
        });

        // 입력공간 EditText
        write_input = (EditText) findViewById(R.id.write_input);

        // 공개여부 및 울타리글
        mSecurityRadioGroup = (RadioGroup) findViewById(R.id.write_radioButton);
        mSecretCheckBox = (CheckBox) findViewById(R.id.check_icon);
    }

    // 검색관련 셋팅
    public void check_list_open() {

        writing_inputSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(writing_search_layout.getVisibility() == View.GONE) {
                    writing_search_layout.setVisibility(View.VISIBLE);
                    writing_searchList.setAdapter(writing_adapter_search);
                } else {
                    writing_search_layout.setVisibility(View.GONE);
                }
            }
        });
    }

    private class Writing_Holder {
        public ImageView Image;
    }

    // 리스트뷰 어뎁터
    private class write_DataAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<writing_ListData> mListData = new ArrayList<writing_ListData>();

        public write_DataAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        public ArrayList getList() { return mListData; };

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public writing_ListData getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        // 생성자로 값을 받아 셋팅
        public void addItem(Bitmap image, int width, int height, ExifInterface exif) {
            writing_ListData addInfo = null;
            addInfo = new writing_ListData();
            addInfo.Image = image;
            addInfo.width = width;
            addInfo.height = height;
            addInfo.exif = exif;

            mListData.add(addInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Writing_Holder holder;

            if (convertView == null) {
                holder = new Writing_Holder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.write, null);

                holder.Image = (ImageView) convertView.findViewById(R.id.write_input_picture);

                convertView.setTag(holder);
            }else{
                holder = (Writing_Holder) convertView.getTag();
            }

            final writing_ListData mData = mListData.get(position);

            // 이미지 처리
            if (mData.Image != null) {
                holder.Image.setVisibility(View.VISIBLE);
                holder.Image.getLayoutParams().height = mData.height;
                holder.Image.getLayoutParams().width = mData.width;
                holder.Image.setImageBitmap(mData.Image);
            }else{
                holder.Image.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

    // 메뉴의 실제 데이터를 저장할 class
    class writing_ListData {
        public Bitmap Image;
        public int width;
        public int height;
        public ExifInterface exif;
    }

    // 카메라 사진에 받은 값을 직접 할당(bitmap)
    private class bitMapData {
        private Bitmap bitmapimg; // 이미지 처리

        public bitMapData(Bitmap image) {
            bitmapimg = image;
        } // 생성자
        public Bitmap getImage() {
            return bitmapimg;
        }        // getter
    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) { //사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) { //권한이 추가되었으면 해당 리스트가 empty가 아니므로 request 즉 권한을 요청합니다.
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    //아래는 권한 요청 Callback 함수입니다. PERMISSION_GRANTED로 권한을 획득했는지 확인할 수 있습니다. 아래에서는 !=를 사용했기에
    //권한 사용에 동의를 안했을 경우를 if문으로 코딩되었습니다.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    //권한 획득에 동의를 하지 않았을 경우 아래 Toast 메세지를 띄우며 해당 Activity를 종료시킵니다.
    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    //앨범 불러오기 버튼
    public void goToAlbumButton(View v) {
        getImages();

    }

    //공개여부
    public void whether_open_button()
    {
        whether_button.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v)
            {
                if(ispageOpen)
                {
                    page.startAnimation(translateRightAnim);    //페이지가 열려있으면 오른쪽으로 애니메이션 주기
                }
                else
                {
                    page.setVisibility(View.VISIBLE);
                    page.startAnimation(translateLeftAnim);     //페이지가 닫겨있으면 왼쪽으로 애니메이션 주기
                }
            }
        });
    }

    //슬라이딩 에니메이션
    private class SlidingPageAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationEnd(Animation animation)
        {
            if(ispageOpen) {
                page.setVisibility(View.INVISIBLE);     //페이지가 열려있으면 안보이도록 하기
                ispageOpen = false;
            }
            else {
                ispageOpen = true;      //페이지가 닫겨있으면 보이도록 하기
            }
        }

        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationRepeat(Animation animation) {}
    }

    @Override
    protected void onDestroy() {
        // bitmaap 삭제
        ArrayList<writing_ListData> arrayList = adapter.getList();
        for(writing_ListData writing_listData : arrayList) {
            writing_listData.Image.recycle();
            writing_listData.Image = null;
        }
        super.onDestroy();
    }

    // 완료 및 뒤로가기 버튼
    public void onWriteClicked(View view) {
        switch (view.getId()) {
            case R.id.preferences_back_icon:        // 뒤로가기 버튼
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.write_commit:                // 게시 버튼
                contentUpdateSQLData();            // 게시글 업데이트
                break;
        }
    }

    // 게시하기
    private void contentUpdateSQLData() {

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return adapter.getCount() + 1;
            }
            @Override
            public JSONObject getSQLQuery() {
                return InsertSQLContent();
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return uploadImage();
            }
            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                if(responseData != null) {
                    Toast.makeText(WritingActivity.this,"등록 되었습니다.",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener,ProgressDialog.STYLE_HORIZONTAL,null);
    }

    private JSONObject InsertSQLContent() {
        final UserInfo userInfo = UserInfo.getInstance();
        mDataQueryGroup.clear();
        mDataQueryGroup.addInt(userInfo.getUserNum());          // 유저 번호
        mDataQueryGroup.addInt(SecurityRadioChecked());         // 공개여부 값
        mDataQueryGroup.addBoolean(mSecretCheckBox.isChecked());    // 울타리 체크
        mDataQueryGroup.addDouble(mCurrentLocation.getLatitude());  // 위도
        mDataQueryGroup.addDouble(mCurrentLocation.getLongitude()); // 경도
        mDataQueryGroup.addString(mAddressText.getText().toString());   // 주소
        JSONObject data = SQLDataService.getDynamicSQLJSONData(mContentUpdateSQL,mDataQueryGroup,0,"update");       // sql 셋팅
        SQLDataService.putBundleValue(data, "upload", "usernum", userInfo.getUserNum());                 // 번들 데이터 더 추가(유저 id)
        SQLDataService.putBundleValue(data, "upload", "path", "content");
        SQLDataService.putBundleValue(data,"upload","context","text");
        SQLDataService.putBundleValue(data, "upload", "text", write_input.getText().toString());        // 번들 데이터 더 추가(내용)
        return data;
    }

    private JSONObject uploadImage() {
        final UserInfo userInfo = UserInfo.getInstance();
        JSONObject data = new JSONObject();
        SQLDataService.putBundleValue(data, "upload", "usernum", userInfo.getUserNum());                 // 번들 데이터 더 추가(유저 id)
        SQLDataService.putBundleValue(data, "upload", "path", "content");
        SQLDataService.putBundleValue(data,"upload","context","image");
        JSONArray jsonArray = new JSONArray();
        ArrayList<writing_ListData> arrayList = adapter.getList();
        if(arrayList.size() == 0) return null;
        jsonArray.put(FomatService.getStringFromBitmap(arrayList.get(imagecount).Image));
        this.imagecount++;
        SQLDataService.putBundleValue(data, "upload", "imagecount", this.imagecount);
        SQLDataService.putBundleValue(data, "upload", "array", jsonArray);
        return data;
    }

    // 공개여부 라디오 버튼 체크 된 부분 찾기
    private int SecurityRadioChecked() {
        int id = mSecurityRadioGroup.getCheckedRadioButtonId();     // 라디오버튼에 체크된 값 id
        RadioButton rb = (RadioButton) findViewById(id);            // 가져오기
        switch (rb.getId()) {       // 공개 여부
            case R.id.write_public_button:      // 전체
                return 1;
            case R.id.write_friend_button:      // 친구
                return 2;
            case R.id.write_private_button:     // 비공개
                return 3;
        }
        return 0;
    }

    // 친구태그
    public void tag(View v) {
        Intent it = new Intent(getApplicationContext(), TagFriendListActivity.class);
        startActivity(it);
    }

    // 체크항목넣기 db
    private void checkInsertSQLData() {

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
                Location location = new Location("checks");
                for(int i =0; i < jspn.length(); i++) {
                    JSONObject j = jspn.getJSONObject(i);
                    chk_n = j.getInt("check_num");
                    x.add(j.getDouble("chk_x"));
                    y.add(j.getDouble("chk_y"));
                    location.setLatitude(j.getDouble("chk_x"));
                    location.setLongitude(j.getDouble("chk_y"));
                    product.add(getCurrentAddress(getApplicationContext(),location));
                }
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener, ProgressDialog.STYLE_SPINNER,null);
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    //이미지 갯수 제한
    private void getImages() {
        Intent intent = new Intent(mContext, ImagePickerActivity.class);
        Config config = new Config.Builder()
                .setTabBackgroundColor(R.color.white)    // set tab background color. Default white.
                .setTabSelectionIndicatorColor(R.color.blue)
                .setCameraButtonColor(R.color.orange)
                .setSelectionLimit(10)    // set photo selection limit. Default unlimited selection.
                .build();
        ImagePickerActivity.setConfig(config);
        startActivityForResult(intent, INTENT_REQUEST_GET_N_IMAGES);
    }

    // 카메라, 엘범 결과
    @Override
    protected void onActivityResult(int requestCode, int resuleCode, Intent intent) {
        super.onActivityResult(requestCode, resuleCode, intent);

        if (resuleCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES || requestCode == INTENT_REQUEST_GET_N_IMAGES) {
                Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
                int[] parcelableOrientations = intent.getIntArrayExtra((ImagePickerActivity.EXTRA_IMAGE_ORIENTATIONS));
                if (parcelableUris == null) {
                    return;
                }

                Uri[] uris = new Uri[parcelableUris.length];
                int[] orientations = new int[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);
                System.arraycopy(parcelableOrientations, 0, orientations, 0, parcelableOrientations.length);

                if (uris != null) {
                    for (int i=0; i<orientations.length; i++) {
                        mMediaImages.add(new Image(uris[i], orientations[i]));
                    }

                    showMedia();
                }
            }
        }
    }

    // 이미지 보여주기
    private void showMedia() {

        Iterator<Image> iterator = mMediaImages.iterator();
        ImageInternalFetcher imageFetcher = new ImageInternalFetcher(this, 500);
        while (iterator.hasNext()) {
            Image image = iterator.next();
            Bitmap bitmap = null;

            View imageHolder = LayoutInflater.from(this).inflate(R.layout.write, null);
            ImageView thumbnail = (ImageView) imageHolder.findViewById(R.id.write_input_picture);

            // 이미지 크기 조절
            if (!image.mUri.toString().contains("content://")) {
                // probably a relative uri
                image.mUri = Uri.fromFile(new File(image.mUri.toString()));

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image.mUri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    if (bitmap.getWidth() >= 5000 || bitmap.getHeight() >= 2600) {
                        options.inSampleSize = 4;
                    } else if ((bitmap.getWidth() < 5000 && bitmap.getWidth() >= 3750) ||
                            (bitmap.getHeight() < 2600 && bitmap.getHeight() >= 1950)) {
                        options.inSampleSize = 3;
                    } else if ((bitmap.getWidth() < 3750 && bitmap.getWidth() >= 2500) ||
                            (bitmap.getHeight() < 1950 && bitmap.getHeight() >= 1300)) {
                        options.inSampleSize = 2;
                    }
                    bitmap.recycle();
                    bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(image.mUri),null,options);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            imageFetcher.loadImage(image.mUri, thumbnail, image.mOrientation);

            // set the dimension to correctly
            // show the image thumbnail.
            int wdpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
            int htpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());

            try {
                ExifInterface exif = new ExifInterface(image.mUri.getPath());
                adapter.addItem(bitmap, wdpx, htpx, exif);
                image_exif.add(exif);
            } catch (IOException e) {
                e.printStackTrace();
            }

            list.setAdapter(adapter);
        }

        // 위치정보 셋팅
        setLocation_Image();
        image_exif = null;
        image_exif = new ArrayList<>();
    }

    // image에있는 location정보 불러와 셋팅
    public void setLocation_Image() {
        final GeoDegree geoDegree = new GeoDegree(image_exif);

        // gps정보가 있으면
        if (geoDegree.isValid()) {  // gps가 사진에 있는지
            AlertDialog.Builder dialog = new AlertDialog.Builder(WritingActivity.this);
            dialog.setTitle("사진 위치정보가 있습니다." +"\n"+ "사진위치에 글을 등록하시겠습니까?");

            // 위치정보 list에 셋팅
            Location lo = new Location("image");
            final String temp[] = new String[geoDegree.getSize()];
            for(int i = 0; i < geoDegree.getSize(); i++) {
                lo.setLatitude(geoDegree.getLatitude(i));
                lo.setLongitude(geoDegree.getLongitude(i));
                temp[i] = getCurrentAddress(getApplicationContext(),lo);
            }

            // dialog에 위치정보 list추가 및 list item 클릭시 이벤트 처리
            location_adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, temp);
            dialog.setAdapter(location_adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAddressText.setText(temp[which]); // 위치정보 textview 셋팅
                    mCurrentLocation.setLatitude(geoDegree.getLatitude(which)); // 위치정보 수정
                    mCurrentLocation.setLongitude(geoDegree.getLongitude(which)); // 위치정보 수정
                }
            });

            // dialog 보여주기
            dialog.show();
        }

    }

    // 사진 위치정보 저장할 클레스
    public class location_class {
        Float latitude;
        Float longitude;
    }

    // 이미지 gps 경로
    public class GeoDegree {
        private boolean valid = false;
        private ArrayList<location_class> location;

        public GeoDegree() {}

        public GeoDegree(ArrayList list_exif) {
            location = new ArrayList<>();

            for(int i = 0; i < list_exif.size(); i++) {
                ExifInterface exif = (ExifInterface) list_exif.get(i);
                setLocation(exif);
            }
        };

        // 이미지 위치정보 셋팅
        public void setLocation(ExifInterface exif) {
            String attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String attrLATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String attrLONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

            if ((attrLATITUDE != null) && (attrLATITUDE_REF != null) && (attrLONGITUDE != null) && (attrLONGITUDE_REF != null)) {
                location_class lo_class = new location_class();
                valid = true;

                if (attrLATITUDE_REF.equals("N")) {
                    lo_class.latitude = convertToDegree(attrLATITUDE);
                } else {
                    lo_class.latitude = 0 - convertToDegree(attrLATITUDE);
                }

                if (attrLONGITUDE_REF.equals("E")) {
                    lo_class.longitude = convertToDegree(attrLONGITUDE);
                } else {
                    lo_class.longitude = 0 - convertToDegree(attrLONGITUDE);
                }
                location.add(lo_class);
            }
        }

        // 이미지 위치정보 이름반환
        public String getLocationName(ExifInterface exif) {
            String attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String attrLATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String attrLONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

            if ((attrLATITUDE != null) && (attrLATITUDE_REF != null) && (attrLONGITUDE != null) && (attrLONGITUDE_REF != null)) {
                valid = true;
                Location lo = new Location("image_equal");

                if (attrLATITUDE_REF.equals("N")) {
                    lo.setLatitude(convertToDegree(attrLATITUDE));
                } else {
                    lo.setLatitude(0 - convertToDegree(attrLATITUDE));
                }

                if (attrLONGITUDE_REF.equals("E")) {
                    lo.setLongitude(convertToDegree(attrLONGITUDE));
                } else {
                    lo.setLongitude(0 - convertToDegree(attrLONGITUDE));
                }

                return getCurrentAddress(getApplicationContext(),lo);
            }

            return "";
        }

        private Float convertToDegree(String stringDMS) {
            Float result = null;
            String[] DMS = stringDMS.split(",", 3);

            String[] stringD = DMS[0].split("/", 2);
            Double D0 = new Double(stringD[0]);
            Double D1 = new Double(stringD[1]);
            Double FloatD = D0 / D1;

            String[] stringM = DMS[1].split("/", 2);
            Double M0 = new Double(stringM[0]);
            Double M1 = new Double(stringM[1]);
            Double FloatM = M0 / M1;

            String[] stringS = DMS[2].split("/", 2);
            Double S0 = new Double(stringS[0]);
            Double S1 = new Double(stringS[1]);
            Double FloatS = S0 / S1;
            result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));
            return result;
        };

        public boolean isValid() {
            return valid;
        }

        public String toString(int i) {
            location_class lo_class = location.get(i);
            return lo_class.latitude + ", " + lo_class.longitude;
        }

        public Float getLatitude(int i) {
            location_class lo_class = location.get(i);
            return lo_class.latitude;
        }

        public Float getLongitude(int i) {
            location_class lo_class = location.get(i);
            return lo_class.longitude;
        }

        public int getSize() {
            return location.size();
        }
    }
}