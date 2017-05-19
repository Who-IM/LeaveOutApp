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
import android.widget.ListView;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

//체크 삭제
public class PreferencesCheckDeleteActivity extends AppCompatActivity {
    private ListView check_delete_lv = null;
    private Preferences_Adapter adapter = null;
    Button delete_button = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_check_delete_layout);

        check_delete_lv = (ListView) findViewById(R.id.check_delete_listview);
        delete_button = (Button) findViewById(R.id.check_delete_button);
        adapter = new Preferences_Adapter(this);
        check_delete_lv.setAdapter(adapter);
        setItem("1");
        setItem("2");
        setItem("3");
        setItem("4");
        setItem("5");
    }

    public void setItem(String text) {

        adapter.addItem(text);
    }

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
                convertView = inflater.inflate(R.layout.preferences_check_delete, null);

                holder.name = (TextView) convertView.findViewById(R.id.check_text);
                convertView.setTag(holder);
            } else {
                holder = (Preferences_ViewHolder) convertView.getTag();
            }


            Preferences_ListData mData = mListData.get(position);

            // textView 처리
            holder.name.setText(mData.name);

            Button btn = (Button) convertView.findViewById(R.id.check_delete_button);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count, checked;
                    count = adapter.getCount();

                    if (count > 0) {

                        // 아이템 삭제
                        mListData.remove(position);

                        // listview 선택 초기화.
                        check_delete_lv.clearChoices();

                        // listview 갱신.
                        adapter.notifyDataSetChanged();

                    }
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
