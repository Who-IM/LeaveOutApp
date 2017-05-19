package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

// 글 보기
public class ViewArticleActivity extends AppCompatActivity
{
    private ListView list = null;
    private article_Adapter adapter = null;

    private ListView view_list = null;
    private view_Comment_Adapter view_adapter = null;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_article_layout);

        // 모아보기 listview 셋팅
        setCollect();
    }

    // 모아보기 listview 셋팅
    private void setCollect() {
        // 메뉴
        list = (ListView) findViewById(R.id.view_listview);

        // 어뎁터 생성민 등록
        adapter = new article_Adapter(this);
        list.setAdapter(adapter);

        // 여기서 db데이터 넣기
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"허성문", "대구 수성구 범어동", "2017.05.08 19:12","250","511","놀러와라");
    }

    // 댓글 listview 셋팅
    private void setComment(int image, String name, String comment) {

        // 실제 데이터 삽입
        view_adapter.addItem(getResources().getDrawable(image, null), name, comment);
        // 리스트뷰 펼처보기(한화면에)
        setListViewHeightBasedOnChildren(view_list);
    }

    // 리스트뷰 펼처보기(한화면에)
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
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

            // textView 처리
            holder.name.setText(mData.name);
            holder.location.setText(mData.location);
            holder.time.setText(mData.time);
            holder.recom_num.setText(mData.recom_num);
            holder.views_num.setText(mData.views_num);
            holder.contents.setText(mData.contents);

            // 댓글
            view_list = (ListView) convertView.findViewById(R.id.view_comment_list);
            // 어뎁터 생성민 등록
            view_adapter = new view_Comment_Adapter(ViewArticleActivity.this);
            view_list.setAdapter(view_adapter);
            // 댓글 셋팅(db받아서)
            setComment(R.drawable.basepicture, "김창석", "값싸다");
            setComment(R.drawable.basepicture, "김창석", "값싸다");
            setComment(R.drawable.basepicture, "김창석", "값싸다");
            setComment(R.drawable.basepicture, "김창석", "값싸다");

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


