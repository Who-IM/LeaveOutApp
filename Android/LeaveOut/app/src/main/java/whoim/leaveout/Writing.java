package whoim.leaveout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

// 글쓰기
public class Writing extends AppCompatActivity {
    Toolbar toolbar;
    ImageButton camera_button = null;
    ImageButton image_button = null;
    ImageView picture = null;

    ArrayList<write_list_view_data> ar_write_pic_data;    //그림 넣는 공간

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_main);

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

        toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#00FFFFFF"));   //제목 투명하게
        setSupportActionBar(toolbar);   //액션바와 같게 만들어줌
        Button_start(); //글쓰기 버튼들 활성화

        ar_write_pic_data = new ArrayList<write_list_view_data>();
        write_list_view_data list_view;
        list_view = new write_list_view_data(R.drawable.public_wirte_background);
        ar_write_pic_data.add(list_view);

        write_adapter adapter = new write_adapter(this, R.layout.write, ar_write_pic_data);

        //리스트뷰 추가
        ListView list;
        list = (ListView)findViewById(R.id.write_listview);
        list.setAdapter(adapter);   //어뎁터 샛팅
    }

    // 리스트뷰에 출력할 항목 클래스
    class write_list_view_data {

        int Icon;
        String Name;

        write_list_view_data(int aIcon) {
            Icon = aIcon;   //이미지
        }
    }

    // 어댑터 클래스
    class write_adapter extends BaseAdapter {

        Context con;
        LayoutInflater inflacter;
        ArrayList<write_list_view_data> arD;
        int layout;

        public write_adapter(Context context, int alayout, ArrayList<write_list_view_data> aarD) {
            con = context;
            inflacter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            arD = aarD;
            layout = alayout;
        }

        // 어댑터에 몇 개의 항목이 있는지 조사
        @Override
        public int getCount() {
            return arD.size();
        }

        // position 위치의 항목 Name 반환
        @Override
        public Object getItem(int position) {
            return arD.get(position).Name;
        }

        // position 위치의 항목 ID 반환
        @Override
        public long getItemId(int position) {
            return position;
        }

        // 각 항목의 뷰 생성 후 반환
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflacter.inflate(layout, parent, false);
            }
            //사진 넣는 공간
            ImageView img = (ImageView) convertView.findViewById(R.id.input_picture);
            img.setImageResource(arD.get(position).Icon);
            return convertView;
        }
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
                ImageView imageView = (ImageView) findViewById(R.id.input_picture);  //이미지 뷰에다가 찍은 사진 저장
                Bitmap bm = (Bitmap) data.getExtras().get("data");  //이미지 저장
                imageView.setImageBitmap(bm);   //이미지뷰에다가 이미지 저장

            }  catch (Exception e) {
                e.printStackTrace();
            }
        }

        else if (requestCode == 2)
        {
            try{
                Uri imgUri = data.getData();    //이미지 위치 url
                ImageView imageView = (ImageView)findViewById(R.id.input_picture);  //이미지 뷰에다가 찍은 사진 저장
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri );
                imageView.setImageBitmap(bm);   //이미지뷰에다가 이미지 저장
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
