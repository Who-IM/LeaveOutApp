package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

//모아보기
public class CollectActivity extends AppCompatActivity {
    // list
    private ListView list = null;
    private collect_Adapter adapter = null;

    // comment list
    private ArrayList<ListView> comment_list = null;
    private collect_Comment_Adapter comment_adapter = null;

    // comment 버튼
    private ArrayList<Button> comment_btnlistner = null;
    private boolean comment_flag = true;

    //tab
    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;
    profile_tab tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_layout);

        comment_list = new ArrayList<ListView>();
        comment_btnlistner = new ArrayList<Button>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#00FFFFFF"));   //제목 투명하게
        setSupportActionBar(toolbar);   //액션바와 같게 만들어줌
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.

        // 모아보기 listview 셋팅
        setCollect();

        //tab layout 등록
        tabLayout = (TabLayout) findViewById(R.id.collect_tab);
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

        //viewpager 등록
        viewPager = (ViewPager) findViewById(R.id.collect_pager);

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

    // 텝 설정
    private class profile_tab
    {
        private profile_tab(String text)
        {
            tabLayout.addTab(tabLayout.newTab().setText(text));
        }
    }

    // 모아보기 listview 셋팅
    private void setCollect() {
        // 메뉴
        list = (ListView) findViewById(R.id.collect_listview);

        // 어뎁터 생성민 등록
        adapter = new collect_Adapter(this);
        list.setAdapter(adapter);

        // 여기서 db데이터 넣기
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"허성문", "대구 수성구 범어동", "2017.05.08 19:12","250","511","놀러와라");
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"김창석", "대구 수성구 만촌역", "2017.05.21 20:00","500","1000","ddd");
    }

    // 댓글 listview 셋팅
    private void setComment(int image, String name, String comment) {

        // 실제 데이터 삽입
        comment_adapter.addItem(getResources().getDrawable(image, null), name, comment);
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
    private class collect_ViewHolder {
        public ImageView Image;
        public TextView name;
        public TextView location;
        public TextView time;
        public TextView recom_num;
        public TextView views_num;
        public TextView contents;
    }

    // 리스트뷰 어뎁터
    private class collect_Adapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<collect_ListData> mListData = new ArrayList<collect_ListData>();

        public collect_Adapter(Context mContext) {
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
            collect_ListData addInfo = null;
            addInfo = new collect_ListData();
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
            collect_ViewHolder holder;

            int temp;
            if (convertView == null) {
                holder = new collect_ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.collect, null);

                holder.Image = (ImageView) convertView.findViewById(R.id.collect_Image);
                holder.name = (TextView) convertView.findViewById(R.id.collect_name);
                holder.location = (TextView) convertView.findViewById(R.id.collect_location);
                holder.time = (TextView) convertView.findViewById(R.id.collect_time);
                holder.recom_num = (TextView) convertView.findViewById(R.id.collect_recom_num);
                holder.views_num = (TextView) convertView.findViewById(R.id.collect_views_num);
                holder.contents = (TextView) convertView.findViewById(R.id.collect_contents);

                convertView.setTag(holder);
            }else{
                holder = (collect_ViewHolder) convertView.getTag();
            }

            final collect_ListData mData = mListData.get(position);

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

            // 글쓰기 이미지
            ImageView iv = (ImageView) convertView.findViewById(R.id.collect_mycomment_image);
            iv.setImageResource(R.drawable.basepicture);

            // 댓글
            if(comment_list.size() == position) {  // ArrayList 자원 재활용
                comment_list.add(position, (ListView) convertView.findViewById(R.id.collect_comment_list));
            } else {
                comment_list.set(position, (ListView) convertView.findViewById(R.id.collect_comment_list));
            }
            // 어뎁터 생성민 등록
            comment_adapter = new collect_Comment_Adapter(CollectActivity.this);
            comment_list.get(position).setAdapter(comment_adapter);
            // 댓글 셋팅(db받아서)
            setComment(R.drawable.basepicture, "김창석", "값싸다");
            setComment(R.drawable.basepicture, "김창석", "값싸다");
            setComment(R.drawable.basepicture, "김창석", "값싸다");
            setComment(R.drawable.basepicture, "김창석", "값싸다");
            setListViewHeightBasedOnChildren(comment_list.get(position)); // 리스트뷰 펼처보기(한화면에)

            // 처음에만 댓글 지우기
            if(comment_flag) {
                comment_list.get(position).setVisibility(View.GONE);
            }

            // 커멘드 버튼 클릭시 처리
            if(comment_btnlistner.size() == position) { // ArrayList 자원 재활용
                comment_btnlistner.add(position, (Button) convertView.findViewById(R.id.collect_comment_btn));
            } else {
                comment_btnlistner.set(position, (Button) convertView.findViewById(R.id.collect_comment_btn));
            }
            comment_btnlistner.get(position).setTag(position); // tag로 listview position 등록
            comment_btnlistner.get(position).setOnClickListener(new View.OnClickListener() { // 댓글 보기 버튼 이벤트
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag(); // listview의 position
                    comment_flag = false;

                    if(comment_list.get(pos).getVisibility() == View.GONE) {
                        comment_list.get(pos).setVisibility(View.VISIBLE);
                    }
                    else {
                        comment_list.get(pos).setVisibility(View.GONE);
                    }
                }
            });

            return convertView;
        }
    }

    // 메뉴의 실제 데이터를 저장할 class
    class collect_ListData {
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
    private class collect_Comment_ViewHolder {
        public ImageView Image;
        public TextView name;
        public TextView comment;
    }

    // 리스트뷰 어뎁터
    private class collect_Comment_Adapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<collect_Comment_ListData> ListData = new ArrayList<collect_Comment_ListData>();

        public collect_Comment_Adapter(Context mContext) {
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
            collect_Comment_ListData addInfo = null;
            addInfo = new collect_Comment_ListData();
            addInfo.Image = image;
            addInfo.name = name;
            addInfo.comment = comment;

            ListData.add(addInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            collect_Comment_ViewHolder holder;

            if (convertView == null) {
                holder = new collect_Comment_ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.collect_comment, null);

                holder.Image = (ImageView) convertView.findViewById(R.id.collect_comment_image);
                holder.name = (TextView) convertView.findViewById(R.id.collect_comment_name);
                holder.comment = (TextView) convertView.findViewById(R.id.collect_comment_text);

                convertView.setTag(holder);
            }else{
                holder = (collect_Comment_ViewHolder) convertView.getTag();
            }

            collect_Comment_ListData Data = ListData.get(position);

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
    class collect_Comment_ListData {
        public Drawable Image;
        public String name;
        public String comment;
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