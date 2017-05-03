package whoim.leaveout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import static whoim.leaveout.R.id.input_picture;

// 글쓰기
public class Writing extends AppCompatActivity {
    Toolbar toolbar;
    ImageButton camer_abutton;
    ImageView picture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write);

        camera_start();

        //폰트처리
        Typeface typeface = Typeface.createFromAsset(getAssets(), "RixToyGray.ttf");
        TextView textView = (TextView) findViewById(R.id.write_commit);
        TextView textView2 = (TextView) findViewById(R.id.write_title);
        TextView textView3 = (TextView) findViewById(R.id.write_loc_text);
        TextView textView4 = (TextView) findViewById(R.id.write_fence);
        textView.setTypeface(typeface);
        textView2.setTypeface(typeface);
        textView3.setTypeface(typeface);
        textView4.setTypeface(typeface);

        TextView textView5 = (TextView) findViewById(R.id.write_input);
//        TextView textView6 = (TextView) findViewById(R.id.textView5);
        textView5.setTypeface(typeface);
//        textView6.setTypeface(typeface);

        toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#00FFFFFF"));   //제목 투명하게
        setSupportActionBar(toolbar);   //액션바와 같게 만들어줌

    }

    private void camera_start()
    {
        camer_abutton = (ImageButton)findViewById(R.id.camera);
        picture = (ImageView)findViewById(input_picture);

        camer_abutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        picture.setImageURI(data.getData());
    }

    // 뒤로가기
    public void writeBack(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
