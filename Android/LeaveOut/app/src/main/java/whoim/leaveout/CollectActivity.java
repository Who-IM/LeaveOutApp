package whoim.leaveout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import whoim.leaveout.Adapter.GridAdapter;
import whoim.leaveout.Loading.LoadingSQLDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.Server.ImageDownLoad;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.User.UserInfo;

//모아보기
public class CollectActivity extends AppCompatActivity {
    // list
    private ListView list = null;
    private collect_Adapter adapter = null;

    // comment list
    private ArrayList<ListView> comment_list = null;
    private ArrayList<collect_Comment_Adapter> comment_adapter = null;

    // comment 버튼
    private ArrayList<Button> comment_btnlistner = null;
    private boolean comment_flag = true;
    private ArrayList<EditText> comment_edit = null;
    private ArrayList<Button> collect_comment_btn2 = null;

    //like 버튼
    private ArrayList<Button> like_btnlistner = null;
    private int like_count = 0;

    //tab
    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;
    profile_tab tab;


    private ArrayList<GridView> grid_list = null;
    private ArrayList<GridAdapter> gridAdapter = null;

    ArrayList<Bitmap> bitmap = null;
    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance();          // sql에 필요한 데이터 그룹

    int menuCount = 0;  //매뉴 옵션 아이템 순서
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_view_article_layout);

        comment_list = new ArrayList<ListView>();
        comment_btnlistner = new ArrayList<Button>();
        comment_edit = new ArrayList<EditText>();
        comment_adapter = new ArrayList<collect_Comment_Adapter>();
        collect_comment_btn2 = new ArrayList<Button>();

        like_btnlistner = new ArrayList<Button>();

        grid_list = new ArrayList<GridView>();
        gridAdapter = new ArrayList<GridAdapter>();
        bitmap= new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#00FFFFFF"));   //제목 투명하게
        setSupportActionBar(toolbar);   //액션바와 같게 만들어줌
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.

        // 모아보기 listview 셋팅
        setCollect();

        //tab layout 등록
        tabLayout = (TabLayout) findViewById(R.id.public_view_article_tab);
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
        viewPager = (ViewPager) findViewById(R.id.public_view_article_pager);

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

        final Intent data = getIntent();
        if (data != null) {
            try {
                final JSONArray jsondata;
                jsondata = new JSONArray(data.getStringExtra("result"));
                for (int i = 0; i < jsondata.length(); i++) {
                    JSONObject jdata = jsondata.getJSONObject(i);
                    String name = jdata.getString("name"); // 이름
                    String rec_cnt = jdata.getString("rec_cnt"); // 추천 -> 댓글수로 변경 해야됨
                    String view_cnt = jdata.getString("view_cnt"); // 조화수
                    String reg_time = jdata.getString("reg_time"); // 시간
                    String address = jdata.getString("address"); // 주소
                    String text = jdata.getString("text"); // 텍스트 내용
                    int content_num = jdata.getInt("content_num");

                    adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null), name, address, reg_time, view_cnt, rec_cnt, text, content_num);
                }
                list.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //옵션 버튼
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 메뉴버튼이 처음 눌러졌을 때 실행되는 콜백메서드
        // 메뉴버튼을 눌렀을 때 보여줄 menu 에 대해서 정의
        getMenuInflater().inflate(R.menu.collect_menu, menu);
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
            case R.id.collect_menu_location:
                Toast.makeText(getApplicationContext(), "가까운 위치 순서대로", Toast.LENGTH_SHORT).show();
                menuCount = 0;
                return true;

            case R.id.collect_menu_time:
                Toast.makeText(getApplicationContext(), "최신글 순서대로", Toast.LENGTH_SHORT).show();
                menuCount = 1;
                return true;

            case R.id.collect_menu_view:
                Toast.makeText(getApplicationContext(), "조회수 순서대로", Toast.LENGTH_SHORT).show();
                menuCount = 2;
                return true;

            case R.id.collect_menu_recommended:
                Toast.makeText(getApplicationContext(), "추천수 순서대로", Toast.LENGTH_SHORT).show();
                menuCount = 3;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // 옵션 버튼 끝

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
        list = (ListView) findViewById(R.id.public_view_article_listview);

        // 어뎁터 생성민 등록
        adapter = new collect_Adapter(this);
        list.setAdapter(adapter);
    }

    // 댓글 listview 셋팅
    private void setComment(int position, int image, String name, String comment) {
        // 실제 데이터 삽입
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM월 dd일 HH:mm:ss");
        String time = sdfNow.format(new Date(System.currentTimeMillis()));

        comment_adapter.get(position).addItem(getResources().getDrawable(image, null), name, comment, time);
    }

    // 리스트뷰 펼처보기(한화면에)
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
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

    // ------------ public_view_article listview -------------
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

        public void setmListData(int position, String recom_num) {
            mListData.get(position).recom_num = recom_num;
        }

        // 생성자로 값을 받아 셋팅
        public void addItem(Drawable image, String name, String location, String time, String recom_num, String views_num, String contents, int content_num) {
            collect_ListData addInfo = null;
            addInfo = new collect_ListData();
            addInfo.Image = image;
            addInfo.name = name;
            addInfo.location = location;
            addInfo.time = time;
            addInfo.recom_num = recom_num;
            addInfo.views_num = views_num;
            addInfo.contents = contents;
            addInfo.content_num = content_num;

            mListData.add(addInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            collect_ViewHolder holder;

            if (convertView == null) {
                holder = new collect_ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.public_view_article, null);

                holder.Image = (ImageView) convertView.findViewById(R.id.public_view_article_Image);
                holder.name = (TextView) convertView.findViewById(R.id.public_view_article_name);
                holder.location = (TextView) convertView.findViewById(R.id.public_view_article_location);
                holder.time = (TextView) convertView.findViewById(R.id.public_view_article_time);
                holder.recom_num = (TextView) convertView.findViewById(R.id.public_view_article_recom_num);
                holder.views_num = (TextView) convertView.findViewById(R.id.public_view_article_views_num);
                holder.contents = (TextView) convertView.findViewById(R.id.public_view_article_contents);

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
            ImageView iv = (ImageView) convertView.findViewById(R.id.public_view_article_mycomment_image);
            iv.setImageResource(R.drawable.basepicture);

            // 댓글
            if(comment_list.size() == position) {  // ArrayList 자원 재활용
                comment_list.add(position, (ListView) convertView.findViewById(R.id.public_view_article_comment_list));    }
            else {
                comment_list.set(position, (ListView) convertView.findViewById(R.id.public_view_article_comment_list));    }


            // 어뎁터 생성 등록
            if(comment_adapter.size() == position) { // ArrayList 자원 재활용
                comment_adapter.add(position, new collect_Comment_Adapter(CollectActivity.this));     }
            else {
                comment_adapter.set(position, new collect_Comment_Adapter(CollectActivity.this));     }


            // 댓글 edittext
            if(comment_edit.size() == position) { // ArrayList 자원 재활용
                comment_edit.add(position, (EditText) convertView.findViewById(R.id.public_view_article_comment_editText));     }
            else {
                comment_edit.set(position, (EditText) convertView.findViewById(R.id.public_view_article_comment_editText));     }
            comment_edit.get(position).setTag(position);
            comment_edit.get(position).setOnEditorActionListener(new TextView.OnEditorActionListener()
            {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                {
                    if(actionId == EditorInfo.IME_ACTION_DONE)
                    {
                        int pos = (int) v.getTag();  // 포지션값 받아오기

                        // 빈칸 입력시 입력 x
                        if(comment_edit.get(pos).getText().toString().equals("") == false) {
                            setComment(pos, R.drawable.basepicture, "김창석", comment_edit.get(pos).getText().toString());  // 데이터 셋팅
                            comment_adapter.get(pos).notifyDataSetChanged();   // 데이터 변화시
                            comment_list.get(pos).setAdapter(comment_adapter.get(pos));   // 어뎁터 등록
                            setListViewHeightBasedOnChildren(comment_list.get(pos)); // 리스트뷰 펼처보기(한화면에)
                            comment_edit.get(pos).setText("");   // 내용 초기화

                            collect_ListData mData = mListData.get(pos);
                            comment_InsertSQLData(mData.content_num, comment_edit.get(pos).getText().toString());

                            // 입력했는데 감춰져있으면 보이게 셋팅
                            if (comment_list.get(pos).getVisibility() == View.GONE) {
                                comment_flag = false;
                                comment_list.get(pos).setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });

            // getview 초기화시 셋팅
            if(comment_adapter.get(position).getCount() != 0) {
                comment_list.get(position).setAdapter(comment_adapter.get(position));
                setListViewHeightBasedOnChildren(comment_list.get(position)); // 리스트뷰 펼처보기(한화면에)

                // 처음에만 댓글 지우기
                if (comment_flag)
                    comment_list.get(position).setVisibility(View.GONE);
            }


            // 커멘드 버튼 클릭시 처리
            if(comment_btnlistner.size() == position) { // ArrayList 자원 재활용
                comment_btnlistner.add(position, (Button) convertView.findViewById(R.id.public_view_article_comment_btn));    }
            else {
                comment_btnlistner.set(position, (Button) convertView.findViewById(R.id.public_view_article_comment_btn));    }
            comment_btnlistner.get(position).setTag(position); // tag로 listview position 등록
            comment_btnlistner.get(position).setOnClickListener(new View.OnClickListener() { // 댓글 보기 버튼 이벤트
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag(); // listview의 position
                    comment_flag = false;

                    // 리스트뷰에 데이터가 있을시만
                    if (comment_list.size() != 0) {
                        if (comment_list.get(pos).getVisibility() == View.GONE) {
                            comment_list.get(pos).setVisibility(View.VISIBLE);
                        } else {
                            comment_list.get(pos).setVisibility(View.GONE);
                        }
                    }
                }
            });

            //추천하기 숫자 올라가기
            if(like_btnlistner.size() == position)
            {
                like_btnlistner.add(position, (Button) convertView.findViewById(R.id.public_view_article_like_btn));
            }else{
                like_btnlistner.set(position, (Button) convertView.findViewById(R.id.public_view_article_like_btn));
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

            /*// 이미지 처리
            if(grid_list.size() == position) {  // ArrayList 자원 재활용
                grid_list.add((GridView) convertView.findViewById(R.id.public_view_article_grid));
                gridAdapter.add(new GridAdapter(CollectActivity.this));
                content_number_SelectSQLData(mData.content_num, position);
            }*/

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
        public int content_num;
    }
    // -------------------------------------- End public_view_article listview -----------------------

    // 여기부터 public_view_article_comment 부분
    private class collect_Comment_ViewHolder {
        public ImageView Image;
        public TextView name;
        public TextView comment;
        public TextView time;
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
        public void addItem(Drawable image, String name, String comment, String time) {
            collect_Comment_ListData addInfo = null;
            addInfo = new collect_Comment_ListData();
            addInfo.Image = image;
            addInfo.name = name;
            addInfo.comment = comment;
            addInfo.time = time;

            ListData.add(addInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            collect_Comment_ViewHolder holder;

            if (convertView == null) {
                holder = new collect_Comment_ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.public_view_article_comment, null);

                holder.Image = (ImageView) convertView.findViewById(R.id.public_view_article_comment_image);
                holder.name = (TextView) convertView.findViewById(R.id.public_view_article_comment_name);
                holder.comment = (TextView) convertView.findViewById(R.id.public_view_article_comment_text);
                holder.time = (TextView) convertView.findViewById(R.id.public_view_article_comment_time);

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
            holder.time.setText(Data.time);

            // 커멘드 버튼 클릭시 처리
            if(collect_comment_btn2.size() == position) { // ArrayList 자원 재활용
                collect_comment_btn2.add(position, (Button) convertView.findViewById(R.id.public_view_article_comment_btn2));    }
            else {
                collect_comment_btn2.set(position, (Button) convertView.findViewById(R.id.public_view_article_comment_btn2));    }
            collect_comment_btn2.get(position).setOnClickListener(new View.OnClickListener() { // 댓글 보기 버튼 이벤트
                @Override
                public void onClick(View v) {
                    Intent temp = new Intent(getApplicationContext(), CommentActivity.class);
                    startActivity(temp);
                }
            });

            return convertView;
        }
    }

    // 메뉴의 실제 데이터를 저장할 class
    class collect_Comment_ListData {
        public Drawable Image;
        public String name;
        public String comment;
        public String time;
    }

    private void comment_InsertSQLData(final int content_num, final String text) {
        final String mSelectSQL = "insert into comment(content_num, user_name, reg_time) " +
                                   "values(?, ?, now());";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                mDataQueryGroup.clear();        // 초기화
                mDataQueryGroup.addInt(content_num);
                mDataQueryGroup.addString("이름");
                JSONObject data = SQLDataService.getDynamicSQLJSONData(mSelectSQL, mDataQueryGroup, 0, "update");             // select SQL 제이슨
                SQLDataService.putBundleValue(data, "upload", "usernum", UserInfo.getInstance().getUserNum());                 // 번들 데이터 더 추가(유저 id)
                SQLDataService.putBundleValue(data, "upload", "path", "comment");
                SQLDataService.putBundleValue(data, "upload","context","text");
                SQLDataService.putBundleValue(data, "upload", "text", text);        // 번들 데이터 더 추가(내용)
                return data;
            }

            @Override
            public JSONObject getUpLoad() {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {

            }
        };
        LoadingSQLDialog.SQLSendStart(this, loadingSQLListener, ProgressDialog.STYLE_SPINNER, null);       // sql 시작
    }


    private void content_number_SelectSQLData(final int content_num, final int position) {
        final String mSelectSQL = "select files from content where content_num = ?";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                mDataQueryGroup.clear();        // 초기화
                mDataQueryGroup.addInt(content_num);
                JSONObject data = SQLDataService.getDynamicSQLJSONData(mSelectSQL, mDataQueryGroup, -1, "select");             // select SQL 제이슨
                SQLDataService.putBundleValue(data, "download", "context", "files");
                return data;
            }

            @Override
            public JSONObject getUpLoad() {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                if (!responseData.get(0).getJSONArray("result").equals("error")) {
                    JSONArray result = responseData.get(0).getJSONArray("result");     // 결과 값 가져오기
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject fileObject = result.getJSONObject(i);
                        String image = fileObject.getString("image");
                        JSONArray fileArray = new JSONArray(image);

                        if (fileArray.length() != 0) {
                            selectThread th = new selectThread();
                            th.init(fileArray, position);
                            th.start();
                        }
                    }

                }
            }
        };
        LoadingSQLDialog.SQLSendStart(this, loadingSQLListener, ProgressDialog.STYLE_SPINNER, null);       // sql 시작
    }

    public class selectThread extends Thread {
        ImageDownLoad down = new ImageDownLoad();
        JSONArray fileArray;
        int position;

        public void init(JSONArray fileArray, int position) {
            this.fileArray = fileArray;
            this.position = position;
        }

        public void run() {
            for (int j = 0; j < fileArray.length(); j++) {
                try {
                    Bitmap bitmap = down.execute(fileArray.getString(j)).get();
                    gridAdapter.get(position).addItem(bitmap);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    grid_list.get(position).setAdapter(gridAdapter.get(position));
                    setListViewHeightBasedOnChildren(grid_list.get(position)); // 펼쳐보기
                }
            });
        }
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