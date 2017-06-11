package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import whoim.leaveout.Adapter.GridAdapter;

// 환경설정
public class ProfileActivity extends AppCompatActivity {

    // list
    private ListView list = null;
    private profile_Adapter adapter = null;

    // comment list
    private ArrayList<ListView> profile_list = null;
    private ArrayList<profile_Comment_Adapter> profile_adapter = null;
    View header = null; // 리스트뷰 헤더

    // comment 버튼
    private ArrayList<Button> comment_btnlistner = null;
    private boolean profile_flag = true;
    private ArrayList<EditText> profile_edit = null;

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

    private ArrayList<GridView> grid_list = null;
    private ArrayList<GridAdapter> gridAdapter = null;

    //like 버튼
    private ArrayList<Button> like_btnlistner = null;
    private int like_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#00FFFFFF"));   //제목 투명하게
        setSupportActionBar(toolbar);   //액션바와 같게 만들어줌
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.

        // 초기설정 (db필요)
        init(R.drawable.basepicture, "허성문", "gjtjdans123@naver.com");

        profile_list = new ArrayList<ListView>();                              // profile listview
        comment_btnlistner = new ArrayList<Button>();                         // 댓글보기 버튼
        profile_adapter = new ArrayList<profile_Comment_Adapter>();
        profile_edit = new ArrayList<EditText>();

        like_btnlistner = new ArrayList<Button>();

        grid_list = new ArrayList<GridView>();
        gridAdapter = new ArrayList<GridAdapter>();

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

    //갤러리 불러오기
    public void profileAddImage(View v)
    {
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
        //bitmap 형태의 이미지로 가져오기 위해 Thumbnail을 추출.
        iv = (ImageView) findViewById(R.id.profile_title_image);
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
        thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 100, 100);  //사진 크기를 조절
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bs); //이미지가 클 경우 OutOfMemoryException 발생이 예상되어 압축
        profile_image.setImageBitmap(thumbImage);
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

        // 어뎁터 생성민 등록
        adapter = new profile_Adapter(ProfileActivity.this);
        list.setAdapter(adapter);

        // 여기서 db데이터 넣기
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"허성문", "대구 수성구 범어동", "2017.05.08 19:12","250","511","놀러와라");
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"김창석", "대구 수성구 만촌역", "2017.05.21 20:00","500","1000","ddd");
    }

    // 댓글 listview 셋팅
    private void setComment(int position, int image, String name, String comment) {
        // 실제 데이터 삽입
        profile_adapter.get(position).addItem(getResources().getDrawable(image, null), name, comment);
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

    // ------------ profile listview -------------
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

        public void setmListData(int position, String recom_num) {
            mListData.get(position).recom_num = recom_num;
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
            if(profile_adapter.size() == position) { // ArrayList 자원 재활용
                profile_adapter.add(position, new profile_Comment_Adapter(ProfileActivity.this));     }
            else {
                profile_adapter.set(position, new profile_Comment_Adapter(ProfileActivity.this));     }


            // 댓글 edittext
            if(profile_edit.size() == position) { // ArrayList 자원 재활용
                profile_edit.add(position, (EditText) convertView.findViewById(R.id.profile_comment_editText));     }
            else {
                profile_edit.set(position, (EditText) convertView.findViewById(R.id.profile_comment_editText));     }
            profile_edit.get(position).setTag(position);
            profile_edit.get(position).setOnEditorActionListener(new TextView.OnEditorActionListener()
            {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                {
                    if(actionId == EditorInfo.IME_ACTION_DONE)
                    {
                        int pos = (int) v.getTag();  // 포지션값 받아오기

                        // 빈칸 입력시 입력 x
                        if(profile_edit.get(pos).getText().toString().equals("") == false) {
                            setComment(pos, R.drawable.basepicture, "김창석", profile_edit.get(pos).getText().toString());  // 데이터 셋팅
                            profile_list.get(pos).setAdapter(profile_adapter.get(pos));   // 어뎁터 등록
                            profile_adapter.get(pos).notifyDataSetChanged();   // 데이터 변화시
                            setListViewHeightBasedOnChildren(profile_list.get(pos)); // 리스트뷰 펼처보기(한화면에)
                            profile_edit.get(pos).setText("");   // 내용 초기화

                            // 입력했는데 감춰져있으면 보이게 셋팅
                            if (profile_list.get(pos).getVisibility() == View.GONE) {
                                profile_flag = false;
                                profile_list.get(pos).setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });


            // getview 초기화시 셋팅
            if(profile_adapter.get(position).getCount() != 0) {
                profile_list.get(position).setAdapter(profile_adapter.get(position));
                setListViewHeightBasedOnChildren(profile_list.get(position)); // 리스트뷰 펼처보기(한화면에)

                // 처음에만 댓글 지우기
                if (profile_flag)
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

                    // 리스트뷰에 데이터가 있을시만
                    if(profile_list.size() != 0) {
                        if (profile_list.get(pos).getVisibility() == View.GONE) {
                            profile_list.get(pos).setVisibility(View.VISIBLE);
                        } else {
                            profile_list.get(pos).setVisibility(View.GONE);
                        }
                    }
                }
            });

            //추천하기 숫자 올라가기
            if(like_btnlistner.size() == position)
            {
                like_btnlistner.add(position, (Button) convertView.findViewById(R.id.profile_like_btn));
            }else{
                like_btnlistner.set(position, (Button) convertView.findViewById(R.id.profile_like_btn));
            }
            like_btnlistner.get(position).setTag(position);
            like_btnlistner.get(position).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    int pos = (int) v.getTag();
                    like_count++;
                    adapter.setmListData(pos, like_count+"");
                    adapter.notifyDataSetChanged();
                }
            });

            // 이미지 처리
            if(grid_list.size() == position) {  // ArrayList 자원 재활용
                grid_list.add(position, (GridView) convertView.findViewById(R.id.profile_grid));    }
            else {
                grid_list.set(position, (GridView) convertView.findViewById(R.id.profile_grid));    }

            // 어뎁터 생성 등록
            if(gridAdapter.size() == position) { // ArrayList 자원 재활용
                gridAdapter.add(position, new GridAdapter(ProfileActivity.this));     }
            else {
                gridAdapter.set(position, new GridAdapter(ProfileActivity.this));     }

            if(position == 0) {
                // 데이터는 동적으로 apadter에 저장
                gridAdapter.get(position).addItem(getResources().getDrawable(R.drawable.basepicture, null));
                gridAdapter.get(position).addItem(getResources().getDrawable(R.drawable.basepicture, null));
                gridAdapter.get(position).addItem(getResources().getDrawable(R.drawable.basepicture, null));
            } else if(position == 1) {
                gridAdapter.get(position).addItem(getResources().getDrawable(R.drawable.basepicture, null));
                gridAdapter.get(position).addItem(getResources().getDrawable(R.drawable.basepicture, null));
            }
            grid_list.get(position).setAdapter(gridAdapter.get(position));
            setListViewHeightBasedOnChildren(grid_list.get(position)); // 펼쳐보기
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
        profile_image = (ImageView) findViewById(R.id.profile_title_image);
        profile_image.setImageResource(image);

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