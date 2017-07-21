package whoim.leaveout;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bu456 on 2017-07-20.
 */

public class FriendRequestActivity extends AppCompatActivity {

    // 리사이클 뷰
    RecyclerView lecyclerView = null;

    // 실제 데이터
    List<friendrequest_data> request_list = null;
    friendrequestAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendrequest_layout);

        // reciycleView(답글)
        lecyclerView = (RecyclerView) findViewById(R.id.friendrequest_listview);
        request_list = new ArrayList<friendrequest_data>();

        friendrequest_data album = new friendrequest_data();
        album.setName("요청");
        //album.setImage();
        request_list.add(album);

        adapter = new friendrequestAdapter(request_list);
        lecyclerView.setAdapter(adapter);
        lecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        lecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    // 댓글 및 답글에 공용사용
    public class friendrequest_data {
        private Bitmap image;
        private String name;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public Bitmap getImage() {
            return image;
        }
        public void setImage(Bitmap image) {
            this.image = image;
        }
    }

    // 여기부터 댓글(RecycleView)
    public class friendrequestAdapter extends RecyclerView.Adapter<friendrequestAdapter.ViewHolder> {

        private List<friendrequest_data> friendrequest_list_data;
        View view = null;

        public friendrequestAdapter(List<friendrequest_data> items){
            this.friendrequest_list_data = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friendrequest_item, viewGroup, false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            friendrequest_data item = friendrequest_list_data.get(position);
            viewHolder.name.setText(item.getName());
           // viewHolder.img.setImageBitmap(item.getImage());

            viewHolder.itemView.setTag(item);

        }

        @Override
        public int getItemCount() {
            return friendrequest_list_data.size();      //배열 길이 만큼 + footer
        }

        /**
         * 뷰 재활용을 위한 viewHolder
         */
        public class ViewHolder extends RecyclerView.ViewHolder{

            public ImageView img;
            public TextView name;

            public ViewHolder(View itemView){
                super(itemView);

                img = (ImageView) itemView.findViewById(R.id.friendrequest_image);
                name = (TextView) itemView.findViewById(R.id.friendrequest_item);
            }
        }
    }
    /// ----------------- 여기까지 -----------------------
}
