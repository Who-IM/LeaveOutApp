package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.tsengvn.typekit.TypekitContextWrapper;
import java.util.ArrayList;

// 환경설정
public class ProfileActivity extends AppCompatActivity {

    // 메뉴 관련 인스턴스
    private ListView list;
    profile_DataAdapter adapter; // 데이터를 연결할 Adapter
    ArrayList<profileData> alist; // 데이터를 담을 자료구조

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        // 초기설정 (db필요)
        init(R.drawable.basepicture, "허성문", "gjtjdans123@naver.com");

        // 매뉴 구성
        list = (ListView) findViewById(R.id.proflie_listview);

        // ArrayList객체를 생성합니다
        alist = new ArrayList<profileData>();

        adapter = new profile_DataAdapter(getApplicationContext(), alist);  // 데이터를 받기위해 데이터어댑터 객체 선언

        setProfileAdapter(R.drawable.basepicture, "허성문", "대구 북구 복현동 영진전문대", "2017.05.12 17:12"
                ,"30", "500", "공부" );

        list.setAdapter(adapter);   // 리스트뷰에 어댑터 연결
    }

    // 프로필 데이터 셋팅
    public void setProfileAdapter(int image, String name, String location, String time, String recom_num, String views_num, String contents) {
        adapter.add(new profileData(image, name, location, time, recom_num, views_num, contents));
    }

    // 메뉴 커스텀
    private class profile_DataAdapter extends ArrayAdapter<profileData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public profile_DataAdapter(Context context, ArrayList<profileData> object) {
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

            // view 구성하기
            if (v == null) {
                view = mInflater.inflate(R.layout.profile, null);
            } else {
                view = v;
            }

            // 자료를 받는다.
            final profileData data = this.getItem(position);

            if (data != null) {
                // 프로필 글보기 사진
                ImageView iv = (ImageView) view.findViewById(R.id.profile_Image);
                iv.setImageResource(data.getImage());

                // 프로필 글보기 이름
                TextView tx1 = (TextView) view.findViewById(R.id.profile_name);
                tx1.setText(data.getName());

                // 프로필 글보기 장소
                TextView tx2 = (TextView) view.findViewById(R.id.profile_location);
                tx2.setText(data.getLocation());

                // 프로필 글보기 시간
                TextView tx3 = (TextView) view.findViewById(R.id.profile_time);
                tx3.setText(data.getTime());

                // 프로필 글보기 추천수
                TextView tx4 = (TextView) view.findViewById(R.id.profile_recom_num);
                tx4.setText(data.getRecom_num());

                // 프로필 글보기 조회수
                TextView tx5 = (TextView) view.findViewById(R.id.profile_views_num);
                tx5.setText(data.getViews_num());

                // 프로필 글보기 글내용
                TextView tx6 = (TextView) view.findViewById(R.id.profile_contents);
                tx6.setText(data.getContents());
            }
            return view;
        }
    }

    // 프로필에 글보기 data
    private class profileData {
        private int img; // 이미지 처리
        public String name;
        public String location;
        public String time;
        public String recom_num;
        public String views_num;
        public String contents;

        // 생성자
        public profileData(int image, String name, String location, String time, String recom_num, String views_num, String contents) {
            img = image;
            this.name = name;
            this.location = location;
            this.time = time;
            this.recom_num = recom_num;
            this.views_num = views_num;
            this.contents = contents;
        }

        // getter
        public int getImage() {
            return img;
        }
        public String getName() { return name; }
        public String getLocation() { return location; }
        public String getTime() { return time; }
        public String getRecom_num() { return recom_num; }
        public String getViews_num() { return views_num; }
        public String getContents() { return contents; }
    }

    /* 초기설정 첫번째는 사진 : db에서
                두번째는 이름 : db에서
                세번째는 이메일 : db에서  */
    public void init(int image, String name, String email) {
        // 프로필 사진
        ImageView iv1 = (ImageView) findViewById(R.id.profile_title_image);
        iv1.setImageResource(image);

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
        startActivity(intent);
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}