package whoim.leaveout;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import whoim.leaveout.Services.FomatService;
import whoim.leaveout.User.UserInfo;

//체크 삭제
public class PreferencesCheckViewActivity extends AppCompatActivity {
    private ListView check_delete_lv = null;
    private Preferences_Adapter adapter = null;
    private Button delete_button = null;
    private ArrayList<ImageButton> btn = null;
    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance(); // sql에 필요한 데이터 그룹
    private int chk_n;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_check_view_layout);

        check_delete_lv = (ListView) findViewById(R.id.check_view_listview);
        delete_button = (Button) findViewById(R.id.check_delete);
        btn = new ArrayList<ImageButton>();
        adapter = new Preferences_Adapter(this);

        // listview 아이템 셋팅
        checkViewSQLData();


        // 삭제 버튼 눌렀을시 나타남 or 지워짐(채크 아이콘)
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = adapter.getCount();
                if (count == 0)     //체크에 아무 내용이 없을시 실행
                {
                    Toast.makeText(PreferencesCheckViewActivity.this, "체크에 아무것도 없음", Toast.LENGTH_SHORT).show();
                    return;
                }
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

    // 리스트뷰 홀더
    private class Preferences_ViewHolder {
        public TextView name;
        public ImageView check_image;
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
        public void addItem(String name, int number, boolean check_flag) {
            Preferences_ListData addInfo = null;
            addInfo = new Preferences_ListData();
            addInfo.name = name;
            addInfo.number = number;
            addInfo.check_flag = check_flag;

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
                holder.check_image = (ImageView) convertView.findViewById(R.id.check_image);

                convertView.setTag(holder);

            } else {
                holder = (Preferences_ViewHolder) convertView.getTag();
            }

            Preferences_ListData mData = mListData.get(position);

            // textView 처리
            holder.name.setText(mData.name);

            if(mData.check_flag) {
                holder.check_image.setImageResource(R.drawable.checkimg);
            }
            else {
                holder.check_image.setImageResource(R.drawable.write_image);
            }

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

                    checkDeleteSQLData(mListData.get(pos).number);

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
        public int number;
        public boolean check_flag;
    }

    // 뒤로가기
    public void Back(View v) {
        finish();
    }

    private void checkViewSQLData() {

        final String sql = "select * " +
                "from checks " +
                "where (user_num = ?);";

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
                Location location = new Location("checks");
                for(int i =0; i < jspn.length(); i++) {
                    JSONObject j = jspn.getJSONObject(i);
                    double x = j.getDouble("chk_x");
                    double y = j.getDouble("chk_y");
                    chk_n = j.getInt("check_num");
                    location.setLatitude(x);
                    location.setLongitude(y);
                    boolean flag;
                    if(!j.getString("check_image").equals("null")) {
                        flag = false;
                    } else {
                        flag = true;
                    }

                    adapter.addItem(FomatService.getCurrentAddress(getApplicationContext(),location), chk_n, flag);
                }
                check_delete_lv.setAdapter(adapter);
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener, ProgressDialog.STYLE_SPINNER,null);
    }

    private void checkDeleteSQLData(final int number) {

        final String sql = "delete from checks where check_num = ? and user_num = ?;";

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


    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

}
