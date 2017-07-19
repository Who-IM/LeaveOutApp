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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

/**
 * Created by bu456 on 2017-06-09.
 */

public class CommentActivity extends AppCompatActivity {
    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance();
    private ExecutorService service = Executors.newCachedThreadPool();      // 스레드 풀

    // 리사이클 뷰
    RecyclerView lecyclerView = null;
    ArrayList<Button> comment_item_btn2 = null;

    // 댓글 카운터
    TextView comment_count = null;

    // 글쓰는 공간 셋팅
    ImageView comment_edit_image = null;
    EditText comment_edit_text = null;

    // 실제 데이터
    List<comment_data> comment_list = null;
    CommentAdapter adapter = null;

    // db관련
    UserInfo user = UserInfo.getInstance();
    int content_num;
    int comm_num;

    // keyborad
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_recomment_layout);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //키보드 숨기기위해 인풋메니저 등록

        // 댓글 카운터
        comment_count = (TextView) findViewById(R.id.comment_count);

        // reciycleView(답글)
        lecyclerView = (RecyclerView) findViewById(R.id.comment_item_list);
        comment_item_btn2 = new ArrayList<Button>();

        // 자기 사진 및 edittext
        comment_edit_image = (ImageView) findViewById(R.id.comment_edit_image);
        comment_edit_text = (EditText) findViewById(R.id.comment_edit_text);

        // db에서 자기사진 추가
        comment_edit_image.setImageBitmap(user.getProfile());
        comment_list = new ArrayList<comment_data>();

        lecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        lecyclerView.setItemAnimator(new DefaultItemAnimator());

        // 초기 데이터 셋팅
        Intent tagintent = getIntent();
        if(tagintent != null) {
            content_num = tagintent.getIntExtra("content_num", 0);
            comm_num = tagintent.getIntExtra("comm_num", 0);

            recomment_select(content_num, comm_num);
        }

        // 글입력 했을 시
        comment_edit_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    // db추가
                    Insert_recomm_data(comment_edit_text.getText().toString());

                    // recierview 추가
                    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = sdfNow.format(new Date(System.currentTimeMillis()));

                    comment_data album = new comment_data();
                    album.setName(user.getName());
                    album.setContents(comment_edit_text.getText().toString());
                    album.setTime(time);
                    album.setImage(user.getProfile());
                    comment_list.add(album);

                    adapter.notifyDataSetChanged();
                    lecyclerView.setAdapter(adapter);

                    // 댓글 수 및 내용 초기화
                    comment_edit_text.setText("");
                    comment_count.setText(comment_list.size() + "");

                    // 키보드 숨기기
                    hideKeyboard();

                    return true;
                }
                return false;
            }
        });
    }

    // 댓글 및 답글에 공용사용
    public class comment_data {
        private Bitmap image;
        private String name;
        private String contents;
        private String time;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getContents() {
            return contents;
        }
        public void setContents(String contents) {
            this.contents = contents;
        }

        public Bitmap getImage() {
            return image;
        }
        public void setImage(Bitmap image) {
            this.image = image;
        }

        public String getTime() {
            return time;
        }
        public void setTime(String time) {
            this.time = time;
        }
    }

    // 여기부터 댓글(RecycleView)
    public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

        private List<comment_data> comment_list_data;
        View view = null;

        public CommentAdapter(List<comment_data> items){
            this.comment_list_data = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.public_recomment_item, viewGroup, false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            comment_data item = comment_list_data.get(position);
            viewHolder.name.setText(item.getName());
            viewHolder.contenst.setText(item.getContents());
            viewHolder.img.setImageBitmap(item.getImage());
            viewHolder.time.setText(item.getTime());

            viewHolder.itemView.setTag(item);

        }

        @Override
        public int getItemCount() {
            return comment_list_data.size();      //배열 길이 만큼 + footer
        }

        /**
         * 뷰 재활용을 위한 viewHolder
         */
        public class ViewHolder extends RecyclerView.ViewHolder{

            public ImageView img;
            public TextView name;
            public TextView contenst;
            public TextView time;

            public ViewHolder(View itemView){
                super(itemView);

                img = (ImageView) itemView.findViewById(R.id.comment_item_image);
                name = (TextView) itemView.findViewById(R.id.comment_item_name);
                contenst = (TextView) itemView.findViewById(R.id.comment_item_text);
                time = (TextView) itemView.findViewById(R.id.comment_item_time);

            }
        }
    }
    /// ----------------- 여기까지 -----------------------

    // 초기 데이터 셋팅
    private void recomment_select(final int content_num, final int comm_num) {

        final String sql = "select name, recomm_content, reg_time, user.profile from recomment " +
                           "inner join user on recomment.user_num = user.user_num " +
                           "where content_num = ? AND comm_num = ?;";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(content_num);
                mDataQueryGroup.addInt(comm_num);
                JSONObject request =  SQLDataService.getDynamicSQLJSONData(sql,mDataQueryGroup,-1,"select");
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
                JSONArray jspn = responseData.get(0).getJSONArray("result");
                for(int i =0; i < jspn.length(); i++) {
                    JSONObject j = jspn.getJSONObject(i);
                    comment_data album = new comment_data();
                    album.setName(j.getString("name"));
                    album.setContents(j.getString("recomm_content"));

                    // 시간
                    String reg_time = j.getString("reg_time");
                    reg_time = reg_time.substring(0,reg_time.length()-2);
                    album.setTime(reg_time);

                    // 이미지
                    Bitmap bit = setProfile(j);
                    album.setImage(bit);

                    // listview에 add
                    comment_list.add(album);
                }

                // adapter 셋팅
                adapter = new CommentAdapter(comment_list);
                lecyclerView.setAdapter(adapter);

                // 댓글에 댓글수 count
                comment_count.setText(comment_list.size() + "");
            }
        };
        LoadingSQLDialog.SQLSendStart(this, loadingSQLListener, ProgressDialog.STYLE_SPINNER,null);
    }

    // sql insert
    private void Insert_recomm_data(final String recomm_content) {
        final String sql = "insert into recomment(content_num, comm_num, user_num, recomm_content, reg_time) values(?, ?, ?, ?, now());";     // 유저 추가 sql

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(content_num);
                mDataQueryGroup.addInt(comm_num);
                mDataQueryGroup.addInt(user.getUserNum());
                mDataQueryGroup.addString(recomm_content);
                return SQLDataService.getDynamicSQLJSONData(sql, mDataQueryGroup, 0, "update");     // update SQL 제이슨
            }

            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                // 성공할시 toast 출력
                if(responseData != null) {
                    Toast.makeText(CommentActivity.this,"등록 되었습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        };
        LoadingSQLDialog.SQLSendStart(this, loadingSQLListener, ProgressDialog.STYLE_SPINNER, null);      // 로딩 다이얼로그 및 sql 전송
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

    // 키보드 숨기기
    private void hideKeyboard(){
        imm.hideSoftInputFromWindow(comment_edit_text.getWindowToken(), 0);
    }
}
