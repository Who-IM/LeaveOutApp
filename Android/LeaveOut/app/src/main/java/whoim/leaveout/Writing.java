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

// 글쓰기
public class Writing extends AppCompatActivity {
    Toolbar toolbar;
    ImageButton camera_abutton = null;
    ImageView picture = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write);

        //폰트처리
        Typeface typeface = Typeface.createFromAsset(getAssets(), "RixToyGray.ttf");
        TextView textView = (TextView) findViewById(R.id.write_commit);
        TextView textView2 = (TextView) findViewById(R.id.write_title);
        TextView textView3 = (TextView) findViewById(R.id.write_loc_text);
        TextView textView4 = (TextView) findViewById(R.id.write_fence);
        TextView textView5 = (TextView) findViewById(R.id.write_input);
        textView.setTypeface(typeface);
        textView2.setTypeface(typeface);
        textView3.setTypeface(typeface);
        textView4.setTypeface(typeface);
        textView5.setTypeface(typeface);

//        TextView textView6 = (TextView) findViewById(R.id.textView5);
//        textView6.setTypeface(typeface);

        toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#00FFFFFF"));   //제목 투명하게
        setSupportActionBar(toolbar);   //액션바와 같게 만들어줌
        camera_start();
    }

    //카메라 시작
    private void camera_start()
    {
        camera_abutton = (ImageButton)findViewById(R.id.camera);
        picture = (ImageView)findViewById(R.id.input_picture);

        camera_abutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
            }
        });
    }

    //사진을 글쓰기 화면에 나오게 하기(미완성)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        picture.setImageURI(data.getData());
    }

    // 뒤로가기
    public void writeBack(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
