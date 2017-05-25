package whoim.leaveout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import whoim.leaveout.SQL.SQLWeb;
import whoim.leaveout.StartSetting.SharedName;
import whoim.leaveout.User.UserInfo;

// 로그인
public class loginActivity extends AppCompatActivity {

    private SharedPreferences mLoginShared;       // 상태 저장(로그인 정보)
    private UserInfo mUserInfo;                 // 유저 정보
    private CallbackManager mCallbackManager;   // 페이스북 전용 콜백 관리자
    private LoginButton mLoginButton;           // 페이스북 로그인 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        try {
            JSONObject dataJSON = new JSONObject();
            dataJSON.put("data","test중입니다.");
            JSONObject sqlWeb = new SQLWeb().execute(dataJSON).get();
            Log.d("sqlWeb",sqlWeb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


        mUserInfo = UserInfo.getInstance();     // 유저 정보 객체 가져오기
        mLoginShared = getSharedPreferences(SharedName.SHARED_LOGIN_INFO, Activity.MODE_PRIVATE);     // 로그인 정보 상태

        if (loginCheck()) {       // 자동 로그인 체크
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);        // 메인화면으로
            startActivity(intent);
//            finish();           // 액티비티 종료
        }

        mLoginButton = (LoginButton) findViewById(R.id.facebook_loginBtn);
        faceBookLoginSet();     // 페이스북 로그인 버튼 셋팅

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
    private boolean loginCheck() {
        if(mLoginShared.getString("id",null) != null) {     // LeaveOut 로그인을 했을경우
            Toast.makeText(this,"앱으로 로그인",Toast.LENGTH_SHORT).show();  // 테스트
            mUserInfo.setId(mLoginShared.getString("id",null));       //  유저 id 셋팅
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
    protected void onResume() {
/*        if (loginCheck()) {       // 자동 로그인 체크
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);        // 메인화면으로
            startActivity(intent);
//            finish();           // 액티비티 종료
        }*/
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }

    //회원가입 화면
    public void joinButton(View v) {
        Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
        startActivity(intent);
    }

    // 비번 찾기 화면
    public void idPwSelect(View view) {
        SharedPreferences.Editor LoginSharedEdit = mLoginShared.edit();     // 상태 저장 에디터
        LoginSharedEdit.clear().commit();
        Toast.makeText(this,"비번 찾기 버튼 테스트 완료",Toast.LENGTH_SHORT).show();
    }

    // 앱 로그인 버튼 메인화면으로 넘어가기(데이터베이스에서 확인 후 넘기기)
    public void loginButton(View v) {
        EditText editText = (EditText) findViewById(R.id.userName);     // 유저 id 텍스트 필드
        if(!editText.getText().toString().equals("")) {
            SharedPreferences.Editor LoginSharedEdit = mLoginShared.edit();     // 상태 저장 에디터
            LoginSharedEdit.putString("id",editText.getText().toString());      // 유저 id 상태 저장
            LoginSharedEdit.commit();       // commit
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);        // 메인 화면으로
            startActivity(intent);
            finish();       // 액티비티 종료
        }
        else
            Toast.makeText(this,"ID를 입력해주세여",Toast.LENGTH_SHORT).show();

    }

    // 메인화면으로 넘어가기
    public void facebook_loginButton(View v) {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void findButton(View v) {
        Intent intent = new Intent(getApplicationContext(), FindActivity.class);
        startActivity(intent);
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
