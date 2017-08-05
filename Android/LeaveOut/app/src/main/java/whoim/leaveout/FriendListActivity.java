package whoim.leaveout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import whoim.leaveout.Loading.LoadingSQLDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.Server.ImageDownLoad;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.User.UserInfo;

public class FriendListActivity extends AppCompatActivity  {

    // 리사이클 뷰
    RecyclerView lecyclerView = null;
    List<friend_data> friend_list = null;
    friendListAdapter adapter = null;

    // 검색
    LinearLayout friend_search_layout;
    private ListView friend_searchList;
    ArrayAdapter<String> friend_adapter_search;
    EditText friend_inputSearch;
    ArrayList<String> products;

    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance();          // sql에 필요한 데이터 그룹
    private ExecutorService service = Executors.newCachedThreadPool();      // 스레드 풀

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

        lecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        lecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setInstance() {
        // 검색 관련 인스턴스
        friend_searchList = (ListView) findViewById(R.id.friend_search_list);
        friend_inputSearch = (EditText) findViewById(R.id.friend_search);
        friend_search_layout = (LinearLayout) findViewById(R.id.friend_search_layout);

        // recyclerview
        lecyclerView = (RecyclerView) findViewById(R.id.friend_list);
        friend_list = new ArrayList<friend_data>();
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

    // 댓글 및 답글에 공용사용
    public class friend_data {
        private Bitmap image;
        private String name;
        private String email;
        private int friend_num;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public Bitmap getImage() {
            return image;
        }
        public void setImage(Bitmap image) { this.image = image; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public int getfriend_num() { return friend_num; }
        public void setFriend_num(int friend_num) { this.friend_num = friend_num; }
    }

    // 여기부터 댓글(RecycleView)
    public class friendListAdapter extends RecyclerView.Adapter<friendListAdapter.ViewHolder> {

        private List<friend_data> friend_list_data;
        View view = null;

        public friendListAdapter(List<friend_data> items){
            this.friend_list_data = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_list_item, viewGroup, false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            friend_data item = friend_list_data.get(position);
            viewHolder.name.setText(item.getName());
            viewHolder.img.setImageBitmap(item.getImage());

            viewHolder.itemView.setTag(item);

        }

        @Override
        public int getItemCount() {
            return friend_list_data.size();      //배열 길이 만큼 + footer
        }

        /**
         * 뷰 재활용을 위한 viewHolder
         */
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            public ImageView img;
            public TextView name;
            public RelativeLayout layout;

            public ViewHolder(View itemView){
                super(itemView);

                img = (ImageView) itemView.findViewById(R.id.friend_list_item_image);
                name = (TextView) itemView.findViewById(R.id.friend_list_item_text);
                layout = (RelativeLayout) itemView.findViewById(R.id.friend_list_item_layout);

                layout.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                final int position = getAdapterPosition();
                friend_data data = friend_list_data.get(position);

                Intent intent = new Intent(v.getContext() , FriendProfileActivity.class);
                intent.putExtra("user_num",data.getfriend_num());
                intent.putExtra("name",data.getName());
                intent.putExtra("email",data.getEmail());
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        }
    }
    /// ----------------- 여기까지 -----------------------


    private void friendlistSelectAndInsertSQL() {
        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }
            @Override
            public JSONObject getSQLQuery() {

                String sql = "select friend.friend_num, user.name, user.email, user.profile "+
                             "from friend inner join user "+
                             "on friend.friend_num = user.user_num "+
                             "where friend.user_num = ? "+
                             "AND friend.request = 0 "+
                             "order by user.name asc";

                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(UserInfo.getInstance().getUserNum());
                JSONObject request = SQLDataService.getDynamicSQLJSONData(sql,mDataQueryGroup,-1,"select");
                SQLDataService.putBundleValue(request,"download","context","files");
                SQLDataService.putBundleValue(request,"download","context2","profile");
                return request;
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                JSONArray jsonArray = responseData.get(0).getJSONArray("result");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject friend_Object = jsonArray.getJSONObject(i);
                    String name = friend_Object.getString("name");

                    friend_data album = new friend_data();
                    album.setName(name);
                    album.setEmail(friend_Object.getString("email"));
                    album.setFriend_num(friend_Object.getInt("friend_num"));

                    Bitmap bit = setProfile(friend_Object);
                    album.setImage(bit);
                    friend_list.add(album);

                    lecyclerView.setAdapter(adapter);

                    // 검색 리스트 뷰
                    products.add(name);

                }
                // adapter 셋팅
                adapter = new friendListAdapter(friend_list);
                lecyclerView.setAdapter(adapter);

                // 검색리스트에 넣기
                friend_adapter_search = new ArrayAdapter<String>(FriendListActivity.this, R.layout.main_search_item, R.id.product_name, products);
            }
        };

        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener, ProgressDialog.STYLE_SPINNER,null);
    }

    // 프로필 이미지 셋팅
    public Bitmap setProfile(final JSONObject data) throws JSONException {
        Bitmap bitmap = null;
        Callable<Bitmap> bitmapCallable = new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                Bitmap _bitmap = null;
                if(data.has("image2")) {      // 있는지 확인
                    JSONArray profileUri = data.getJSONArray("image2");
                    if (profileUri.length() != 0) {
                        _bitmap = ImageDownLoad.imageDownLoad(profileUri.getString(0));
                    }
                }
                else if(data.has("image")) {
                    JSONArray profileUri = data.getJSONArray("image");
                    if (profileUri.length() != 0) {
                        _bitmap = ImageDownLoad.imageDownLoad(profileUri.getString(0));
                    }
                }
                else {
                    _bitmap = ImageDownLoad.imageDownLoad(data.getString("profile"));
                }
                if (_bitmap == null) {
                    _bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.basepicture, null)).getBitmap();
                }
                return _bitmap;
            }
        };

        try {
            bitmap = service.submit(bitmapCallable).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // 뒤로가기
    public void Back(View v) {
        finish();
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}