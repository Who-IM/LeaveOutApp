package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

// 글 보기
public class ViewArticleActivity extends AppCompatActivity
{
    // 글보기 리스트 뷰
    private ListView list = null;
    private article_Adapter adapter = null;

    // 글보기(댓글) 리스트 뷰
    private ArrayList<ListView> view_list = null;
    private view_Comment_Adapter view_adapter = null;

    // comment 버튼
    private ArrayList<Button> btnlistner = null;
    private boolean views_flag = true;

    int menuCount = 0;  //매뉴 옵션 아이템 순서

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_article_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#00FFFFFF"));   //제목 투명하게
        setSupportActionBar(toolbar);   //액션바와 같게 만들어줌
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.

        // 댓글
        view_list = new ArrayList<ListView>();
        btnlistner = new ArrayList<Button>();

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
        list = (ListView) findViewById(R.id.view_listview);

        // 어뎁터 생성민 등록
        adapter = new article_Adapter(this);
        list.setAdapter(adapter);

        // 여기서 db데이터 넣기
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"허성문", "대구 수성구 범어동", "2017.05.08 19:12","250","511","놀러와라");
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"김창석", "대구 북구 복현동", "2017.05.24 20:58","50","111","test");
    }

    // 댓글 listview 셋팅
    private void setComment(int image, String name, String comment) {

        // 실제 데이터 삽입
        view_adapter.addItem(getResources().getDrawable(image, null), name, comment);
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
                convertView = inflater.inflate(R.layout.view_article, null);

                holder.Image = (ImageView) convertView.findViewById(R.id.view_Image);
                holder.name = (TextView) convertView.findViewById(R.id.view_name);
                holder.location = (TextView) convertView.findViewById(R.id.view_location);
                holder.time = (TextView) convertView.findViewById(R.id.view_time);
                holder.recom_num = (TextView) convertView.findViewById(R.id.view_recom_num);
                holder.views_num = (TextView) convertView.findViewById(R.id.view_views_num);
                holder.contents = (TextView) convertView.findViewById(R.id.view_contents);

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
            ImageView iv = (ImageView) convertView.findViewById(R.id.view_mycomment_image);
            iv.setImageResource(R.drawable.basepicture);

            // textView 처리
            holder.name.setText(mData.name);
            holder.location.setText(mData.location);
            holder.time.setText(mData.time);
            holder.recom_num.setText(mData.recom_num);
            holder.views_num.setText(mData.views_num);
            holder.contents.setText(mData.contents);

            // 댓글
            view_list.add((ListView) convertView.findViewById(R.id.view_comment_list));
            // 어뎁터 생성민 등록
            view_adapter = new view_Comment_Adapter(ViewArticleActivity.this);
            view_list.get(position).setAdapter(view_adapter);
            // 댓글 셋팅(db받아서)
            setComment(R.drawable.basepicture, "김창석", "값싸다");
            setComment(R.drawable.basepicture, "김창석", "값싸다");
            setComment(R.drawable.basepicture, "김창석", "값싸다");
            setComment(R.drawable.basepicture, "김창석", "값싸다");
            setListViewHeightBasedOnChildren(view_list.get(position)); // 리스트뷰 펼처보기(한화면에)

            // 처음에만 댓글 지우기
            if(views_flag) {
                view_list.get(position).setVisibility(View.GONE);
            }

            // 커멘드 버튼 클릭시 처리
            btnlistner.add((Button) convertView.findViewById(R.id.views_comment_btn));
            btnlistner.get(position).setTag(position);
            btnlistner.get(position).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    views_flag = false;
                    if(view_list.get(pos).getVisibility() == View.GONE) {
                        view_list.get(pos).setVisibility(View.VISIBLE);
                    }
                    else {
                        view_list.get(pos).setVisibility(View.GONE);
                    }
                }
            });

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
        public void addItem(Drawable image, String name, String comment) {
            view_Comment_ListData addInfo = null;
            addInfo = new view_Comment_ListData();
            addInfo.Image = image;
            addInfo.name = name;
            addInfo.comment = comment;

            ListData.add(addInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            view_Comment_ViewHolder holder;
            if (convertView == null) {
                holder = new view_Comment_ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_article_comment, null);

                holder.Image = (ImageView) convertView.findViewById(R.id.view_comment_image);
                holder.name = (TextView) convertView.findViewById(R.id.view_comment_name);
                holder.comment = (TextView) convertView.findViewById(R.id.view_comment_text);

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

            return convertView;
        }
    }

    // 메뉴의 실제 데이터를 저장할 class
    class view_Comment_ListData {
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


