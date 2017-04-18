package whoim.leaveout;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class login extends AppCompatActivity {

    TextView title;
    Button login;
    TextView insertlogin;
    TextView id_pwSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "RixToyGray.ttf");

        title = (TextView) findViewById(R.id.title01);
        login = (Button) findViewById(R.id.loginBtn);
        insertlogin = (TextView) findViewById(R.id.insertlogin);
        id_pwSelect = (TextView) findViewById(R.id.id_pwSelect);

        title.setTypeface(typeface);
        login.setTypeface(typeface);
        insertlogin.setTypeface(typeface);
        id_pwSelect.setTypeface(typeface);

    }

    public void loginButton(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
