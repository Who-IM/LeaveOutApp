package whoim.leaveout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import whoim.leaveout.Loading.LoadingSQLDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.User.UserInfo;

/**
 * Created by bu456 on 2017-06-16.
 */

public class CategorySelectActivity extends AppCompatActivity {
    ListView list = null;
    category_Select_DataAdapter adapter = null;
    ArrayList<ImageView> check_image = null;
    Button category_delete_button = null;

    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance();

    int seq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_select_layout);

        Intent tagintent = getIntent();
        seq = tagintent.getIntExtra("cate_seq", -1);

        check_image = new ArrayList<>();
        category_delete_button = (Button) findViewById(R.id.category_delete_button);

        list = (ListView) findViewById(R.id.catagory_select_list);
        adapter = new category_Select_DataAdapter(CategorySelectActivity.this);
        categoryDataSQLData();

        // 리스트뷰 아이템클릭시
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(check_image.get(position).getVisibility() == View.INVISIBLE) {
                    check_image.get(position).setVisibility(View.VISIBLE);
                } else {
                    check_image.get(position).setVisibility(View.INVISIBLE);
                }
            }
        });

        // 확인버튼 클릭시
        category_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < adapter.getCount(); i++) {
                    if(check_image.get(i).getVisibility() == View.VISIBLE) {
                        categoryInsertSQLData(seq, adapter.getItem(i).address_data, adapter.getItem(i).content_num);
                    }
                }
            }
        });
    }

    private class category_Select_Holder {
        public TextView address;
    }

    // 리스트뷰 어뎁터
    private class category_Select_DataAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<category_Select_ListData> mListData = new ArrayList<category_Select_ListData>();

        public category_Select_DataAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        public ArrayList getList() { return mListData; };

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public category_Select_ListData getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        // 생성자로 값을 받아 셋팅
        public void addItem(String address_data, int content_num) {
            category_Select_ListData addInfo = null;
            addInfo = new category_Select_ListData();
            addInfo.address_data = address_data;
            addInfo.content_num = content_num;

            mListData.add(addInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            category_Select_Holder holder;

            if (convertView == null) {
                holder = new category_Select_Holder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_select_item, null);

                holder.address = (TextView) convertView.findViewById(R.id.category_select_check_text);

                convertView.setTag(holder);
            }else{
                holder = (category_Select_Holder) convertView.getTag();
            }

            final category_Select_ListData mData = mListData.get(position);

            holder.address.setText(mData.address_data);

            if(check_image.size() == position) {
                check_image.add(position, (ImageView) convertView.findViewById(R.id.category_select_check_image));
            } else {
                check_image.set(position, (ImageView) convertView.findViewById(R.id.category_select_check_image));
            }

            return convertView;
        }
    }

    // 메뉴의 실제 데이터를 저장할 class
    class category_Select_ListData {
        public String address_data;
        public int content_num;
    }

    private void categoryDataSQLData() {
        final String sql = "select address, content_num from content where user_num = ?;";

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
                for(int i =0; i < jspn.length(); i++) {
                    JSONObject j = jspn.getJSONObject(i);
                    adapter.addItem(j.getString("address"), j.getInt("content_num"));
                }
                list.setAdapter(adapter);
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener, ProgressDialog.STYLE_SPINNER,null);
    }

    private void categoryInsertSQLData(final int seq, final String address, final int content_num) {
        final String sql = "insert into cate_data(cate_seq, cate_data_text, content_num) values(?, ?, ?);";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(seq);
                mDataQueryGroup.addString(address);
                mDataQueryGroup.addInt(content_num);
                return SQLDataService.getDynamicSQLJSONData(sql,mDataQueryGroup,0,"update");
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                Toast.makeText(CategorySelectActivity.this, "카테고리에 추가하셨습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener, ProgressDialog.STYLE_SPINNER,null);
    }
}
