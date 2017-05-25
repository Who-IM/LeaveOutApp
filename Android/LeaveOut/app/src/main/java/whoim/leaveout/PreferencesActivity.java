package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

// 환경설정
public class PreferencesActivity extends AppCompatActivity {
    Switch login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_layout);
        login = (Switch) findViewById(R.id.preferences_autologin);

        //자동 로그인 버튼
        login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true)
                {
                    Toast.makeText(PreferencesActivity.this, "스위치 ON", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(PreferencesActivity.this, "스위치 OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 버튼
    public void preferencesOnclick(View v) {
        if (v.getId() == R.id.preferences_category) {        //카테고리
            Toast.makeText(this, "카테고리 실행.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), PreferencesCategoryActivity.class);
            startActivity(intent);
        }

        else if (v.getId() == R.id.preferences_check_delete) {      //체크 삭제
            Toast.makeText(this, "체크 삭제 실행.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), PreferencesCheckViewActivity.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.preferences_notice) {        //알림
            Toast.makeText(this, "알림 실행.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), PreferencesNoticeActivity.class);
            startActivity(intent);

        }
        else if (v.getId() == R.id.preferences_logout) {       //로그아웃
            Toast.makeText(this, "로그아웃 실행.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), loginActivity.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.preferences_id_delete) {        //계정 탈퇴
            Toast.makeText(this, "계정 탈퇴 실행.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), loginActivity.class);
            startActivity(intent);
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
}