package whoim.leaveout;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import whoim.leaveout.Loading.LoadingSQLDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.StartSetting.Permission;
import whoim.leaveout.StartSetting.SharedName;
import whoim.leaveout.User.UserInfo;

// 로그인
public class loginActivity extends AppCompatActivity {

    private InputMethodManager mInputMethodManager;     // 키보드 서비스

    private SharedPreferences mLoginShared;       // 상태 저장(로그인 정보)
    private UserInfo mUserInfo;                 // 유저 정보

    private CallbackManager mCallbackManager;   // 페이스북 전용 콜백 관리자
    private LoginButton mLoginButton;           // 페이스북 로그인 버튼

    EditText mIdEditText;           // id 에딧
    EditText mPassEditText;         // pass 에딧

    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance();          // sql에 필요한 데이터 그룹
    private String mSelectSQL = "select user_num,id from user where id = ? and password = ?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        Permission.permissionSetting(this, new String[]{Manifest.permission.READ_PHONE_STATE});     // 권한

        mIdEditText = (EditText) findViewById(R.id.userName);       // 유저 id 텍스트 필드
        mPassEditText = (EditText) findViewById(R.id.passWord);     // 유저 pass 텍스트 필드
        mLoginButton = (LoginButton) findViewById(R.id.facebook_loginBtn);      // 페이스 북 로그인 버튼

