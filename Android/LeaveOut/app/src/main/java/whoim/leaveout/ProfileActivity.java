package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

// 환경설정
public class ProfileActivity extends AppCompatActivity {

    // 메뉴 관련 인스턴스
    private ListView list;
    private DrawerLayout Drawer;
    private ImageButton menu_btn;

    profile_Adapter adapter; // 데이터를 연결할 Adapter
    ArrayList<profileData> alist; // 데이터를 담을 자료구조
    ScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        // 초기설정 (db필요)
        init(R.drawable.basepicture, "허성문", "gjtjdans123@naver.com");

        // 매뉴 구성
        list = (ListView) findViewById(R.id.proflie_listview);
        scroll = (ScrollView) findViewById(R.id.profile_scroll);

        setMenuCustom();
    }

    public void setItem(int image, String name, String location, String time, String recom_num, String views_num, String contents) {
        adapter.add(new profileData(image, name, location, time, recom_num, views_num, contents));
    }

    // 메뉴 커스텀 (나중에 DB받아서 수정)
    private void setMenuCustom() {

        // ArrayList객체를 생성합니다
        alist = new ArrayList<profileData>();
        // 데이터를 받기위해 데이터어댑터 객체 선언
        adapter = new profile_Adapter(this, alist);

        setItem(R.drawable.basepicture, "허성문", "대구 수성구 범어동", "2017.05.08 19:12","250","511","놀러와라");
        setItem(R.drawable.basepicture, "김창석", "대구 복현동 뚝불", "2017.05.08 19:22","1230","2325","값싸다");

        // 리스트뷰에 어댑터 연결
        list.setAdapter(adapter);

        list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scroll.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    // 메뉴 커스텀
    private class profile_Adapter extends ArrayAdapter<profileData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public profile_Adapter(Context context, ArrayList<profileData> object) {
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
                view = mInflater.inflate(R.layout.profile, null);
            } else  {
                view = v;
            }

            // 자료를 받는다.
            final profileData data = this.getItem(position);

            // 자기 프로필
            if (data != null) {
                // 사진
                ImageView iv = (ImageView) view.findViewById(R.id.profile_Image);
                iv.setImageResource(data.getImage());

                // 장소
                TextView tv = (TextView) view.findViewById(R.id.profile_location);
                tv.setText(data.getLocation());

                // 시간
                TextView tv2 = (TextView) view.findViewById(R.id.profile_time);
                tv2.setText(data.getTime());

                // 추천수
                TextView tv3 = (TextView) view.findViewById(R.id.profile_recom_num);
                tv3.setText(data.getRecom_num());

                // 조회수
                TextView tv4 = (TextView) view.findViewById(R.id.profile_views_num);
                tv4.setText(data.getViews_num());

                // 글
                TextView tv5 = (TextView) view.findViewById(R.id.profile_contents);
                tv5.setText(data.getContents());
            }

            return view;
        }
    }       // DataAdapter class -- END --

    // menuData안에 받은 값을 직접 할당
    private class profileData {
        public int Image;
        public String name;
        public String location;
        public String time;
        public String recom_num;
        public String views_num;
        public String contents;

        public profileData(int image, String name, String location, String time, String recom_num, String views_num, String contents) {
            this.Image = image;
            this.name = name;
            this.location = location;
            this.time = time;
            this.recom_num = recom_num;
            this.views_num = views_num;
            this.contents = contents;
        }

        public int getImage() { return Image; }
        public String getName() { return name; }
        public String getLocation() { return location; }
        public String getTime() { return time; }
        public String getRecom_num() { return recom_num; }
        public String getViews_num() { return views_num; }
        public String getContents() { return contents; }


    }    // profileData class -- END --

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