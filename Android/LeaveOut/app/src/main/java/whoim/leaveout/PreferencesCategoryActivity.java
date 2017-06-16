package whoim.leaveout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import whoim.leaveout.Loading.LoadingSQLDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.User.UserInfo;

//카테고리
public class PreferencesCategoryActivity extends AppCompatActivity {
    private ListView check_lv = null;
    private Preferences_Adapter adapter = null;
    String inputValue = null;
    Button plus_button = null;
    Button delete_all_button = null;    //삭제 버튼
    ArrayList<ImageButton> X_button = null;
    ArrayList<ImageButton> delete_button = null;
    boolean delete_flag = true;

    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance(); // sql에 필요한 데이터 그룹

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.preferences_category_layout);
        check_lv = (ListView) findViewById(R.id.category_listview);
        plus_button = (Button) findViewById(R.id.category_plus_button);
        delete_all_button = (Button) findViewById(R.id.category_delete_button);
        X_button = new ArrayList<>();
        delete_button = new ArrayList<ImageButton>();
        adapter = new Preferences_Adapter(PreferencesCategoryActivity.this);

        selectCategorySQLData();

        plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if(adapter.getCount() >= 5) {
                    Toast.makeText(PreferencesCategoryActivity.this, "카테고리는 5개 이상 등록할수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final EditText etEdit = new EditText(PreferencesCategoryActivity.this);
                AlertDialog.Builder dialog = new AlertDialog.Builder(PreferencesCategoryActivity.this);
                dialog.setTitle("카테고리 추가");
                dialog.setView(etEdit);

                //키보드 바로 띄우기//키보드 띄우기
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                // OK 버튼 이벤트
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //키보드 내리기
                        imm.hideSoftInputFromWindow(etEdit.getWindowToken(), 0);
                        inputValue = etEdit.getText().toString();
                        if(inputValue.length() <= 10) {
                            if (inputValue.equals("")) {    //다이얼로그에 아무것도 입력하지 않았을 경우
                                Toast.makeText(PreferencesCategoryActivity.this, "아무것도 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //Toast.makeText(PreferencesCategoryActivity.this, inputValue, Toast.LENGTH_SHORT).show();

                            insertCategorySQLData(inputValue);

                            select_seq_CategorySQLData();
                        }
                        else {
                            Toast.makeText(PreferencesCategoryActivity.this, "10자 이하로 입력하세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                // Cancel 버튼 이벤트
                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //키보드 내리기
                        imm.hideSoftInputFromWindow(etEdit.getWindowToken(), 0);
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
                else if (delete_flag) {
                    for (int i = 0; i < count; i++) {
                        delete_button.get(i).setVisibility(View.VISIBLE);
                    }
                    delete_flag = false;
                } else {
                    for (int i = 0; i < count; i++) {
                        delete_button.get(i).setVisibility(View.INVISIBLE);
                    }
                    delete_flag = true;
                }

            }
        });

        check_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), CategorySelectActivity.class);
                intent.putExtra("cate_seq", adapter.getseq(position));
                startActivity(intent);
            }
        });
    }

    public void setItem(String text, int cate_number) {
        adapter.addItem(text, cate_number);
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

        public int getseq(int position) { return mListData.get(position).cate_number; }

        @Override
        public long getItemId(int position) {
            return position;
        }

        // 생성자로 값을 받아 셋팅
        public void addItem(String name, int cate_number) {
            Preferences_ListData addInfo = null;
            addInfo = new Preferences_ListData();
            addInfo.name = name;
            addInfo.cate_number = cate_number;
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
            if(X_button.size() == position) {
                X_button.add(position, (ImageButton) convertView.findViewById(R.id.category_delete));
            }
            else {
                X_button.set(position, (ImageButton) convertView.findViewById(R.id.category_delete));
            }
            X_button.get(position).setTag(position);
            X_button.get(position).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = adapter.getCount();
                    int pos = (int) v.getTag();

                    if (count > 0) {
                        deleteCategorySQLData(mListData.get(pos).cate_number);

                        // 아이템 삭제
                        mListData.remove(pos);

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
        public int cate_number;
    }
    private void insertCategorySQLData(final String text) {
        final String sql = "insert into category(user_num, cate_text) values(?, ?);";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {

            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(UserInfo.getInstance().getUserNum());
                mDataQueryGroup.addString(text);
                return SQLDataService.getDynamicSQLJSONData(sql,mDataQueryGroup,0,"update");
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener, ProgressDialog.STYLE_SPINNER,null);
    }

    private void selectCategorySQLData() {

        final String sql = "select * " +
                "from category " +
                "where (user_num = ?)";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(UserInfo.getInstance().getUserNum());
                return SQLDataService.getDynamicSQLJSONData(sql,mDataQueryGroup,-1,"select");
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                JSONArray jspn = responseData.get(0).getJSONArray("result");
                for (int i = 0; i < jspn.length(); i++) {
                    JSONObject j = jspn.getJSONObject(i);
                    int cate_number = j.getInt("cate_seq");
                    String text = j.getString("cate_text");
                    setItem(text, cate_number);
                }
                check_lv.setAdapter(adapter);
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener, ProgressDialog.STYLE_SPINNER,null);
    }


    private void deleteCategorySQLData(final int number) {

        final String sql = "delete from category where cate_seq = ? and user_num = ?;";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {

            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(number);
                mDataQueryGroup.addInt(UserInfo.getInstance().getUserNum());
                return SQLDataService.getDynamicSQLJSONData(sql,mDataQueryGroup,0,"update");
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener, ProgressDialog.STYLE_SPINNER,null);
    }


    private void select_seq_CategorySQLData() {

        final String sql = "select max(cate_seq) as cate_seq from category where user_num = ?;";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(UserInfo.getInstance().getUserNum());
                return SQLDataService.getDynamicSQLJSONData(sql,mDataQueryGroup,-1,"select");
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                JSONArray jspn = responseData.get(0).getJSONArray("result");
                setItem(inputValue, jspn.getJSONObject(0).getInt("cate_seq"));
                check_lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener, ProgressDialog.STYLE_SPINNER,null);
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
