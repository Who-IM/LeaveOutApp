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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

//카테고리
public class PreferencesCategoryActivity extends AppCompatActivity {
    private ListView check_lv = null;
    private Preferences_Adapter adapter = null;
    String inputValue = null;
    Button plus_button = null;
    Button delete_all_button = null;    //삭제 버튼
    ImageButton X_button = null;
    ArrayList<ImageButton> delete_button = null;
    boolean delete_flag = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_category_layout);
        check_lv = (ListView) findViewById(R.id.category_listview);
        plus_button = (Button) findViewById(R.id.category_plus_button);
        delete_all_button = (Button) findViewById(R.id.category_delete_button);

        delete_button = new ArrayList<ImageButton>();
        adapter = new Preferences_Adapter(PreferencesCategoryActivity.this);

        plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText etEdit = new EditText(PreferencesCategoryActivity.this);
                AlertDialog.Builder dialog = new AlertDialog.Builder(PreferencesCategoryActivity.this);
                dialog.setTitle("카테고리 추가");
                dialog.setView(etEdit);

                // OK 버튼 이벤트
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        inputValue = etEdit.getText().toString();
                        if (inputValue.equals("")) {    //다이얼로그에 아무것도 입력하지 않았을 경우
                            Toast.makeText(PreferencesCategoryActivity.this, "아무것도 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(PreferencesCategoryActivity.this, inputValue, Toast.LENGTH_SHORT).show();

                        setItem(inputValue);
                        check_lv.setAdapter(adapter);
                    }
                });
                // Cancel 버튼 이벤트
                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });

        delete_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = adapter.getCount();
                if (count == 0)     //체크에 아무 내용이 없을시 실행
                {
                    Toast.makeText(PreferencesCategoryActivity.this, "카테고리 아무것도 없음", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (delete_flag) {
                    for (int i = 0; i <= count; i++) {
                        delete_button.get(i).setVisibility(View.VISIBLE);
                    }
                    delete_flag = false;
                } else {
                    for (int i = 0; i <= count; i++) {
                        delete_button.get(i).setVisibility(View.INVISIBLE);
                    }
                    delete_flag = true;
                }

            }
        });
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            Preferences_ViewHolder holder;
            if (convertView == null) {
                holder = new Preferences_ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.preferences_category, null);

                holder.name = (TextView) convertView.findViewById(R.id.category_plus);
                convertView.setTag(holder);

                delete_button.add(position, (ImageButton) convertView.findViewById(R.id.category_delete));

            } else {
                holder = (Preferences_ViewHolder) convertView.getTag();
            }

            Preferences_ListData mData = mListData.get(position);

            // textView 처리
            holder.name.setText(mData.name);

            //X버튼 눌렀을 경우 체크 삭제
            X_button = (ImageButton) convertView.findViewById(R.id.category_delete);
            X_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count;
                    count = adapter.getCount();

                    if (count > 0) {

                        // 아이템 삭제
                        mListData.remove(position);

                        // listview 선택 초기화.
                        check_lv.clearChoices();

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
