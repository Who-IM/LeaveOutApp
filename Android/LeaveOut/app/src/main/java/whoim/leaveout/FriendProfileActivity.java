package whoim.leaveout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import whoim.leaveout.Adapter.CommentAdapter;
import whoim.leaveout.Adapter.ContentAdapter;
import whoim.leaveout.Loading.LoadingDialogBin;
import whoim.leaveout.Server.ImageDownLoad;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.Server.WebControll;
import whoim.leaveout.User.UserInfo;

// 환경설정
public class FriendProfileActivity extends AppCompatActivity {

    // 툴바 제목
    TextView friendtextview;

    // 인텐트 데이터 번들
    Bundle mDataBundle;
    String mContentnum;
    Bitmap profilebitmap;

    // 친구 추가 버튼
    int friendnum;
    ImageButton mFriendAddButton;
    boolean friendaddcheck = false;


    // 프로필 이미지 변경
    private ImageButton mProfileSetImage;

    // list 게시글
    private ListView mContentlist = null;
    private ContentAdapter mContentAdapter;

    //갤러리
    ImageView profile_image = null;

    ArrayList<Integer> cate = null;

    UserInfo userInfo = UserInfo.getInstance();     // 유저 정보
    Bitmap userbitmap = userInfo.getProfile();

    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance(); // sql에 필요한 데이터 그룹
    JSONObject request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        mDataBundle = getIntent().getExtras();
        mContentnum = String.valueOf(mDataBundle.getInt("contentnum"));

