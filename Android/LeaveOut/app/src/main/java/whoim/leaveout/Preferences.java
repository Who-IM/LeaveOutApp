package whoim.leaveout;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// 환경설정
public class Preferences extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        Typeface menu_bar = Typeface.createFromAsset(getAssets(), "RixToyGray.ttf");
        TextView menu = (TextView) findViewById(R.id.pre_preferences);
        menu.setTypeface(menu_bar);

        // 폰트처리
        Typeface typeface = Typeface.createFromAsset(getAssets(), "HMKMMAG.TTF");
        TextView setting = (TextView) findViewById(R.id.pre_setting);
        TextView range = (TextView) findViewById(R.id.pre_range);
        TextView open = (TextView) findViewById(R.id.pre_open);
        TextView push = (TextView) findViewById(R.id.pre_push);
        TextView gps = (TextView) findViewById(R.id.pre_gps_text);
        TextView autologin = (TextView) findViewById(R.id.pre_autologin);
        Button logout = (Button) findViewById(R.id.pre_logout);

        setting.setTypeface(typeface);
        range.setTypeface(typeface);
        open.setTypeface(typeface);
        push.setTypeface(typeface);
        gps.setTypeface(typeface);
        autologin.setTypeface(typeface);
        logout.setTypeface(typeface);
    }

    // 로그아웃
    public void logout(View v) {
        Intent intent = new Intent(getApplicationContext(), login.class);
        startActivity(intent);
    }

    // 뒤로가기
    public void Back(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

}