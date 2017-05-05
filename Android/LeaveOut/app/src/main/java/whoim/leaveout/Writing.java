package whoim.leaveout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;

import android.net.Uri;
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
    int count = 0;
    Toolbar toolbar;
    ImageButton camera_button = null;
    ImageButton image_button = null;
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

//      TextView textView6 = (TextView) findViewById(R.id.textView5);
//      textView6.setTypeface(typeface);

        toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#00FFFFFF"));   //제목 투명하게
        setSupportActionBar(toolbar);   //액션바와 같게 만들어줌
        Button_start(); //글쓰기 버튼들 활성화
    }

    //글쓰기 버튼 사용
    private void Button_start()
    {
        camera_button = (ImageButton)findViewById(R.id.camera);    //카메라 버튼
        picture = (ImageView)findViewById(R.id.input_picture);  //사진 붙여넣는 공간
        image_button = (ImageButton)findViewById(R.id.image_icon);  //이미지 버튼

        //카메라 불러오기
        camera_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //카메라 기능 불러오기
                startActivityForResult(camera_intent, 1);
            }
        });

        //앨범 불러오기
        image_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent image_intent = new Intent(Intent.ACTION_PICK); //이미지 불러오기
                image_intent.setAction(Intent.ACTION_GET_CONTENT);
                image_intent.setType("image/*");
                startActivityForResult(image_intent, 2);
            }
        });
    }

    //사진을 글쓰기 화면에 나오게 하기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //requestCode 1=카메라 사진, 2=앨범 사진
        if (requestCode == 1)
        {
            try {
                if(count == 0) {
                    ImageView imageView = (ImageView) findViewById(R.id.input_picture);  //이미지 뷰에다가 찍은 사진 저장
                    Bitmap bm = (Bitmap) data.getExtras().get("data");  //이미지 저장
                    imageView.setImageBitmap(bm);   //이미지뷰에다가 이미지 저장
                }
                else if(count == 1)
                {
                    ImageView imageView = (ImageView) findViewById(R.id.input_picture2);  //이미지 뷰에다가 찍은 사진 저장
                    Bitmap bm = (Bitmap) data.getExtras().get("data");  //이미지 저장
                    imageView.setImageBitmap(bm);   //이미지뷰에다가 이미지 저장
                }
                else if(count == 2)
                {
                    ImageView imageView = (ImageView) findViewById(R.id.input_picture3);  //이미지 뷰에다가 찍은 사진 저장
                    Bitmap bm = (Bitmap) data.getExtras().get("data");  //이미지 저장
                    imageView.setImageBitmap(bm);   //이미지뷰에다가 이미지 저장
                }
                count++;
            }  catch (Exception e) {
                e.printStackTrace();
            }
        }

        else if (requestCode == 2)
        {
            try{
                    if(count == 0)
                    {
                        Uri imgUri = data.getData();    //이미지 위치 url
                        ImageView imageView = (ImageView)findViewById(R.id.input_picture);  //이미지 뷰에다가 찍은 사진 저장
                        Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri );
                        imageView.setImageBitmap(bm);   //이미지뷰에다가 이미지 저장
                    }
                    else if(count == 1)
                    {
                        Uri imgUri = data.getData();    //이미지 위치 url
                        ImageView imageView = (ImageView)findViewById(R.id.input_picture2);  //이미지 뷰에다가 찍은 사진 저장
                        Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri );
                        imageView.setImageBitmap(bm);   //이미지뷰에다가 이미지 저장
                    }
                    else if(count == 2)
                    {
                    Uri imgUri = data.getData();    //이미지 위치 url
                    ImageView imageView = (ImageView)findViewById(R.id.input_picture3);  //이미지 뷰에다가 찍은 사진 저장
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri );
                    imageView.setImageBitmap(bm);   //이미지뷰에다가 이미지 저장
                }
                count++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 뒤로가기
    public void writeBack(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

}
