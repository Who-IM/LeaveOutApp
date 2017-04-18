package whoim.leaveout;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Preferences extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        Typeface menu_bar = Typeface.createFromAsset(getAssets(), "RixToyGray.ttf");
        TextView menu = (TextView) findViewById(R.id.preferences);
        menu.setTypeface(menu_bar);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "HMKMMAG.TTF");
        TextView setting = (TextView) findViewById(R.id.setting);
        TextView range = (TextView) findViewById(R.id.range);
        TextView open = (TextView) findViewById(R.id.open);
        TextView push = (TextView) findViewById(R.id.push);
        TextView gps = (TextView) findViewById(R.id.gps);
        TextView autologin = (TextView) findViewById(R.id.autologin);
        Button logout = (Button) findViewById(R.id.logout);

        setting.setTypeface(typeface);
        range.setTypeface(typeface);
        open.setTypeface(typeface);
        push.setTypeface(typeface);
        gps.setTypeface(typeface);
        autologin.setTypeface(typeface);
        logout.setTypeface(typeface);
    }

    public void logout(View v) {
        Intent intent = new Intent(getApplicationContext(), login.class);
        startActivity(intent);
    }

    public void Back(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

}