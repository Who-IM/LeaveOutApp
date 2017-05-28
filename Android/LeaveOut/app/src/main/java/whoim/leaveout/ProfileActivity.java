package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

// 환경설정
public class ProfileActivity extends AppCompatActivity {

    // list
    private ListView list = null;
    private profile_Adapter adapter = null;

    // comment list
    private ArrayList<ListView> profile_list = null;
    private profile_Comment_Adapter profile_adapter = null;
    View header = null; // 리스트뷰 헤더

    // comment 버튼
    private ArrayList<Button> comment_btnlistner = null;
    private boolean profile_flag = true;

    //tab
    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;
    profile_tab tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        // 초기설정 (db필요)
        init(R.drawable.basepicture, "허성문", "gjtjdans123@naver.com");

        profile_list = new ArrayList<ListView>();                              // profile listview
        comment_btnlistner = new ArrayList<Button>();                         // 댓글보기 버튼
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
    }

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

        // 어뎁터 생성민 등록
        adapter = new profile_Adapter(ProfileActivity.this);
        list.setAdapter(adapter);

        // 여기서 db데이터 넣기
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"허성문", "대구 수성구 범어동", "2017.05.08 19:12","250","511","놀러와라");
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"김창석", "대구 수성구 만촌역", "2017.05.21 20:00","500","1000","ddd");
    }

    // 댓글 listview 셋팅
    private void setComment(int image, String name, String comment) {
        // 실제 데이터 삽입
        profile_adapter.addItem(getResources().getDrawable(image, null), name, comment);
    }

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

    // ------------ collect listview -------------
    private class profile_ViewHolder {
        public ImageView Image;
        public TextView name;
        public TextView location;
        public TextView time;
        public TextView recom_num;
        public TextView views_num;
        public TextView contents;
    }

    // 리스트뷰 어뎁터
    private class profile_Adapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<profile_ListData> mListData = new ArrayList<profile_ListData>();

        public profile_Adapter(Context mContext) {
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
        public void addItem(Drawable image, String name, String location, String time, String recom_num, String views_num, String contents) {
            profile_ListData addInfo = null;
            addInfo = new profile_ListData();
            addInfo.Image = image;
            addInfo.name = name;
            addInfo.location = location;
            addInfo.time = time;
            addInfo.recom_num = recom_num;
            addInfo.views_num = views_num;
            addInfo.contents = contents;

            mListData.add(addInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            profile_ViewHolder holder;
            if (convertView == null) {
                holder = new profile_ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.profile, null);

                holder.Image = (ImageView) convertView.findViewById(R.id.profile_Image);
                holder.name = (TextView) convertView.findViewById(R.id.profile_name);
                holder.location = (TextView) convertView.findViewById(R.id.profile_location);
                holder.time = (TextView) convertView.findViewById(R.id.profile_time);
                holder.recom_num = (TextView) convertView.findViewById(R.id.profile_recom_num);
                holder.views_num = (TextView) convertView.findViewById(R.id.profile_views_num);
                holder.contents = (TextView) convertView.findViewById(R.id.profile_contents);

                convertView.setTag(holder);
            }else{
                holder = (profile_ViewHolder) convertView.getTag();
            }

            // 글쓰기 이미지
            ImageView iv = (ImageView) convertView.findViewById(R.id.profile_mycomment_image);
            iv.setImageResource(R.drawable.basepicture);

            profile_ListData mData = mListData.get(position);

            // 이미지 처리
            if (mData.Image != null) {
                holder.Image.setVisibility(View.VISIBLE);
                holder.Image.setImageDrawable(mData.Image);
            }else{
                holder.Image.setVisibility(View.GONE);
            }

            // textView 처리
            holder.name.setText(mData.name);
            holder.location.setText(mData.location);
            holder.time.setText(mData.time);
            holder.recom_num.setText(mData.recom_num);
            holder.views_num.setText(mData.views_num);
            holder.contents.setText(mData.contents);

            // 댓글
            if(profile_list.size() == position) { // ArrayList 자원 재활용
                profile_list.add(position, (ListView) convertView.findViewById(R.id.profile_comment_list));
            } else {
                profile_list.set(position, (ListView) convertView.findViewById(R.id.profile_comment_list));
            }
            // 어뎁터 생성 등록
            profile_adapter = new profile_Comment_Adapter(ProfileActivity.this);
            // 댓글 셋팅(db받아서)
            setComment(R.drawable.basepicture, "김창석", "값싸다");
            setComment(R.drawable.basepicture, "김창석", "값싸다");
            setComment(R.drawable.basepicture, "김창석", "값싸다");
            setComment(R.drawable.basepicture, "김창석", "값싸다");
            profile_list.get(position).setAdapter(profile_adapter);
            setListViewHeightBasedOnChildren(profile_list.get(position)); // 리스트뷰 펼처보기(한화면에)

            // 처음에만 댓글 지우기
            if(profile_flag) {
                profile_list.get(position).setVisibility(View.GONE);
            }

            // 커멘드 버튼 클릭시 처리
            if(comment_btnlistner.size() == position) { // ArrayList 자원 재활용
                comment_btnlistner.add(position, (Button) convertView.findViewById(R.id.profile_comment_btn));
            } else {
                comment_btnlistner.set(position, (Button) convertView.findViewById(R.id.profile_comment_btn));
            }
            comment_btnlistner.get(position).setTag(position);
            comment_btnlistner.get(position).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    profile_flag = false;
                    if(profile_list.get(pos).getVisibility() == View.GONE) {
                        profile_list.get(pos).setVisibility(View.VISIBLE);
                    }
                    else {
                        profile_list.get(pos).setVisibility(View.GONE);
                    }
                }
            });

            return convertView;
        }
    }

    // 메뉴의 실제 데이터를 저장할 class
    class profile_ListData {
        public Drawable Image;
        public String name;
        public String location;
        public String time;
        public String recom_num;
        public String views_num;
        public String contents;
    }
    // -------------------------------------- End collect listview -----------------------

    // 여기부터 collect_comment 부분
    private class profile_Comment_ViewHolder {
        public ImageView Image;
        public TextView name;
        public TextView comment;
    }

    // 리스트뷰 어뎁터
    private class profile_Comment_Adapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<profile_Comment_ListData> ListData = new ArrayList<profile_Comment_ListData>();

        public profile_Comment_Adapter(Context mContext) {
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
        public void addItem(Drawable image, String name, String comment) {
            profile_Comment_ListData addInfo = null;
            addInfo = new profile_Comment_ListData();
            addInfo.Image = image;
            addInfo.name = name;
            addInfo.comment = comment;

            ListData.add(addInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            profile_Comment_ViewHolder holder;
            if (convertView == null) {
                holder = new profile_Comment_ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.profile_comment, null);

                holder.Image = (ImageView) convertView.findViewById(R.id.profile_comment_image);
                holder.name = (TextView) convertView.findViewById(R.id.profile_comment_name);
                holder.comment = (TextView) convertView.findViewById(R.id.profile_comment_text);

                convertView.setTag(holder);
            }else{
                holder = (profile_Comment_ViewHolder) convertView.getTag();
            }

            profile_Comment_ListData Data = ListData.get(position);

            // 이미지 처리
            if (Data.Image != null) {
                holder.Image.setVisibility(View.VISIBLE);
                holder.Image.setImageDrawable(Data.Image);
            }else{
                holder.Image.setVisibility(View.GONE);
            }

            // textView 처리
            holder.name.setText(Data.name);
            holder.comment.setText(Data.comment);

            return convertView;
        }
    }

    // 메뉴의 실제 데이터를 저장할 class
    class profile_Comment_ListData {
        public Drawable Image;
        public String name;
        public String comment;
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