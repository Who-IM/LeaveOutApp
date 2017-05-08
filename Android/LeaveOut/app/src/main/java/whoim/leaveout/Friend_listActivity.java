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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Friend_listActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list_layout);

        // 확장 listview 생성
        expListView = (ExpandableListView) findViewById(R.id.friend_list);
        prepareListData();

        // 어뎁터 생성
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
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // 큰 목록
        listDataHeader.add("ㄱ");
        listDataHeader.add("ㄴ");
        listDataHeader.add("ㄷ");

        // 작은 목록들 추가
        List<String> friend_list1 = new ArrayList<String>();
        friend_list1.add("미정");

        List<String> friend_list2 = new ArrayList<String>();
        friend_list2.add("미정");

        List<String> friend_list3 = new ArrayList<String>();
        friend_list3.add("미정");

        listDataChild.put(listDataHeader.get(0), friend_list1);
        listDataChild.put(listDataHeader.get(1), friend_list2);
        listDataChild.put(listDataHeader.get(2), friend_list3);
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

            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.friend_list_item);

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

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.friend_list_group, null);
            }

            TextView lblListHeader = (TextView) convertView.findViewById(R.id.friend_list_header);
            lblListHeader.setTypeface(null, Typeface.BOLD);
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
}