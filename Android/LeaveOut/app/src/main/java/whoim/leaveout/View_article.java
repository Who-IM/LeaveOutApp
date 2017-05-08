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
import android.widget.ListView;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

// 글 보기
public class View_article extends AppCompatActivity
{
    private ListView list = null;
    private article_Adapter adapter = null;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_article_layout);

        // 메뉴
        list = (ListView) findViewById(R.id.collect_listview);

        // 어뎁터 생성민 등록
        adapter = new article_Adapter(this);
        list.setAdapter(adapter);

        // 실제 데이터 삽입
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),
                "허성문", "대구 수성구 범어동", "2017.05.08 19:12","250","511","놀러와라");
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),
                "김창석", "대구 복현동 뚝불", "2017.05.08 19:22","1230","2325","값싸다");
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


