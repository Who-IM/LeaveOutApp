package whoim.leaveout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import whoim.leaveout.SingleClick.OnSingleClickListener;

// 글쓰기
public class WritingActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView mAddressText;

    File storageDir = null;
    ExifInterface exif = null;
    File photoFile = null;
    int orientation;

    // spinner로 나중에 시간나면 바꿀예정
    // checkList
    LinearLayout writing_search_layout;
    private ListView writing_searchList;
    ArrayAdapter<String> writing_adapter_search;
    ImageButton writing_inputSearch;

    //카메라 앨범 변수
    private static final int PICK_FROM_CAMERA = 1; //카메라 촬영으로 사진 가져오기
    private static final int PICK_FROM_ALBUM = 2;  //앨범에서 사진 가져오기
    ImageView iv = null;
    Uri photoUri;
    Bitmap thumbImage = null;
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

    // 입력공간
    EditText write_input = null;

    // 친구태그
    private ImageButton friendtag = null;
    private String tagText = null;

    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_layout);
        // 인스턴스 셋팅
        setInstance();

        Intent data = getIntent();      // 데이터 가져오기
        mAddressText = (TextView) findViewById(R.id.write_address);
        if (data != null) mAddressText.setText(data.getStringExtra("address"));  // 주소 창 표시

        toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#00FFFFFF"));   //제목 투명하게
        setSupportActionBar(toolbar);   //액션바와 같게 만들어줌
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.

        checkPermissions(); //권한 묻기

        // 매뉴 구성
        list = (ListView) findViewById(R.id.write_listview);
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

        // tag 데이터 받기
        Intent tagintent = getIntent();
        tagText = tagintent.getStringExtra("tag");
        if(tagText != null) {
            // 여기다 db 내용 + tagText 하면됨
            write_input.setText(tagText);
        }
    }

    private void setInstance() {

        // 검색 관련 인스턴스
        writing_searchList = (ListView) findViewById(R.id.write_search_list);
        writing_inputSearch = (ImageButton) findViewById(R.id.open_list);
        writing_search_layout = (LinearLayout) findViewById(R.id.write_search_layout);
        String products[] = {"대구 수성구", "대구 동구", "대구 남구" };

        // 검색 리스트 뷰
        writing_adapter_search = new ArrayAdapter<String>(this, R.layout.main_search_item, R.id.product_name, products);

        // 입력공간 EditText
        write_input = (EditText) findViewById(R.id.write_input);
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

    public void addWriteAdapter(Bitmap th) {
        // 카메라, 겔러리 사진 업데이트
        adapter.addItem(th);
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

        // 생성자로 값을 받아 셋팅
        public void addItem(Bitmap image) {
            writing_ListData addInfo = null;
            addInfo = new writing_ListData();
            addInfo.Image = image;

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

    // Android M에서는 Uri.fromFile 함수를 사용하였으나 7.0부터는 이 함수를 사용할 시 FileUriExposedException이
    // 발생하므로 아래와 같이 함수를 작성합니다.
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());   //그림저장할때 파일명 지정
        String imageFileName = "IP" + timeStamp + "_";
        storageDir = new File(Environment.getExternalStorageDirectory() + "/LeaveOut/"); //LeaveOut라는 경로에 이미지를 저장하기 위함
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);    //확장자를 .jpg로 저장
        return image;
    }

    //카메라 불러오기 버튼
    public void takePhotoButton(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //사진을 찍기 위하여 설정
        try {
            photoFile = createImageFile();  //찍은 사진정보
        } catch (IOException e) {
            Toast.makeText(WritingActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (photoFile != null) {
            //URI에 대해 임시 액세스 권한을 부여하기 위해서 FileProvider 클래스를 사용
            photoUri = FileProvider.getUriForFile(WritingActivity.this, "whoim.leaveout.provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); //사진을 찍어 해당 Content uri를 photoUri에 적용시키기 위함
            startActivityForResult(intent, PICK_FROM_CAMERA);   //requestCode가 PICK_FROM_CAMERA으로 이동
        }
    }

    //앨범 불러오기 버튼
    public void goToAlbumButton(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK); //ACTION_PICK 즉 사진을 고르겠다!
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);     //requestCode가 PICK_FROM_ALBUM으로 이동
    }

    //카메라 및 갤러리 기능 활성화
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(count == 5)
        {
            Toast.makeText(WritingActivity.this, "이미지 갯수 5개 초과 더이상 등록할수 없습니다..", Toast.LENGTH_SHORT).show();
            return;
        }
        count++;

        //카메라나 갤러리 창을 종료 했을 경우
        if (resultCode != RESULT_OK) {
            Toast.makeText(WritingActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }

        //앨범
        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                return;
            }
            photoUri = data.getData();  //Uri 주소값을 받아온다
            try {
                imageExtraction(requestCode);  //이미지 추출
                addWriteAdapter(thumbImage);    //ImageView에 setImageBitmap을 활용하여 해당 이미지에 그림을 띄우기
                list.setAdapter(adapter);   // 리스트뷰에 어댑터 연결
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage().toString());
            }
        }
        //카메라
        else if (requestCode == PICK_FROM_CAMERA) {
            MediaScannerConnection.scanFile(WritingActivity.this, //앨범에 사진을 보여주기 위해 Scan을 합니다.
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {}
                    });
            try {
                imageExtraction(requestCode);  //이미지 추출
                addWriteAdapter(thumbImage);    //ImageView에 setImageBitmap을 활용하여 해당 이미지에 그림을 띄우기
                list.setAdapter(adapter);   // 리스트뷰에 어댑터 연결

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage().toString());
            }
        }
    }

    //이미지 추출
    protected void imageExtraction(int requestCode) throws IOException {

        //bitmap 형태의 이미지로 가져오기 위해 Thumbnail을 추출.
        iv = (ImageView) findViewById(R.id.write_input_picture);
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
        thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 1024, 768);  //사진 크기를 조절
        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        if (requestCode == PICK_FROM_CAMERA) {
            // 파일 경로 저장
            try {
                exif = new ExifInterface(photoFile.getPath());
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*else if (requestCode == PICK_FROM_ALBUM){
            try {
                exif = new ExifInterface(storageDir.toURI().getPath());
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/ 

        // 이미지 돌리기
        thumbImage = rotateBitmap(thumbImage, orientation);
        thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs); //이미지가 클 경우 OutOfMemoryException 발생이 예상되어 압축
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

    // 이미지 돌리기(삼성폰경우 90도 회전되기때문에)
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    // 뒤로가기
    public void writeBack(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    // 친구태그
    public void tag(View v) {
        Intent it = new Intent(getApplicationContext(), TagFriendListActivity.class);
        startActivity(it);
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
