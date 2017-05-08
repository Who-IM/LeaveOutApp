package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tsengvn.typekit.TypekitContextWrapper;

// 환경설정
public class PreferencesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_layout);
    }

    // 로그아웃
    public void logout(View v) {
        Intent intent = new Intent(getApplicationContext(), loginActivity.class);
        startActivity(intent);
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