package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONObject;

import java.util.Arrays;

// 로그인
public class login extends AppCompatActivity {

    private CallbackManager mCallbackManager;
    private LoginButton mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        mCallbackManager = CallbackManager.Factory.create();
        mLoginButton = (LoginButton) findViewById(R.id.facebook_loginBtn);
        mLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("result", object.toString());
                        Log.d("response", response.toString());
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,picture");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

                Log.d("TAG", loginResult.getAccessToken().getToken() + " [ID] " + loginResult.getAccessToken().getUserId());
            }

            @Override
            public void onCancel() {
                Log.d("onCancel","취소");

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("LoginErr",error.toString());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }

    //회원가입 화면
    public void joinButton(View v) {
        Intent intent = new Intent(getApplicationContext(), Join.class);
        startActivity(intent);
    }
    // 메인화면으로 넘어가기
    public void loginButton(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    // 메인화면으로 넘어가기
    public void facebook_loginButton(View v) {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
