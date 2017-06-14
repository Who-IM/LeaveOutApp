package whoim.leaveout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import whoim.leaveout.Loading.LoadingSQLDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.User.UserInfo;

public class FriendListActivity extends AppCompatActivity  {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView; // 확장 리스트 뷰
    List<String> listDataHeader;    // 리스트 뷰(하위항목 )
    HashMap<String, List<String>> listDataChild;
    String[] friends_list_title = {"ㄱ","ㄴ","ㄷ","ㄹ","ㅁ","ㅂ","ㅅ","ㅇ","ㅈ","ㅊ","ㅋ","ㅌ","ㅍ","ㅎ"};
    ArrayList<List> dataControl; // child에 데이터 셋팅

    // 검색
    LinearLayout friend_search_layout;
    private ListView friend_searchList;
    ArrayAdapter<String> friend_adapter_search;
    EditText friend_inputSearch;
    ArrayList<String> products;

    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance();          // sql에 필요한 데이터 그룹
    List<String> friend_list[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list_layout);

        // 인스턴스 셋팅
        setInstance();

        products = new ArrayList<>();
        friendlistSelectAndInsertSQL();

        // 검색 셋팅
        setSerach();
        friend_search_layout.setVisibility(View.GONE);
    }

    private void setInstance() {
        // 검색 관련 인스턴스
        friend_searchList = (ListView) findViewById(R.id.friend_search_list);
        friend_inputSearch = (EditText) findViewById(R.id.friend_search);
        friend_search_layout = (LinearLayout) findViewById(R.id.friend_search_layout);
    }

    // 초성
    public static String toKoChosung(String text)
    {
        char[] KO_INIT_S = { 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ',
                             'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };

        if (text == null) { return null; }

        // 한글자가 한글자와 그대로 대응됨.
        // 때문에 기존 텍스트를 토대로 작성된다.
        char[] rv = text.toCharArray();
        char ch;

        for (int i = 0 ; i < rv.length ; i++) {
            ch = rv[i];
            if (ch >= '가' && ch <= '힣') {
                rv[i] = KO_INIT_S[(ch - '가') / 588]; // 21 * 28
            }
        }

        return new String(rv);
    }

    // 검색관련 셋팅
    private void setSerach() {

        // editText 글자 쳤을 시
        friend_inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if(cs.toString().equals("")) {
                    friend_search_layout.setVisibility(View.GONE);
                    friend_searchList.setAdapter(null);
                } else {
                    friend_search_layout.setVisibility(View.VISIBLE);
                    friend_searchList.setAdapter(friend_adapter_search);
                    FriendListActivity.this.friend_adapter_search.getFilter().filter(cs);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) { }
            @Override
            public void afterTextChanged(Editable arg0) {     }
        });
    }

    // 확장 리스트뷰 데이터 설정
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // 여기부터 데이터 삽입인데 데이터베이스 추가시 수정
        // 작은 목록들 추가
        List<String> templist = new ArrayList();
        templist.add("없음");

        // 큰 목록
        for(int i = 0; i < friends_list_title.length; i++) {
            listDataHeader.add(friends_list_title[i]);
            if(friend_list[i].size() != 0) {
                listDataChild.put(listDataHeader.get(i), friend_list[i]);
            }
            else {
                listDataChild.put(listDataHeader.get(i), templist);
            }
        }
        //-------------------------------------------------------
    }

    // 확장 리스트뷰 어뎁터(친구목록에 사용)
    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<String> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, List<String>> _listDataChild;

        // 어뎁터 생성시 초기값
        public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                     HashMap<String, List<String>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.friend_list_item, null);
            }

            TextView txtListChild = (TextView) convertView.findViewById(R.id.friend_list_item);
            txtListChild.setText(childText);

            ImageView friend_list_item_image = (ImageView) convertView.findViewById(R.id.friend_list_item_image);
            //db에서 해당유저 이미지 사진넣기
            if(childText.equals("없음")) {
                friend_list_item_image.setVisibility(View.GONE);
            }
            else {
                friend_list_item_image.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        // 자식항목 숫자
        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        // 그룹의 숫자
        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        // 그룹의 ID
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        // header(ㄱ ~ ㅎ)
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            Typeface ty = Typeface.createFromAsset(getAssets(), "GodoM.ttf");
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.friend_list_group, null);
            }

            TextView lblListHeader = (TextView) convertView.findViewById(R.id.friend_list_header);
            lblListHeader.setTypeface(ty);
            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    // 뒤로가기
    public void Back(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void friendlistSelectAndInsertSQL() {
        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }
            @Override
            public JSONObject getSQLQuery() {
                String sql = "select friend.friend_num, user.name " +
                             "from friend inner join user " +
                             "on friend.friend_num = user.user_num " +
                             "where friend.user_num = ?;";

                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(UserInfo.getInstance().getUserNum());
                return SQLDataService.getDynamicSQLJSONData(sql,mDataQueryGroup,-1,"select");
            }
            @Override
            public JSONObject getUpLoad() {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                JSONArray jsonArray = responseData.get(0).getJSONArray("result");
                friend_list = new List[14];
                for (int i = 0; i < friend_list.length; i++) {
                    friend_list[i] = new ArrayList<>();
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject friend_Object = jsonArray.getJSONObject(i);
                    String name = friend_Object.getString("name");

                    // 검색 리스트 뷰
                    products.add(name);

                    String kochosung = toKoChosung(name.charAt(0) + ""); // 초성
                    switch (kochosung) {
                        case "ㄱ":  friend_list[0].add(name);  break;
                        case "ㄴ":  friend_list[1].add(name);  break;
                        case "ㄷ":  friend_list[2].add(name);  break;
                        case "ㄹ":  friend_list[3].add(name);  break;
                        case "ㅁ":  friend_list[4].add(name);  break;
                        case "ㅂ":  friend_list[5].add(name);  break;
                        case "ㅅ":  friend_list[6].add(name);  break;
                        case "ㅇ":  friend_list[7].add(name);  break;
                        case "ㅈ":  friend_list[8].add(name);  break;
                        case "ㅊ":  friend_list[9].add(name);  break;
                        case "ㅋ":  friend_list[10].add(name);  break;
                        case "ㅌ":  friend_list[11].add(name);  break;
                        case "ㅍ":  friend_list[12].add(name);  break;
                        case "ㅎ":  friend_list[13].add(name);  break;
                    }
                }
                // 검색리스트에 넣기
                friend_adapter_search = new ArrayAdapter<String>(FriendListActivity.this, R.layout.main_search_item, R.id.product_name, products);

                prepareListData(); // 확장 listview에 데이터 셋팅
                expListView = (ExpandableListView) findViewById(R.id.friend_list);  // 확장 listview 생성
                listAdapter = new ExpandableListAdapter(FriendListActivity.this, listDataHeader, listDataChild);  // 어뎁터 생성(header와 child)

                expListView.setAdapter(listAdapter);  // 어뎁터 등록
                expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // 자식항목(친구 이름) 클릭시 이벤트 처리 (임시)

                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + " : "
                                        + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition)
                                , Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
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