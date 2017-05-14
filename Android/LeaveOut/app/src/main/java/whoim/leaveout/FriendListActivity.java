package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendListActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView; // 확장 리스트 뷰
    List<String> listDataHeader;    // 리스트 뷰(하위항목 )
    HashMap<String, List<String>> listDataChild;
    String[] friends_list_title = {"ㄱ","ㄴ","ㄷ","ㄹ","ㅁ","ㅂ","ㅅ","ㅇ","ㅈ","ㅊ","ㅋ","ㅌ","ㅍ","ㅎ"};
    ArrayList<List> dataControl; // child에 데이터 셋팅

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list_layout);

        // 확장 listview 생성
        expListView = (ExpandableListView) findViewById(R.id.friend_list);
        prepareListData(); // 확장 listview에 데이터 셋팅

        // 어뎁터 생성(header와 child)
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // 어뎁터 등록
        expListView.setAdapter(listAdapter);

        // 자식항목(친구 이름) 클릭시 이벤트 처리 (임시)
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(),listDataHeader.get(groupPosition)+ " : "
                                + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition)
                        , Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    // 확장 리스트뷰 데이터 설정
    private void prepareListData() {
        dataControl = new ArrayList<>();
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // 여기부터 데이터 삽입인데 데이터베이스 추가시 수정
        // 작은 목록들 추가
        List<String> friend_list = new ArrayList<String>();
        friend_list.add("미정");

        // 큰 목록
        for(int i = 0; i < friends_list_title.length; i++) {
            listDataHeader.add(friends_list_title[i]);
            setListData(i,friend_list);
        }
        //-------------------------------------------------------
    }

    // child data 셋팅
    private void setListData(int index, List data) {
        dataControl.add(index, data);
        listDataChild.put(listDataHeader.get(index), dataControl.get(index));
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
            Typeface ty = Typeface.createFromAsset(getAssets(), "RixToyGray.ttf");
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

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}