package whoim.leaveout;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bu456 on 2017-06-05.
 */

public class TagFriendListActivity extends AppCompatActivity {

    ExpandableListAdapter tag_listAdapter;
    ExpandableListView tag_expListView; // 확장 리스트 뷰
    List<String> tag_listDataHeader;    // 리스트 뷰(하위항목 )
    HashMap<String, List<String>> tag_listDataChild;
    String[] tag_friends_list_title = {"ㄱ","ㄴ","ㄷ","ㄹ","ㅁ","ㅂ","ㅅ","ㅇ","ㅈ","ㅊ","ㅋ","ㅌ","ㅍ","ㅎ"};
    ArrayList<List> tag_dataControl; // child에 데이터 셋팅

    // 검색
    LinearLayout tag_friend_search_layout;
    private ListView tag_friend_searchList;
    ArrayAdapter<String> tag_friend_adapter_search;
    EditText tag_friend_inputSearch;

    // tag
    ArrayList<ImageView> tagGroup = null;
    ArrayList<String> tagText = null;
    Button tag_commit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_friend_list_layout);

        // 인스턴스 셋팅
        setInstance();

        // 확장 listview 생성
        tag_expListView = (ExpandableListView) findViewById(R.id.tag_friend_list);
        prepareListData(); // 확장 listview에 데이터 셋팅

        // 어뎁터 생성(header와 child)
        tag_listAdapter = new ExpandableListAdapter(this, tag_listDataHeader, tag_listDataChild);

        // 어뎁터 등록
        tag_expListView.setAdapter(tag_listAdapter);

        // 자식항목(친구 이름) 클릭시 이벤트 처리 (임시)
        tag_expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                if(tagGroup.get(groupPosition + childPosition) == null) {
//                    tagGroup.set(groupPosition + childPosition, new image(v.getContext(), (ImageView)v.findViewById(R.id.tag_friend_list_check)));
//                }

                ImageView view_test = tagGroup.get(groupPosition + childPosition);
                if(view_test.getVisibility() == View.INVISIBLE) {
                    view_test.setVisibility(View.VISIBLE);
                }
                else {
                    view_test.setVisibility(View.INVISIBLE);
                }

                /*if(tagGroup.get(groupPosition+childPosition).getVisibility() == View.INVISIBLE) {
                    tagGroup.get(groupPosition + childPosition).setVisibility(View.VISIBLE);
                }
                else {
                    tagGroup.get(groupPosition + childPosition).setVisibility(View.INVISIBLE);
                }*/

                return false;
            }
        });

        // 검색 셋팅
        setSerach();
        tag_friend_search_layout.setVisibility(View.GONE);
    }

    private class image extends android.support.v7.widget.AppCompatImageView {
        ImageView image;

        public image(Context context, ImageView image) {
            super(context);
            this.image = image;
        }

        public int getVisible() {
            return image.getVisibility();
        }

        public void setVisible(int visible) {
            image.setVisibility(visible);
        }
    }

    private void setInstance() {

        // 검색 관련 인스턴스
        tag_friend_searchList = (ListView) findViewById(R.id.tag_friend_search_list);
        tag_friend_inputSearch = (EditText) findViewById(R.id.tag_friend_search);
        tag_friend_search_layout = (LinearLayout) findViewById(R.id.tag_friend_search_layout);
        String products[] = {"홍길동", "홍길", "길동", "허성문", "성문", "김창석", "창석", "미정" };

        // 검색 리스트 뷰
        tag_friend_adapter_search = new ArrayAdapter<String>(this, R.layout.main_search_item, R.id.product_name, products);

        // tag_commit
        tag_commit = (Button) findViewById(R.id.tag_commit);

        // tag
        tagGroup = new ArrayList<ImageView>();
        tagText = new ArrayList<String>();
    }

    // 검색관련 셋팅
    private void setSerach() {
        // editText 글자 쳤을 시
        tag_friend_inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if(cs.toString().equals("")) {
                    tag_friend_search_layout.setVisibility(View.GONE);
                    tag_friend_searchList.setAdapter(null);
                } else {
                    tag_friend_search_layout.setVisibility(View.VISIBLE);
                    tag_friend_searchList.setAdapter(tag_friend_adapter_search);
                    TagFriendListActivity.this.tag_friend_adapter_search.getFilter().filter(cs);
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
        tag_dataControl = new ArrayList<List>();
        tag_listDataHeader = new ArrayList<String>();
        tag_listDataChild = new HashMap<String, List<String>>();

        // 여기부터 데이터 삽입인데 데이터베이스 추가시 수정
        // 작은 목록들 추가
        // "ㄱ","ㄴ","ㄷ","ㄹ","ㅁ","ㅂ","ㅅ","ㅇ","ㅈ","ㅊ","ㅋ","ㅌ","ㅍ","ㅎ"
        List<String> friend_list1 = new ArrayList<String>();  // ㄱ
        friend_list1.add("김창석");
        friend_list1.add("김길동");
        tag_dataControl.add(friend_list1);

        List<String> friend_list2 = new ArrayList<String>();  // ㄴ
        friend_list2.add("노태영");
        tag_dataControl.add(friend_list2);

        List<String> friend_list3 = new ArrayList<String>();  // ㄷ
        friend_list3.add("도봉순");
        tag_dataControl.add(friend_list3);

        List<String> friend_list4 = new ArrayList<String>();  // ㄹ
        friend_list4.add("류상현");
        tag_dataControl.add(friend_list4);

        List<String> friend_list5 = new ArrayList<String>();  // ㅁ
        friend_list5.add("맘스터치");
        tag_dataControl.add(friend_list5);

        List<String> friend_list6 = new ArrayList<String>();  // ㅂ
        friend_list6.add("백승준");
        tag_dataControl.add(friend_list6);

        List<String> friend_list7 = new ArrayList<String>();  // ㅅ
        friend_list7.add("수진");
        tag_dataControl.add(friend_list7);

        List<String> friend_list8 = new ArrayList<String>();  // ㅇ
        friend_list8.add("영진");
        tag_dataControl.add(friend_list8);

        List<String> friend_list9 = new ArrayList<String>();  // ㅈ
        friend_list9.add("진");
        tag_dataControl.add(friend_list9);

        List<String> friend_list10 = new ArrayList<String>();  // ㅊ
        friend_list10.add("최수용");
        tag_dataControl.add(friend_list10);

        List<String> friend_list11 = new ArrayList<String>();  // ㅋ
        friend_list11.add("퀸");
        tag_dataControl.add(friend_list11);

        List<String> friend_list12 = new ArrayList<String>();  // ㅌ
        friend_list12.add("트린다미어");
        tag_dataControl.add(friend_list12);

        List<String> friend_list13 = new ArrayList<String>();  // ㅍ
        friend_list13.add("피오라");
        tag_dataControl.add(friend_list13);

        List<String> friend_list14 = new ArrayList<String>();  // ㅎ
        friend_list14.add("한승주");
        tag_dataControl.add(friend_list14);

        View test = getLayoutInflater().inflate(R.layout.tag_friend_list_item, null);

        // 큰 목록
        for(int i = 0; i < tag_friends_list_title.length; i++) {
            tag_listDataHeader.add(tag_friends_list_title[i]);
            tag_listDataChild.put(tag_listDataHeader.get(i), tag_dataControl.get(i));

            for(int j = 0; j < tag_dataControl.get(i).size(); j++) {
                List temp = tag_dataControl.get(i);
                tagGroup.add(new image(test.getContext(), (ImageView)test.findViewById(R.id.tag_friend_list_check)));
                tagText.add(temp.get(j).toString());
            }
        }

        tag_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = " ";
                for(int i = 0 ; i < tagGroup.size(); i++) {
                    image view_test = (image) tagGroup.get(i);
                    if(tagGroup.get(i) != null && ((image) tagGroup.get(i)).getVisible()==View.VISIBLE) {
                        tag += tagText.get(i) + " ";
                    }
                }
                Intent tagintent = new Intent(getApplicationContext(), WritingActivity.class);
                tagintent.putExtra("tag", tag);
                startActivity(tagintent);
            }
        });
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
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.tag_friend_list_item, null);
            }

            TextView txtListChild = (TextView) convertView.findViewById(R.id.tag_friend_list_item);
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
            Typeface ty = Typeface.createFromAsset(getAssets(), "GodoM.ttf");
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.tag_friend_list_group, null);
            }

            TextView lblListHeader = (TextView) convertView.findViewById(R.id.tag_friend_list_header);
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
        Intent intent = new Intent(getApplicationContext(), WritingActivity.class);
        startActivity(intent);
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