        init();     // 초기화
    }

    // 초기화
    private void init() {
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);      // 키보드 서비스

        mUserInfo = UserInfo.getInstance();     // 유저 정보 객체 가져오기
        mLoginShared = getSharedPreferences(SharedName.SHARED_LOGIN_INFO, Activity.MODE_PRIVATE);     // 로그인 정보 상태
        autoLogin();        // 오토 로그인 확인

        faceBookLoginSet();     // 페이스북 로그인 버튼 셋팅

        mPassEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mInputMethodManager.hideSoftInputFromWindow(mPassEditText.getWindowToken(),0);      // 키보드 내리기
                    loginSelectSQLData();        // select sql 쿼리 돌리기
                    return true;
                }
                return false;
            }
        });

    }

    // 페이스북 로그인 셋팅
    private void faceBookLoginSet() {
        mCallbackManager = CallbackManager.Factory.create();        // 페이스북 전용 콜백관리 생성
        mLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));      // 페이스북 데이터 권한
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {       // 로그인 확인 콜백 메소드 생성
            @Override
            public void onSuccess(final LoginResult loginResult) {      // 로그인 성공시
                faceBookRequest(loginResult.getAccessToken());          // 페이스북에 사용자 정보 요청하기
                Log.d("TAG", loginResult.getAccessToken().getToken());
            }
            @Override
            public void onCancel() {        // 로그인 취소시
                Log.d("onCancel","취소");

            }
            @Override
            public void onError(FacebookException error) {  // 로그인 실패시
                Log.e("LoginErr",error.toString());
            }
        });
    }

    // 페이스북 데이터 정보 가져오기
    private void faceBookRequest(AccessToken accessToken) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {        // 로그인
            @Override
            public void onCompleted(JSONObject data, GraphResponse response) {
                try {
                    Log.d("result", data.toString());
                    mUserInfo.setFacebookId(data.getString("id"));      // 페이스북 유저 데이터 저장
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,picture");     // 원하는 정보 파라미터로 넣기
        graphRequest.setParameters(parameters);     // 셋팅
        graphRequest.executeAsync();    // 페이스북으로 요청 전송
    }

    // 자동 로그인 체크
    private boolean autoLogin() {
        if(mLoginShared.getInt("user_num",0) != 0) {     // LeaveOut 로그인을 했을경우
            autoCheckSelectSQL();           // 데이터베이스 확인 요청
            return true;
        }
        else if(AccessToken.getCurrentAccessToken() != null) {      // 페이스북 로그인을 했을경우
            Toast.makeText(this,"페이스북으로 로그인",Toast.LENGTH_SHORT).show();  // 테스트
            faceBookRequest(AccessToken.getCurrentAccessToken());       // 페이스북에 사용자 요청
            return true;
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }

    //회원가입 화면
    public void joinButton(View v) {
        Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);        // 위에 액티비티 클리어, 싱글로, 스택에 안채우기
        startActivity(intent);
    }

    // 비번 찾기 화면
    public void idPwSelect(View view) {
        SharedPreferences.Editor LoginSharedEdit = mLoginShared.edit();     // 상태 저장 에디터(테스트)
        LoginSharedEdit.clear().commit();   // 테스트용
        Toast.makeText(this,"비번 찾기 버튼 테스트 완료",Toast.LENGTH_SHORT).show();
    }

    // 앱 로그인 버튼 메인화면으로 넘어가기(데이터베이스에서 확인 후 넘기기)
    public void loginButton(View v) {
        loginSelectSQLData();        // select sql 쿼리 돌리기
    }

    // 페이스북 로그인 버튼 메인화면으로 넘어가기
    public void facebook_loginButton(View v) {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public boolean editCheckAll() {
        if(mIdEditText.getText().toString().equals("")) {
            Toast.makeText(this, "아이디를 입력해주세여", Toast.LENGTH_SHORT).show();
            mIdEditText.requestFocus();       // 포커스 이동
            mInputMethodManager.showSoftInput(mIdEditText,InputMethodManager.SHOW_FORCED);         // 키보드 보이기
            return false;
        }
        if(mPassEditText.getText().toString().equals("")) {
            Toast.makeText(this, "비밀번호를 입력해주세여", Toast.LENGTH_SHORT).show();
            mPassEditText.requestFocus();       // 포커스 이동
            mInputMethodManager.showSoftInput(mPassEditText,InputMethodManager.SHOW_FORCED);         // 키보드 보이기
            return false;
        }
        return true;
    }

    private void loginSelectSQLData() {
        if(editCheckAll()) {
            LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
                @Override
                public JSONObject getDataSend() {
                    mDataQueryGroup.clear();        // 초기화
                    mDataQueryGroup.addString(mIdEditText.getText().toString());        // 쿼리 id 추가
                    mDataQueryGroup.addString(mPassEditText.getText().toString());      // 쿼리 pass 추가
                    return SQLDataService.getDynamicSQLJSONData(mSelectSQL, mDataQueryGroup, -1, "select");             // select SQL 제이슨
                }
                @Override
                public void dataProcess(JSONObject responseData, Object caller) throws JSONException {
                    JSONArray result = responseData.getJSONArray("result");     // 결과 값 가져오기
                    if (result.length() == 0)        // 데이터베이스에 입력한 ID가 없을경우
                        Toast.makeText(loginActivity.this, "아이디 혹은 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                    else if (result.getJSONObject(0).getString("id").equals(mIdEditText.getText().toString())) {     // 데이터베이스에 입력한 ID가 있을경우
                        SharedPreferences.Editor LoginSharedEdit = mLoginShared.edit();     // 상태 저장 에디터
                        LoginSharedEdit.putInt("user_num", result.getJSONObject(0).getInt("user_num"));      // 유저 id 상태 저장
                        LoginSharedEdit.commit();       // commit
                        mUserInfo.setUserNum(mLoginShared.getInt("user_num",0));       //  유저 id 셋팅

                        nextActivity();     // 메인액티비티로
                    }
                }
            };
            LoadingSQLDialog.SQLSendStart(this, loadingSQLListener, null);       // sql 시작
        }
    }

    // 오토 로그인 하기 전 데이터베이스에 있는지 확인
    private void autoCheckSelectSQL() {
        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public JSONObject getDataSend() {
                String sql = "select id from user where user_num = " + mLoginShared.getInt("user_num",0);
                return SQLDataService.getSQLJSONData(sql,-1,"select");
            }
            @Override
            public void dataProcess(JSONObject responseData, Object caller) throws JSONException {
                JSONArray result = responseData.getJSONArray("result");     // 결과 값 가져오기
                if(result.length() == 0) {
                    Toast.makeText(loginActivity.this, "다시 로그인 해주십시오.", Toast.LENGTH_LONG).show();     // 없을경우
                    mLoginShared.edit().clear().commit();       // 상태 정보 초기화
                }
                else {
                    Toast.makeText(loginActivity.this,"앱으로 로그인",Toast.LENGTH_SHORT).show();  // 테스트
                    mUserInfo.setUserNum(mLoginShared.getInt("user_num",0));       //  유저 id 셋팅
                    nextActivity();        // 있을경우
                }
            }
        };
        LoadingSQLDialog.SQLSendStart(this, loadingSQLListener, null);       // sql 시작
    }

    // 메인액티비티로
    private void nextActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);        // 메인 화면으로
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        loginActivity.this.startActivity(intent);       // 다음 액티비티
//        loginActivity.this.finish();                    // 액티비티 종료
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