        if(userbitmap == null)  // 프로필 사진이 없을 경우
            userbitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.basepicture)).getBitmap();      // 기본값

        cate = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#00FFFFFF"));   //제목 투명하게
        setSupportActionBar(toolbar);   //액션바와 같게 만들어줌
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.

        mProfileSetImage = (ImageButton) findViewById(R.id.profile_set_Image);

        friendViewSet();

        // 초기설정 (db필요)
        init(profilebitmap, mDataBundle.getString("name"), mDataBundle.getString("email"));

        // 모아보기 listview 셋팅
        setProfile();

        meContentData(0);
    }


    private void friendViewSet() {
        mProfileSetImage.setVisibility(View.GONE);      // 사진 변경 버튼
        findViewById(R.id.profile_google_map).setVisibility(View.GONE);     // 지도
        findViewById(R.id.profile_radiogroup).setVisibility(View.GONE);     // 라디오 버튼
        mFriendAddButton = (ImageButton) findViewById(R.id.profile_friend_plus);    // 친구 추가 버튼
        mFriendAddButton.setVisibility(View.VISIBLE);

        mFriendAddButton.setOnClickListener(new View.OnClickListener() {        // 친구 추가하기
            @Override
            public void onClick(View v) {

                final String FriendAddSQL = "insert into friend(user_num,friend_num) values(?,?)";
                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(userInfo.getUserNum());
                mDataQueryGroup.addInt(friendnum);

                new LoadingDialogBin(FriendProfileActivity.this) {

                    @Override
                    protected Void doInBackground(Void... params) {
                        JSONObject data = SQLDataService.getDynamicSQLJSONData(FriendAddSQL, mDataQueryGroup, 0, "update");
                        JSONObject responsedata = new WebControll().WebLoad(data);     // SQL 돌리기
                        Object[] objects = new Object[1];

                        try {
                            if (responsedata != null) {
                                if((responsedata.getString("result").equals("error"))) {        // 에러가 아닐경우
                                    objects[0] = false;
                                    publishProgress(objects);
                                    return null;
                                }
                                else {      // 데이터베이스에 친구 추가 된경우 친구 디바이스에 알림 보내기
                                    final OkHttpClient client = new OkHttpClient();
                                    RequestBody body = new FormBody.Builder()
                                            .add("requestdata","AddFriend")
                                            .add("user_num", String.valueOf(userInfo.getUserNum()))
                                            .addEncoded("friend_num", String.valueOf(friendnum))
                                            .build();

                                    //request
                                    final Request request = new Request.Builder()
                                            .url(WebControll.WEB_IP + "/FCMPush")
                                            .post(body)
                                            .build();

                                    client.newCall(request).enqueue(new Callback() {        // 서버로 보내기
                                        @Override
                                        public void onFailure(Call call, IOException e) { }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException { }
                                    });
                                    objects[0] = true;
                                    publishProgress(objects);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(Object... values) {
                        boolean check = (boolean) values[0];
                        if(!check) {        // 오류시
                            Toast.makeText(FriendProfileActivity.this,"잠시 후 다시 시도해 주십시오.",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(FriendProfileActivity.this,"친구 추가 요청을 했습니다..",Toast.LENGTH_SHORT).show();
                            mFriendAddButton.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                    }
                }.execute();
            }
        });
    }



    /*//옵션 버튼
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 메뉴버튼이 처음 눌러졌을 때 실행되는 콜백메서드
        // 메뉴버튼을 눌렀을 때 보여줄 menu 에 대해서 정의
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 옵션 메뉴가 화면에 보여지는 메서드

        if (menuCount == 0) //가까운 위치 누를시 버튼 비활성화 그외 다 활성화
        {
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(true);
            menu.getItem(2).setEnabled(true);
            menu.getItem(3).setEnabled(true);
        }

        else if(menuCount == 1) //최신글 누를시 버튼 비활성화 그외 다 활성화
        {
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(false);
            menu.getItem(2).setEnabled(true);
            menu.getItem(3).setEnabled(true);
        }

        else if(menuCount == 2) //조회수 누를시 버튼 비활성화 그외 다 활성화
        {
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(true);
            menu.getItem(2).setEnabled(false);
            menu.getItem(3).setEnabled(true);
        }

        else if(menuCount == 3) //추천수 누를시 버튼 비활성화 그외 다 활성화
        {
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(true);
            menu.getItem(2).setEnabled(true);
            menu.getItem(3).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 메뉴의 항목을 선택(클릭)했을 때 호출되는 콜백메서드
        int id = item.getItemId();

        switch (id) {
            case R.id.profile_menu_location:
                Toast.makeText(getApplicationContext(), "가까운 위치 순서대로", Toast.LENGTH_SHORT).show();
                menuCount = 0;
                return true;

            case R.id.profile_menu_time:
                Toast.makeText(getApplicationContext(), "최신글 순서대로", Toast.LENGTH_SHORT).show();
                menuCount = 1;
                return true;

            case R.id.profile_menu_view:
                Toast.makeText(getApplicationContext(), "조회수 순서대로", Toast.LENGTH_SHORT).show();
                menuCount = 2;
                return true;

            case R.id.profile_menu_recommended:
                Toast.makeText(getApplicationContext(), "추천수 순서대로", Toast.LENGTH_SHORT).show();
                menuCount = 3;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
    // 옵션 버튼 끝

    // 모아보기 listview 셋팅
    private void setProfile() {
        // 메뉴
        mContentlist = (ListView) findViewById(R.id.proflie_list);

        mContentAdapter = new ContentAdapter(this);
        mContentlist.setAdapter(mContentAdapter);
    }

    /* 초기설정 첫번째는 사진 : db에서
                두번째는 이름 : db에서
                세번째는 이메일 : db에서  */
    public void init(Bitmap image, String name, String email) {

        //  툴바 제목
        friendtextview = (TextView) findViewById(R.id.profile_title);

        // 프로필 사진
        profile_image = (ImageView) findViewById(R.id.profile_title_image);
        profile_image.setImageBitmap(image);

        // 프로필 이름
        TextView tx1 = (TextView) findViewById(R.id.profile_title_name);
        tx1.setText(name);

        // 이메일
        TextView tx2 = (TextView) findViewById(R.id.profile_title_email);
        tx2.setText(email);
    }

    // 뒤로가기
    public void Back(View v) {
        finish();
    }

    private void meContentData(final int tab_position) {


        final String sql = "select content_num, content.user_num AS user_num ,name, view_cnt, rec_cnt, reg_time,address,files,profile " +
                "from content inner join user " +
                "on content.user_num = user.user_num " +
                "where content_num in (" + mContentnum + ")";

        request = SQLDataService.getSQLJSONData(sql, -1, "select");

        new LoadingDialogBin(this) {

            @Override
            protected Void doInBackground(Void... params) {

               SQLDataService.putBundleValue(request, "download", "context", "files");
               SQLDataService.putBundleValue(request, "download", "context2", "profile");
               result.add(new WebControll().WebLoad(request));
               try {
                   if (result.get(0) != null && result.get(0).getJSONArray("result").length() != 0) {            // 게시글 가져오기
                       JSONArray resultData = result.get(0).getJSONArray("result");   // 결과값
                       for (int i = 0; i < resultData.length(); i++) {
                           CommentAdapter commentAdapter = null;                   // 댓글 어댑터
                           JSONObject contentdata = resultData.getJSONObject(i);   // 게시글 데이터

                           ArrayList<String> imagelist = new ArrayList();          // 이미지 넣을 데이터
                           JSONArray imageArray = resultData.getJSONObject(i).getJSONArray("image");       // 게시글에서 이미지 가져오기
                           for (int j = 0; j < imageArray.length(); j++) {
                               imagelist.add(imageArray.getString(j));             // 이미지 배열에 넣기
                           }

                           int contentnum = contentdata.getInt("content_num");     // 게시글 번호 가져오기
                           friendnum = contentdata.getInt("user_num");     // 게시글 번호 가져오기
                           String name = contentdata.getString("name");
                           String address = contentdata.getString("address");
                           String reg_time = contentdata.getString("reg_time");
                           reg_time = reg_time.substring(0,reg_time.length()-2);
                           String rec_cnt = contentdata.getString("rec_cnt");
                           String view_cnt = contentdata.getString("view_cnt");
                           String text = contentdata.getString("text");
                           if(i == 0) profilebitmap = setProfile(contentdata);     // 친구 프로필 사진 한번만 가져오기

                            // 댓글 부분
                           String commentsql = "select comm_num, rec_cnt, reg_time, files, name, profile, comment.user_num " +        // 댓글
                                   "from comment join user on comment.user_num = user.user_num " +
                                   "where content_num = " + contentnum;

                           request = SQLDataService.getSQLJSONData(commentsql, -1, "select");
                           SQLDataService.putBundleValue(request, "download", "context", "files");
                           SQLDataService.putBundleValue(request, "download", "context2", "profile");
                           JSONObject commentdata = new WebControll().WebLoad(request);     // SQL 돌리기
                           if (commentdata != null && commentdata.getJSONArray("result").length() != 0) {
                               Bitmap profile = null;                                   // 댓글 유저의 프로필
                               commentAdapter = new CommentAdapter(FriendProfileActivity.this);
                               JSONArray commentresult = commentdata.getJSONArray("result");
                               for (int j = 0; j < commentresult.length(); j++) {
                                   JSONObject resultdata = commentdata.getJSONArray("result").getJSONObject(j);
                                   profile = setProfile(resultdata);

                                   // 마지막에 줄띄우기 잘라내기

//                                   String temptext = resultdata.getString("text").substring(0,resultdata.getString("text").length()-2);
//                                   String temptext = resultdata.getString("text");
//                                   commentAdapter.addItem(contentnum, profile, resultdata.getString("name"), temptext, resultdata.getString("reg_time"), resultdata.getInt("user_num"));       // 어댑터 추가

//                                   String temptext = resultdata.getString("text").substring(0,resultdata.getString("text").length()-2);
                                   String temptext = resultdata.getString("text");
                                   temptext = temptext.substring(0, temptext.length()-2);
                                   String time = resultdata.getString("reg_time").substring(0,resultdata.getString("reg_time").length()-2);
                                   commentAdapter.addItem(contentnum, profile, resultdata.getString("name"), temptext, time, resultdata.getInt("user_num"));       // 어댑터 추가

                               }
                           }
                           Object[] objects = {contentnum, name, address, reg_time, rec_cnt, view_cnt, text, imagelist, commentAdapter};
                           publishProgress(objects);
                       }
                   }    // end -- if

                   // 내 자신 프로필 보는경우면 친구 추가 버튼 안보이게 하기
                   if(userInfo.getUserNum() == friendnum) {
                       mFriendAddButton.setVisibility(View.INVISIBLE);
                   }

                   // 친구 확인 부분
                   String friendSql = "select request " +
                                      "from friend " +
                                      "where user_num = " + userInfo.getUserNum() +" and " +
                                      "friend_num = (select user_num from user where user_num = " + friendnum +")";
                   request = SQLDataService.getSQLJSONData(friendSql, -1, "select");
                   JSONObject frienddata = new WebControll().WebLoad(request);

                   if (frienddata != null && frienddata.getJSONArray("result").length() != 0) {
                       JSONArray friendresult = frienddata.getJSONArray("result");      // 결과값
                       JSONObject resultdata = friendresult.getJSONObject(0);
                       if (resultdata.getInt("request") == 1) {
                           mFriendAddButton.setVisibility(View.INVISIBLE);
                       }
                   }

               } catch (JSONException e) {
                   e.printStackTrace();
               }
               return null;
           }
           @Override
           protected void onProgressUpdate(Object... values) {
               mContentAdapter.addItem(profilebitmap, (int) values[0], (String) values[1], (String) values[2], (String) values[3], (String) values[4],
                           (String) values[5], (String) values[6], (ArrayList<String>) values[7], userbitmap, (CommentAdapter) values[8],null);
           }
           @Override
           protected void onPostExecute(Void aVoid) {
               profile_image.setImageBitmap(profilebitmap);
               mContentlist.requestLayout();
               super.onPostExecute(aVoid);
           }
       }.execute();
    }

    public Bitmap setProfile(JSONObject data) throws JSONException {
        Bitmap bitmap = null;
        if(data.has("image2")) {      // 있는지 확인
            JSONArray profileUri = data.getJSONArray("image2");
            if (profileUri.length() != 0) {
                bitmap = ImageDownLoad.imageDownLoad(profileUri.getString(0));
            }
        }
        else {
            bitmap = ImageDownLoad.imageDownLoad(data.getString("profile"));
        }
        if (bitmap == null) {
            bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.basepicture, null)).getBitmap();
        }
        return bitmap;
    }

    @Override
    protected void onDestroy() {
        mContentAdapter.recycle();
        super.onDestroy();
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

}