package whoim.leaveout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// 글쓰기
public class WritingActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView mAddressText;
    ImageView iv = null;
    private static final int PICK_FROM_CAMERA = 1; //카메라 촬영으로 사진 가져오기
    private static final int PICK_FROM_ALBUM = 2; //앨범에서 사진 가져오기
    Uri photoUri;
    Bitmap thumbImage = null;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}; //권한 설정 변수

    private static final int MULTIPLE_PERMISSIONS = 101; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수

    // 메뉴 관련 인스턴스
    private ListView list;

    DataAdapter adapter; // 데이터를 연결할 Adapter
    ArrayList<MenuData> alist; // 데이터를 담을 자료구조

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_layout);
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

        // ArrayList객체를 생성합니다
        alist = new ArrayList<MenuData>();
    }

    public void setWriteAdapter(Bitmap th) {
        // 자기 프로필(사진, 이름, email)
        adapter.add(new MenuData(th));
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
            if (v == null) {
                view = mInflater.inflate(R.layout.write, null);
            } else {
                view = v;
            }

            // 자료를 받는다.
            final MenuData data = this.getItem(position);

            // 자기 프로필
            if (data != null) {
                // 자기 사진
                iv = (ImageView) view.findViewById(R.id.write_input_picture);
                iv.setImageBitmap(data.getImage());
            }
            return view;
        }
    }       // DataAdapter class -- END --

    // menuData안에 받은 값을 직접 할당
    private class MenuData {
        private Bitmap bitmapimg; // 이미지 처리

        public MenuData(Bitmap image) {
            bitmapimg = image;
        }

        public Bitmap getImage() {
            return bitmapimg;
        }
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

    // Android M에서는 Uri.fromFile 함수를 사용하였으나 7.0부터는 이 함수를 사용할 시 FileUriExposedException이
    // 발생하므로 아래와 같이 함수를 작성합니다.
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());   //그림저장할때 파일명 지정
        String imageFileName = "IP" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/LeaveOut/"); //LeaveOut라는 경로에 이미지를 저장하기 위함
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);    //확장자를 .jpg로 저장
        return image;
    }

    //카메라 불러오기 버튼
    public void takePhotoButton(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //사진을 찍기 위하여 설정
        File photoFile = null;
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
                imageExtraction();  //이미지 추출
                adapter = new DataAdapter(getApplicationContext(), alist);  // 데이터를 받기위해 데이터어댑터 객체 선언
                list.setAdapter(adapter);   // 리스트뷰에 어댑터 연결
                setWriteAdapter(thumbImage);    //ImageView에 setImageBitmap을 활용하여 해당 이미지에 그림을 띄우기
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
                imageExtraction();  //이미지 추출
                adapter = new DataAdapter(getApplicationContext(), alist);  // 데이터를 받기위해 데이터어댑터 객체 선언
                list.setAdapter(adapter);   // 리스트뷰에 어댑터 연결
                setWriteAdapter(thumbImage);    //ImageView에 setImageBitmap을 활용하여 해당 이미지에 그림을 띄우기


            } catch (Exception e) {
                Log.e("ERROR", e.getMessage().toString());
            }
        }

    }

    //이미지 추출
    protected void imageExtraction() throws IOException {
        //bitmap 형태의 이미지로 가져오기 위해 Thumbnail을 추출.
        iv = (ImageView) findViewById(R.id.write_input_picture);
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
        thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 1024, 768);  //사진 크기를 조절
        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs); //이미지가 클 경우 OutOfMemoryException 발생이 예상되어 압축
    }

    // 뒤로가기
    public void writeBack(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
