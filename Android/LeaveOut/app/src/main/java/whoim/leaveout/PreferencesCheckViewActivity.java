package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

//체크 삭제
public class PreferencesCheckViewActivity extends AppCompatActivity {
    private ListView check_delete_lv = null;
    private Preferences_Adapter adapter = null;
    private Button delete_button = null;
    private ArrayList<ImageButton> btn = null;
    private boolean btn_flag = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_check_view_layout);

        check_delete_lv = (ListView) findViewById(R.id.check_view_listview);
        delete_button = (Button) findViewById(R.id.check_delete);
        btn = new ArrayList<ImageButton>();
        adapter = new Preferences_Adapter(this);
        check_delete_lv.setAdapter(adapter);

        // listview 아이템 셋팅
        setItem("1");
        setItem("2");
        setItem("3");
        setItem("4");
        setItem("5");

        // 삭제 버튼 눌렀을시 나타남 or 지워짐(채크 아이콘)
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < btn.size(); i++) {
                    int pos = (int) btn.get(i).getTag();  // listview item position

                    // 버튼 숨김 및 나타남
                    if(btn.get(pos).getVisibility() == View.INVISIBLE) {
                        btn.get(pos).setVisibility(View.VISIBLE);
                    }
                    else {
                        btn.get(pos).setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    // 아이템 셋팅
    public void setItem(String text) {
        adapter.addItem(text);
    }

    // 리스트뷰 홀더
    private class Preferences_ViewHolder {
        public TextView name;
    }

    // 리스트뷰 어뎁터
    private class Preferences_Adapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<Preferences_ListData> mListData = new ArrayList<Preferences_ListData>();

        public Preferences_Adapter(Context mContext) {
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
        public void addItem(String name) {
            Preferences_ListData addInfo = null;
            addInfo = new Preferences_ListData();
            addInfo.name = name;

            mListData.add(addInfo);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            Preferences_ViewHolder holder;
            if (convertView == null) {
                holder = new Preferences_ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.preferences_check_view, null);

                holder.name = (TextView) convertView.findViewById(R.id.check_text);

                convertView.setTag(holder);

            } else {
                holder = (Preferences_ViewHolder) convertView.getTag();
            }

            Preferences_ListData mData = mListData.get(position);

            // textView 처리
            holder.name.setText(mData.name);

            // 버튼 중복생성 방지
            if(btn.size() == position) {
                btn.add(position, (ImageButton) convertView.findViewById(R.id.check_delete_button));  // 처음의 경우만 생성
            } else {
                btn.set(position, (ImageButton) convertView.findViewById(R.id.check_delete_button));  // 재활용할 경우
            }
            btn.get(position).setTag(position);
            btn.get(position).setImageResource(R.drawable.public_delete);

            // 삭제아이콘 누를시
            btn.get(position).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();

                    // 아이템 삭제
                    mListData.remove(pos);

                    // 버튼리스너 삭제
                    btn.remove(pos);

                    // listview 선택 초기화.
                    check_delete_lv.clearChoices();

                    // listview 갱신.
                    adapter.notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }

    // 메뉴의 실제 데이터를 저장할 class
    class Preferences_ListData {
        public String name;
    }

    // 뒤로가기
    public void Back(View v) {
        Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
        startActivity(intent);
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
