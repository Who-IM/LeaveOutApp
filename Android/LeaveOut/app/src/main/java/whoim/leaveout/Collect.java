package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

//모아보기
public class Collect extends AppCompatActivity
{
    ArrayList<collect_list_view_data> ar_view_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_layout);

        //폰트
        Typeface typeface = Typeface.createFromAsset(getAssets(), "RixToyGray.ttf");
        TextView title = (TextView) findViewById(R.id.collect_title);   //제목
        title.setTypeface(typeface);

        // collect_list_view_data 클래스 형태의 데이터 준비
        ar_view_data = new ArrayList<collect_list_view_data>();
        collect_list_view_data list_view;
        list_view = new collect_list_view_data(R.drawable.basepicture, "허성문","대구 북구 복현로 영진전문대","2017.05.05 19:03", "5", "57", "미용실 여기 싸고 좋다.");
        ar_view_data.add(list_view);

        collect_adapter adapter = new collect_adapter(this, R.layout.collect, ar_view_data);

        //리스트뷰 추가
        ListView list;
        list = (ListView)findViewById(R.id.collect_listview);
        list.setAdapter(adapter);   //어뎁터 샛팅
    }
    
    // 리스트뷰에 출력할 항목 클래스
    class collect_list_view_data {

        int Icon;
        String Name;
        String location;
        String time;
        String like;
        String views;
        String contents;
        
        collect_list_view_data(int aIcon, String aName, String aLocation, String aTime, String aLike, String aViews, String aContents) {
            Icon = aIcon;   //이미지
            Name = aName;   //이름
            location = aLocation;   //장소
            time = aTime;   //시간
            like = aLike;   //추천수
            views = aViews; //조회수
            contents = aContents;   //글내용
        }
    }

    // 어댑터 클래스
    class collect_adapter extends BaseAdapter {

        Context con;
        LayoutInflater inflacter;
        ArrayList<collect_list_view_data> arD;
        int layout;

        public collect_adapter(Context context, int alayout, ArrayList<collect_list_view_data> aarD) {
            con = context;
            inflacter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            arD = aarD;
            layout = alayout;
        }

        // 어댑터에 몇 개의 항목이 있는지 조사
        @Override
        public int getCount() {
            return arD.size();
        }

        // position 위치의 항목 Name 반환
        @Override
        public Object getItem(int position) {
            return arD.get(position).Name;
        }

        // position 위치의 항목 ID 반환
        @Override
        public long getItemId(int position) {
            return position;
        }

        // 각 항목의 뷰 생성 후 반환
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflacter.inflate(layout, parent, false);
            }
            // 사람 이미지
            ImageView img = (ImageView) convertView.findViewById(R.id.imageView);
            img.setImageResource(arD.get(position).Icon);

            // 이름
            TextView name = (TextView) convertView.findViewById(R.id.collect_name);
            name.setText(arD.get(position).Name);

            // 장소
            TextView loc = (TextView) convertView.findViewById(R.id.collect_location);
            loc.setText(arD.get(position).location);

            // 시간
            TextView time = (TextView) convertView.findViewById(R.id.collect_time);
            time.setText(arD.get(position).time);

            // 추천수
            TextView like = (TextView) convertView.findViewById(R.id.collect_recom_num);
            like.setText(arD.get(position).like);

            // 조회수
            TextView view = (TextView) convertView.findViewById(R.id.collect_views_num);
            view.setText(arD.get(position).views);

            // 글내용
            TextView con = (TextView) convertView.findViewById(R.id.collect_contents);
            con.setText(arD.get(position).contents);

            return convertView;
        }
    }

    // 뒤로가기
    public void Back(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}


