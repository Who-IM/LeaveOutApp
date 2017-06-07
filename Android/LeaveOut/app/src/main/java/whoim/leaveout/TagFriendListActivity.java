package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

/**
 * Created by bu456 on 2017-06-05.
 */

public class TagFriendListActivity extends AppCompatActivity {

    // tag
    private ListView list = null;
    private tag_Adapter adapter = null;
    private ArrayList<ImageView> tagbtn = null;
    private ArrayList<String> tagdata = null;

    // 검색
    LinearLayout tag_friend_search_layout;
    private ListView tag_friend_searchList;
    ArrayAdapter<String> tag_friend_adapter_search;
    EditText tag_friend_inputSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_friend_list_layout);

        tagdata = new ArrayList<String>();

        // 검색 셋팅
        setSerach();
        tag_friend_search_layout.setVisibility(View.GONE);

        setTag();
    }

    //
    private void setTag() {
        // tag listview
        list = (ListView) findViewById(R.id. tag_friend_list);

        // tag button
        tagbtn = new ArrayList<ImageView>();

        // adapter 셋팅
        adapter = new tag_Adapter(this);
        list.setAdapter(adapter);

        // 여기서 db데이터 넣기
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"허성문");
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"김창석");
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"최수용");
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"김길동");
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"최예찬");
        adapter.addItem(getResources().getDrawable(R.drawable.basepicture, null),"한승주");

        // 아이템 클릭시
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(tagbtn.get(position).getVisibility() == View.INVISIBLE) {
                    tagbtn.get(position).setVisibility(View.VISIBLE);
                }
                else {
                    tagbtn.get(position).setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    // 검색관련 셋팅
    private void setSerach() {
        // 검색 관련 인스턴스
        tag_friend_searchList = (ListView) findViewById(R.id.tag_friend_search_list);
        tag_friend_inputSearch = (EditText) findViewById(R.id.tag_friend_search);
        tag_friend_search_layout = (LinearLayout) findViewById(R.id.tag_friend_search_layout);
        String products[] = {"홍길동", "홍길", "길동", "허성문", "성문", "김창석", "창석", "미정" };

        // 검색 리스트 뷰
        tag_friend_adapter_search = new ArrayAdapter<String>(this, R.layout.main_search_item, R.id.product_name, products);

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

    // ------------ tag listview -------------
    private class tag_ViewHolder {
        public ImageView Image;
        public TextView name;
    }

    // 리스트뷰 어뎁터
    private class tag_Adapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<tag_ListData> mListData = new ArrayList<tag_ListData>();

        public tag_Adapter(Context mContext) {
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
        public void addItem(Drawable image, String name) {
            tag_ListData addInfo = null;
            addInfo = new tag_ListData();
            addInfo.Image = image;
            addInfo.name = name;

            mListData.add(addInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            tag_ViewHolder holder;

            if (convertView == null) {
                holder = new tag_ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.tag_friend_list_item, null);

                holder.Image = (ImageView) convertView.findViewById(R.id.tag_friend_list_image);
                holder.name = (TextView) convertView.findViewById(R.id.tag_friend_list_name);

                convertView.setTag(holder);
            }else{
                holder = (tag_ViewHolder) convertView.getTag();
            }

            final tag_ListData mData = mListData.get(position);

            // 이미지 처리
            if (mData.Image != null) {
                holder.Image.setVisibility(View.VISIBLE);
                holder.Image.setImageDrawable(mData.Image);
            }else{
                holder.Image.setVisibility(View.GONE);
            }

            // textView 처리
            holder.name.setText(mData.name);

            // tagbutton 처리
            if(tagbtn.size() == position) {
                tagbtn.add(position, (ImageView) convertView.findViewById(R.id.tag_friend_list_check));
                tagdata.add(position, holder.name.getText().toString());
            } else {
                tagbtn.set(position, (ImageView) convertView.findViewById(R.id.tag_friend_list_check));
                tagdata.set(position, holder.name.getText().toString());
            }

            return convertView;
        }
    }

    // 메뉴의 실제 데이터를 저장할 class
    class tag_ListData {
        public Drawable Image;
        public String name;
    }
    // -------------------------------------- End tag listview -----------------------

    // 완료 버튼
    public void tagCommit(View v) {
        if(tagbtn.size() != 0) {
            Intent tag = new Intent(getApplicationContext(), WritingActivity.class);
            String tagname = "";

            for (int i = 0; i < tagbtn.size(); i++) {
                if(tagbtn.get(i).getVisibility() == View.VISIBLE) {
                    tagname += tagdata.get(i) + " ";
                }
            }
            tag.putExtra("tag", tagname);
            startActivity(tag);
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
