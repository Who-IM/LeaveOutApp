package whoim.leaveout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import whoim.leaveout.Loading.LoadingSQLDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.StartSetting.SharedName;
import whoim.leaveout.User.UserInfo;

// 환경설정
public class PreferencesActivity extends AppCompatActivity {
    private Switch login;
    private SharedPreferences mLoginShared;       // 상태 저장(로그인 정보)
    private UserInfo userInfo = UserInfo.getInstance();

    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance(); // sql에 필요한 데이터 그룹

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_layout);
        mLoginShared = getSharedPreferences(SharedName.SHARED_LOGIN_INFO, Activity.MODE_PRIVATE);     // 로그인 정보 상태

        login = (Switch) findViewById(R.id.preferences_autologin);
        //자동 로그인 버튼
        login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true)
                {
                   // Toast.makeText(PreferencesActivity.this, "스위치 ON", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Toast.makeText(PreferencesActivity.this, "스위치 OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 버튼
    public void preferencesOnclick(View v) {
        if (v.getId() == R.id.preferences_category) {        //카테고리
            Intent intent = new Intent(getApplicationContext(), PreferencesCategoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else if (v.getId() == R.id.preferences_check_delete) {      //체크 삭제
            Intent intent = new Intent(getApplicationContext(), PreferencesCheckViewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        } /*else if (v.getId() == R.id.preferences_notice) {        //알림
            Intent intent = new Intent(getApplicationContext(), PreferencesNoticeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        }*/ else if (v.getId() == R.id.preferences_logout) {       //로그아웃
            AlertDialog.Builder d = new AlertDialog.Builder(PreferencesActivity.this);
            d.setTitle("로그아웃");
            d.setMessage("로그아웃을 하시겠습니까?");

            d.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                    mLoginShared.edit().clear().commit();       // 상태 정보 초기화
                    userInfo.clear();
                    Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            d.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            d.show();


        } else if (v.getId() == R.id.preferences_id_delete) {        //계정 탈퇴
            AlertDialog.Builder d = new AlertDialog.Builder(PreferencesActivity.this);
            d.setTitle("회원 탈퇴");
            d.setMessage("정말 회원 탈퇴를 하시겠습니까?");

            d.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PreferencesActivity.this.finish();
                    IDDeleteSQLData(); //계정삭제
                    mLoginShared.edit().clear().commit();       // 상태 정보 초기화
                    userInfo.clear();
                    Toast.makeText(PreferencesActivity.this, "계정 탈퇴 실행.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

            d.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            d.show();
        }
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


    //계정삭제 foreign키 걸림
    private void IDDeleteSQLData() {

        final String sql = "delete from user where user_num = ?";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {

            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(UserInfo.getInstance().getUserNum());
                return SQLDataService.getDynamicSQLJSONData(sql,mDataQueryGroup,0,"update");
            }

            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
            }
        };
        LoadingSQLDialog.SQLSendStart(this,loadingSQLListener, ProgressDialog.STYLE_SPINNER,null);
    }
}