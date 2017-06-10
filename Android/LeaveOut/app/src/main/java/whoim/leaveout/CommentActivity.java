package whoim.leaveout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bu456 on 2017-06-09.
 */

public class CommentActivity extends AppCompatActivity {
    // 리사이클 뷰
    RecyclerView lecyclerView = null;
    ArrayList<Button> comment_item_btn2 = null;

    // 댓글 카운터
    TextView comment_count = null;

    // 글쓰는 공간 셋팅
    ImageView comment_edit_image = null;
    EditText comment_edit_text = null;

    // 실제 데이터
    List<comment_data> comment_list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_layout);

        // 댓글 카운터
        comment_count = (TextView) findViewById(R.id.comment_count);

        // reciycleView(답글)
        lecyclerView = (RecyclerView) findViewById(R.id.comment_item_list);
        comment_item_btn2 = new ArrayList<Button>();

        // 자기 사진 및 edittext
        comment_edit_image = (ImageView) findViewById(R.id.comment_edit_image);
        comment_edit_text = (EditText) findViewById(R.id.comment_edit_text);

        // db에서 자기사진 추가
        comment_edit_image.setImageResource(R.drawable.basepicture);

        // 데이터 셋팅(db에서)
        comment_list = new ArrayList<comment_data>();
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM월 dd일 HH:mm:ss");
        String time = sdfNow.format(new Date(System.currentTimeMillis()));
        for (int i = 0; i < 5; i ++){
            comment_data album = new comment_data();
            album.setName("허성문");
            album.setContents("태스트중");
            album.setImage(R.drawable.basepicture);
            album.setTime(time);
            comment_list.add(album);
        }
        comment_count.setText(comment_list.size() + "");

        lecyclerView.setAdapter(new CommentAdapter(comment_list));
        lecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        lecyclerView.setItemAnimator(new DefaultItemAnimator());

        comment_edit_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // 시간
                    SimpleDateFormat sdfNow = new SimpleDateFormat("MM월 dd일 HH:mm:ss");
                    String time = sdfNow.format(new Date(System.currentTimeMillis()));

                    // 데이터 추가
                    comment_data album = new comment_data();
                    album.setName("허성문");  // db에서 이름 따오기
                    album.setContents(comment_edit_text.getText().toString());
                    album.setImage(R.drawable.basepicture);  // db에서 이미지 따오기
                    album.setTime(time);
                    comment_list.add(album);
                    lecyclerView.setAdapter(new CommentAdapter(comment_list));

                    // 댓글 수 및 내용 초기화
                    comment_edit_text.setText("");
                    comment_count.setText(comment_list.size() + "");

                    return true;
                }
                return false;
            }
        });
    }

    // 댓글 및 답글에 공용사용
    public class comment_data {
        private int image;
        private String name;
        private String contents;
        private String time;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getContents() {
            return contents;
        }
        public void setContents(String contents) {
            this.contents = contents;
        }

        public int getImage() {
            return image;
        }
        public void setImage(int image) {
            this.image = image;
        }

        public String getTime() {
            return time;
        }
        public void setTime(String time) {
            this.time = time;
        }
    }

    // 여기부터 댓글(RecycleView)
    public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

        private List<comment_data> comment_list_data;
        View view = null;

        public CommentAdapter(List<comment_data> items){
            this.comment_list_data = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item, viewGroup, false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            comment_data item = comment_list_data.get(position);
            viewHolder.name.setText(item.getName());
            viewHolder.contenst.setText(item.getContents());
            viewHolder.img.setBackgroundResource(item.getImage());
            viewHolder.time.setText(item.getTime());

            viewHolder.itemView.setTag(item);

        }

        @Override
        public int getItemCount() {
            return comment_list_data.size();      //배열 길이 만큼 + footer
        }

        /**
         * 뷰 재활용을 위한 viewHolder
         */
        public class ViewHolder extends RecyclerView.ViewHolder{

            public ImageView img;
            public TextView name;
            public TextView contenst;
            public TextView time;

            public ViewHolder(View itemView){
                super(itemView);

                img = (ImageView) itemView.findViewById(R.id.comment_item_image);
                name = (TextView) itemView.findViewById(R.id.comment_item_name);
                contenst = (TextView) itemView.findViewById(R.id.comment_item_text);
                time = (TextView) itemView.findViewById(R.id.comment_item_time);

            }
        }
    }
    /// ----------------- 여기까지 -----------------------
}
