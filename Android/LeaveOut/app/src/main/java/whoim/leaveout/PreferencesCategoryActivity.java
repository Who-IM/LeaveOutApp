package whoim.leaveout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

//카테고리
public class PreferencesCategoryActivity extends AppCompatActivity
{
    private ListView category_lv = null;
    private Preferences_Adapter adapter = null;
    String inputValue = null;
    Button plus_button = null;
    boolean flag = true;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_category_layout);
        category_lv = (ListView)findViewById(R.id.category_listview);
        plus_button = (Button)findViewById(R.id.category_plus_button);
        adapter = new Preferences_Adapter(PreferencesCategoryActivity.this);

        plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText etEdit = new EditText(PreferencesCategoryActivity.this);
                AlertDialog.Builder dialog = new AlertDialog.Builder(PreferencesCategoryActivity.this);
                dialog.setTitle("입력");
                dialog.setView(etEdit);

                // OK 버튼 이벤트
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        inputValue = etEdit.getText().toString();
                        Toast.makeText(PreferencesCategoryActivity.this, inputValue, Toast.LENGTH_SHORT).show();

                        setItem(inputValue);
                        category_lv.setAdapter(adapter);
                    }
                });
                // Cancel 버튼 이벤트
                dialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
    }

    public void setItem(String text) {
        adapter.addItem(text);
    }

    private class Preferences_ViewHolder
    {
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
        public View getView(int position, View convertView, ViewGroup parent) {
            Preferences_ViewHolder holder;
            if (convertView == null) {
                holder = new Preferences_ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.preferences_category, null);

                holder.name = (TextView) convertView.findViewById(R.id.category_plus);
                convertView.setTag(holder);
            }else{
                holder = (Preferences_ViewHolder) convertView.getTag();
            }

            Preferences_ListData mData = mListData.get(position);

            // textView 처리
            holder.name.setText(mData.name);

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
