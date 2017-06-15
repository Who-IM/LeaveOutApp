package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import whoim.leaveout.Adapter.CommentAdapter;
import whoim.leaveout.Adapter.ContentAdapter;
import whoim.leaveout.Loading.LoadingDialogBin;
import whoim.leaveout.Server.ImageDownLoad;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.Server.WebControll;
import whoim.leaveout.User.UserInfo;

// 글 보기
public class ViewArticleActivity extends AppCompatActivity
{
    private Intent data;        // 데이터
    private JSONObject jsondata;      // 제이슨 데이터
    private UserInfo mUserInfo = UserInfo.getInstance();

    int menuCount = 0;  //매뉴 옵션 아이템 순서

    // tabLayout 및 toobar 이름 수정
    private TabLayout public_view_article_tab = null;
    private TextView public_view_article_title = null;

    // list 게시글
    private ListView mContentlist = null;
    private ContentAdapter mContentAdapter;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_view_article_layout);

        data = getIntent();
        if(data != null) {
            try {
                jsondata = new JSONObject(data.getStringExtra("responseData"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#00FFFFFF"));   //제목 투명하게
        setSupportActionBar(toolbar);   //액션바와 같게 만들어줌
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.

        // tabLayout 및 toobar 이름 수정
        public_view_article_tab = (TabLayout) findViewById(R.id.public_view_article_tab);
        public_view_article_tab.setVisibility(View.GONE);
        public_view_article_title = (TextView) findViewById(R.id.public_view_article_title);
        public_view_article_title.setText("글 보기");

        // 모아보기 listview 셋팅
        setCollect();
        contentDataSet();
    }

    //옵션 버튼
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 메뉴버튼이 처음 눌러졌을 때 실행되는 콜백메서드
        // 메뉴버튼을 눌렀을 때 보여줄 menu 에 대해서 정의
        getMenuInflater().inflate(R.menu.collect_menu, menu);
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
            case R.id.collect_menu_location:
                Toast.makeText(getApplicationContext(), "가까운 위치 순서대로", Toast.LENGTH_SHORT).show();
                menuCount = 0;
                return true;

            case R.id.collect_menu_time:
                Toast.makeText(getApplicationContext(), "최신글 순서대로", Toast.LENGTH_SHORT).show();
                menuCount = 1;
                return true;

            case R.id.collect_menu_view:
                Toast.makeText(getApplicationContext(), "조회수 순서대로", Toast.LENGTH_SHORT).show();
                menuCount = 2;
                return true;

            case R.id.collect_menu_recommended:
                Toast.makeText(getApplicationContext(), "추천수 순서대로", Toast.LENGTH_SHORT).show();
                menuCount = 3;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // 옵션 버튼 끝

    // 모아보기 listview 셋팅
    private void setCollect() {

        // 메뉴
        mContentlist = (ListView) findViewById(R.id.public_view_article_listview);

        // 어뎁터 생성민 등록
        mContentAdapter = new ContentAdapter(this);
        mContentlist.setAdapter(mContentAdapter);

    }

    private void contentDataSet() {
        new LoadingDialogBin(this) {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (jsondata != null && jsondata.getJSONArray("result").length() != 0) {            // 게시글 가져오기
                        JSONArray resultData = jsondata.getJSONArray("result");   // 결과값
                        for (int i = 0; i < resultData.length(); i++) {
                            CommentAdapter commentAdapter = null;                   // 댓글 어댑터
                            JSONObject contentdata = resultData.getJSONObject(i);   // 게시글 데이터

                            ArrayList<String> imagelist = new ArrayList();          // 이미지 넣을 데이터
                            JSONArray imageArray = resultData.getJSONObject(i).getJSONArray("image");       // 게시글에서 이미지 가져오기
                            for (int j = 0; j < imageArray.length(); j++) {
                                imagelist.add(imageArray.getString(j));             // 이미지 배열에 넣기
                            }

                            int contentnum = contentdata.getInt("content_num");     // 게시글 번호 가져오기
                            String name = contentdata.getString("name");
                            String address = contentdata.getString("address");
                            String reg_time = contentdata.getString("reg_time");
                            String rec_cnt = contentdata.getString("rec_cnt");
                            String view_cnt = contentdata.getString("view_cnt");
                            String text = contentdata.getString("text");
                            Bitmap contentuserprofile = setProfile(contentdata);

                            // 댓글 부분
                            String commentsql = "select comm_num, rec_cnt, reg_time, files, name, profile, user_num " +        // 댓글
                                    "from comment join user on comment.user_num = user.user_num " +
                                    "where content_num = " + contentnum;

                            JSONObject request = SQLDataService.getSQLJSONData(commentsql, -1, "select");
                            SQLDataService.putBundleValue(request, "download", "context", "files");
                            SQLDataService.putBundleValue(request, "download", "context2", "profile");
                            JSONObject commentdata = new WebControll().WebLoad(request);     // SQL 돌리기
                            if (commentdata != null && commentdata.getJSONArray("result").length() != 0) {
                                Bitmap profile = null;                                   // 댓글 유저의 프로필
                                commentAdapter = new CommentAdapter();
                                JSONArray commentresult = commentdata.getJSONArray("result");
                                for (int j = 0; j < commentresult.length(); j++) {
                                    JSONObject resultdata = commentdata.getJSONArray("result").getJSONObject(j);
                                    profile = setProfile(resultdata);

                                    // 마지막에 줄띄우기 잘라내기
                                    String temptext = resultdata.getString("text").substring(0,resultdata.getString("text").length()-2);
                                    commentAdapter.addItem(contentnum, profile, resultdata.getString("name"), temptext, resultdata.getString("reg_time"), resultdata.getInt("user_num"));       // 어댑터 추가
                                }
                            }
                            Object[] objects = {contentuserprofile,contentnum, name, address, reg_time, rec_cnt, view_cnt, text, imagelist, commentAdapter};
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
                mContentAdapter.addItem((Bitmap) values[0], (int)values[1], (String)values[2], (String)values[3], (String)values[4],(String)values[5],
                        (String)values[6],(String)values[7], (ArrayList<String>) values[8], mUserInfo.getProfile(),(CommentAdapter)values[9]);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
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


