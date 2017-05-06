package whoim.leaveout;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

// 환경설정
public class Profile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_main);

        Typeface menu_bar = Typeface.createFromAsset(getAssets(), "RixToyGray.ttf");
        TextView menu = (TextView) findViewById(R.id.profile_title);
        menu.setTypeface(menu_bar);

    }

    // 뒤로가기
    public void Back(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

}