package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import whoim.leaveout.Adapter.GridAdapter;

// 글 보기
public class ViewArticleActivity extends AppCompatActivity
{
    private Intent data;        // 데이터
    JSONObject jsondata;      // 제이슨 데이터

    // 글보기 리스트 뷰
    private ListView list = null;
    private article_Adapter adapter = null;

    // 글보기(댓글) 리스트 뷰
    private ArrayList<ListView> view_list = null;
    private ArrayList<view_Comment_Adapter> view_adapter = null;

    // comment 버튼
    private ArrayList<Button> view_btnlistener = null;
    private boolean view_flag = true;
    private ArrayList<EditText> view_edit = null;
    private ArrayList<Button> viewArticle_comment_btn2 = null;

    int menuCount = 0;  //매뉴 옵션 아이템 순서

    private ArrayList<GridView> grid_list = null;
    private ArrayList<GridAdapter> gridAdapter = null;

    //like 버튼
    private ArrayList<Button> like_btnlistner = null;
    private int like_count = 0;

    // tabLayout 및 toobar 이름 수정
    private TabLayout public_view_article_tab = null;
    private TextView public_view_article_title = null;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_view_article_layout);

        data = getIntent();
        if(data != null) {
            try {
                jsondata = new JSONObject(data.getStringExtra("responseData"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#00FFFFFF"));   //제목 투명하게
        setSupportActionBar(toolbar);   //액션바와 같게 만들어줌
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.

        // tabLayout 및 toobar 이름 수정
        public_view_article_tab = (TabLayout) findViewById(R.id.public_view_article_tab);
        public_view_article_tab.setVisibility(View.GONE);
        public_view_article_title = (TextView) findViewById(R.id.public_view_article_title);
        public_view_article_title.setText("글 보기");

        // 댓글
        view_list = new ArrayList<ListView>();
        view_btnlistener = new ArrayList<Button>();
        view_edit = new ArrayList<EditText>();
        view_adapter = new ArrayList<view_Comment_Adapter>();
        viewArticle_comment_btn2 = new ArrayList<Button>();

        like_btnlistner = new ArrayList<Button>();

        grid_list = new ArrayList<GridView>();
        gridAdapter = new ArrayList<GridAdapter>();
        // 모아보기 listview 셋팅
        setCollect();
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

    // 모아보기 listview 셋팅
    private void setCollect() {
        // 메뉴
        list = (ListView) findViewById(R.id.public_view_article_listview);

        // 어뎁터 생성민 등록
        adapter = new article_Adapter(this);
        list.setAdapter(adapter);

        // 여기서 db데이터 넣기
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"허성문", "대구 수성구 범어동", "2017.05.08 19:12","250","511","놀러와라");
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"김창석", "대구 북구 복현동", "2017.05.24 20:58","50","111","test");
    }

    // 댓글 listview 셋팅
    private void setComment(int position, int image, String name, String comment) {
        // 실제 데이터 삽입
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM월 dd일 HH:mm:ss");
        String time = sdfNow.format(new Date(System.currentTimeMillis()));

        // 실제 데이터 삽입
        view_adapter.get(position).addItem(getResources().getDrawable(image, null), name, comment, time);
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
    // 리스트뷰 홀더
    private class article_ViewHolder {
        public ImageView Image;
        public TextView name;
        public TextView location;
        public TextView time;
        public TextView recom_num;
        public TextView views_num;
        public TextView contents;
    }

    // 리스트뷰 어뎁터
    private class article_Adapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<article_ListData> mListData = new ArrayList<article_ListData>();

        public article_Adapter(Context mContext) {
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
            article_ListData addInfo = null;
            addInfo = new article_ListData();
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
            article_ViewHolder holder;
            if (convertView == null) {
                holder = new article_ViewHolder();

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
                holder = (article_ViewHolder) convertView.getTag();
            }

            article_ListData mData = mListData.get(position);

            // 이미지 처리
            if (mData.Image != null) {
                holder.Image.setVisibility(View.VISIBLE);
                holder.Image.setImageDrawable(mData.Image);
            }else{
                holder.Image.setVisibility(View.GONE);
            }

            // 글쓰기 이미지
            ImageView iv = (ImageView) convertView.findViewById(R.id.public_view_article_mycomment_image);
            iv.setImageResource(R.drawable.basepicture);

            // textView 처리
            holder.name.setText(mData.name);
            holder.location.setText(mData.location);
            holder.time.setText(mData.time);
            holder.recom_num.setText(mData.recom_num);
            holder.views_num.setText(mData.views_num);
            holder.contents.setText(mData.contents);

            // 댓글
            if(view_list.size() == position) { // ArrayList 자원 재활용
                view_list.add(position, (ListView) convertView.findViewById(R.id.public_view_article_comment_list));
            } else {
                view_list.set(position, (ListView) convertView.findViewById(R.id.public_view_article_comment_list));
            }


            // 어뎁터 생성 등록
            if(view_adapter.size() == position) { // ArrayList 자원 재활용
                view_adapter.add(position, new view_Comment_Adapter(ViewArticleActivity.this));     }
            else {
                view_adapter.set(position, new view_Comment_Adapter(ViewArticleActivity.this));     }


            // 댓글 edittext
            if(view_edit.size() == position) { // ArrayList 자원 재활용
                view_edit.add(position, (EditText) convertView.findViewById(R.id.public_view_article_comment_editText));     }
            else {
                view_edit.set(position, (EditText) convertView.findViewById(R.id.public_view_article_comment_editText));     }
            view_edit.get(position).setTag(position);
            view_edit.get(position).setOnEditorActionListener(new TextView.OnEditorActionListener()
            {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                {
                    if(actionId == EditorInfo.IME_ACTION_DONE)
                    {
                        int pos = (int) v.getTag();  // 포지션값 받아오기

                        // 빈칸 입력시 입력 x
                        if(view_edit.get(pos).getText().toString().equals("") == false) {
                            setComment(pos, R.drawable.basepicture, "김창석", view_edit.get(pos).getText().toString());  // 데이터 셋팅
                            view_adapter.get(pos).notifyDataSetChanged();   // 데이터 변화시
                            view_list.get(pos).setAdapter(view_adapter.get(pos));   // 어뎁터 등록
                            setListViewHeightBasedOnChildren(view_list.get(pos)); // 리스트뷰 펼처보기(한화면에)
                            view_edit.get(pos).setText("");   // 내용 초기화

                            // 입력했는데 감춰져있으면 보이게 셋팅
                            if (view_list.get(pos).getVisibility() == View.GONE) {
                                view_flag = false;
                                view_list.get(pos).setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });


            // getview 초기화시 셋팅
            if(view_adapter.get(position).getCount() != 0) {
                view_list.get(position).setAdapter(view_adapter.get(position));
                setListViewHeightBasedOnChildren(view_list.get(position)); // 리스트뷰 펼처보기(한화면에)

                // 처음에만 댓글 지우기
                if (view_flag)
                    view_list.get(position).setVisibility(View.GONE);
            }


            // 커멘드 버튼 클릭시 처리
            if(view_btnlistener.size() == position) { // ArrayList 자원 재활용
                view_btnlistener.add(position, (Button) convertView.findViewById(R.id.public_view_article_comment_btn));
            } else {
                view_btnlistener.set(position, (Button) convertView.findViewById(R.id.public_view_article_comment_btn));
            }
            view_btnlistener.get(position).setTag(position); // tag로 listview의 position 등록
            view_btnlistener.get(position).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();  // listview의 position
                    view_flag = false;

                    // 리스트뷰에 데이터가 있을시만
                    if(view_list.size() != 0) {
                        if (view_list.get(pos).getVisibility() == View.GONE) {
                            view_list.get(pos).setVisibility(View.VISIBLE);
                        } else {
                            view_list.get(pos).setVisibility(View.GONE);
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

            // 이미지 처리
            if(grid_list.size() == position) {  // ArrayList 자원 재활용
                grid_list.add(position, (GridView) convertView.findViewById(R.id.public_view_article_grid));    }
            else {
                grid_list.set(position, (GridView) convertView.findViewById(R.id.public_view_article_grid));    }

            // 어뎁터 생성 등록
            if(gridAdapter.size() == position) { // ArrayList 자원 재활용
                gridAdapter.add(position, new GridAdapter(ViewArticleActivity.this));     }
            else {
                gridAdapter.set(position, new GridAdapter(ViewArticleActivity.this));     }

            if(position == 0) {
                // 데이터는 동적으로 apadter에 저장
                gridAdapter.get(position).addItem(((BitmapDrawable)getResources().getDrawable(R.drawable.basepicture, null)).getBitmap());
                gridAdapter.get(position).addItem(((BitmapDrawable)getResources().getDrawable(R.drawable.basepicture, null)).getBitmap());
                gridAdapter.get(position).addItem(((BitmapDrawable)getResources().getDrawable(R.drawable.basepicture, null)).getBitmap());
            } else if(position == 1) {
                gridAdapter.get(position).addItem(((BitmapDrawable)getResources().getDrawable(R.drawable.basepicture, null)).getBitmap());
                gridAdapter.get(position).addItem(((BitmapDrawable)getResources().getDrawable(R.drawable.basepicture, null)).getBitmap());
            }
            grid_list.get(position).setAdapter(gridAdapter.get(position));
            setListViewHeightBasedOnChildren(grid_list.get(position)); // 펼쳐보기
            return convertView;
        }
    }

    // 메뉴의 실제 데이터를 저장할 class
    class article_ListData {
        public Drawable Image;
        public String name;
        public String location;
        public String time;
        public String recom_num;
        public String views_num;
        public String contents;
    }
    // -------------------------------- end article list-------------------------------------------

    // 여기부터 viewarticle_comment 부분
    private class view_Comment_ViewHolder {
        public ImageView Image;
        public TextView name;
        public TextView comment;
        public TextView time;
    }

    // 리스트뷰 어뎁터
    private class view_Comment_Adapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<view_Comment_ListData> ListData = new ArrayList<view_Comment_ListData>();

        public view_Comment_Adapter(Context mContext) {
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
            view_Comment_ListData addInfo = null;
            addInfo = new view_Comment_ListData();
            addInfo.Image = image;
            addInfo.name = name;
            addInfo.comment = comment;
            addInfo.time = time;

            ListData.add(addInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            view_Comment_ViewHolder holder;
            if (convertView == null) {
                holder = new view_Comment_ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.public_view_article_comment, null);

                holder.Image = (ImageView) convertView.findViewById(R.id.public_view_article_comment_image);
                holder.name = (TextView) convertView.findViewById(R.id.public_view_article_comment_name);
                holder.comment = (TextView) convertView.findViewById(R.id.public_view_article_comment_text);
                holder.time = (TextView) convertView.findViewById(R.id.public_view_article_comment_time);

                convertView.setTag(holder);
            }else{
                holder = (view_Comment_ViewHolder) convertView.getTag();
            }

            view_Comment_ListData Data = ListData.get(position);

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
            if(viewArticle_comment_btn2.size() == position) { // ArrayList 자원 재활용
                viewArticle_comment_btn2.add(position, (Button) convertView.findViewById(R.id.public_view_article_comment_btn2));    }
            else {
                viewArticle_comment_btn2.set(position, (Button) convertView.findViewById(R.id.public_view_article_comment_btn2));    }
            viewArticle_comment_btn2.get(position).setOnClickListener(new View.OnClickListener() { // 댓글 보기 버튼 이벤트
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
    class view_Comment_ListData {
        public Drawable Image;
        public String name;
        public String comment;
        public String time;
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


