package whoim.leaveout;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class Join extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "RixToyGray.ttf");
        TextView join_title = (TextView) findViewById(R.id.join_name);
        join_title.setTypeface(typeface);
    }

    public void joinButton(View v)
    {
        Intent intent = new Intent(getApplicationContext(), login.class);
        startActivity(intent);
    }

    public void Join_cancel_Button(View v)
    {
        Intent intent = new Intent(getApplicationContext(), login.class);
        startActivity(intent);
    }
}
