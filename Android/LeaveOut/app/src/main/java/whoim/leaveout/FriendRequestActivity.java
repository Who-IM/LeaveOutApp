package whoim.leaveout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import whoim.leaveout.Loading.LoadingDialogBin;
import whoim.leaveout.Server.ImageDownLoad;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.Server.WebControll;
import whoim.leaveout.User.UserInfo;

/**
 * Created by bu456 on 2017-07-20.
 */

public class FriendRequestActivity extends AppCompatActivity {

    // 리사이클 뷰
    RecyclerView lecyclerView = null;

    // 실제 데이터
    List<friendrequest_data> request_list = null;
    friendrequestAdapter adapter = null;

    JSONObject request;
    UserInfo userInfo = UserInfo.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendrequest_layout);

        // reciycleView(답글)
        lecyclerView = (RecyclerView) findViewById(R.id.friendrequest_listview);
        request_list = new ArrayList<friendrequest_data>();

/*        friendrequest_data album = new friendrequest_data();
        album.setName("요청");
        //album.setImage();
        request_list.add(album);

        adapter = new friendrequestAdapter(request_list);
        lecyclerView.setAdapter(adapter);
        lecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        lecyclerView.setItemAnimator(new DefaultItemAnimator());*/

        setFriendReq();
    }

    // 댓글 및 답글에 공용사용
    public class friendrequest_data {
        private int usernum;
        private Bitmap image;
        private String name;

        public friendrequest_data() {}

        public friendrequest_data(int usernum, Bitmap image, String name) {
            this.usernum = usernum;
            this.image = image;
            this.name = name;
        }

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

        public int getUsernum() {
            return usernum;
        }
        public void setUsernum(int usernum) {
            this.usernum = usernum;
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
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            final friendrequest_data item = friendrequest_list_data.get(position);
            viewHolder.name.setText(item.getName());
            viewHolder.img.setImageBitmap(item.getImage());

            viewHolder.okbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friendrequest_data data = request_list.get(position);
                    final String sql = "update friend set request = 0 where user_num = " + data.getUsernum();

                    new LoadingDialogBin(FriendRequestActivity.this) {
                        @Override
                        protected Void doInBackground(Void... params) {
                            JSONObject result = new WebControll().WebLoad(SQLDataService.getSQLJSONData(sql,0,"update"));
                            Log.d("test",result.toString());
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            viewHolder.delete();
                            super.onPostExecute(aVoid);
                        }
                    }.execute();

                }
            });

            viewHolder.cancelbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.delete();
                }
            });

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
            public Button okbutton;
            public Button cancelbutton;

            public ViewHolder(View itemView){
                super(itemView);

                img = (ImageView) itemView.findViewById(R.id.friendrequest_image);
                name = (TextView) itemView.findViewById(R.id.friendrequest_item);
                okbutton = (Button) itemView.findViewById(R.id.friendrequest_button1);
                cancelbutton = (Button) itemView.findViewById(R.id.friendrequest_button2);

            }
            public void delete() {
                int position = getAdapterPosition();
                request_list.remove(position);
                friendrequestAdapter.this.notifyItemRemoved(position);
            }
        }
    }
    /// ----------------- 여기까지 -----------------------

    private void setFriendReq() {
        String sql = "select user.user_num as user_num, name, profile " +
                "from user inner join friend " +
                "on user.user_num = friend.user_num " +
                "where request = 1 and friend_num = " + userInfo.getUserNum();
        request = SQLDataService.getSQLJSONData(sql,-1,"select");
        SQLDataService.putBundleValue(request, "download", "context", "profile");

        new LoadingDialogBin(this) {
            @Override
            protected Void doInBackground(Void... params) {
                JSONObject respone = new WebControll().WebLoad(request);
                try {
                    JSONArray resultArr = respone.getJSONArray("result");
                    for(int i = 0; i < resultArr.length(); i++) {
                        JSONObject data = resultArr.getJSONObject(i);
                        int user_num = data.getInt("user_num");
                        String name = data.getString("name");
                        Bitmap profile = setProfile(data);

                        Object[] objects = {user_num,profile,name};
                        publishProgress(objects);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                friendrequest_data temp = new friendrequest_data((int) values[0], (Bitmap) values[1], (String) values[2]);
                request_list.add(temp);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                adapter = new friendrequestAdapter(request_list);
                lecyclerView.setAdapter(adapter);
                lecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                lecyclerView.setItemAnimator(new DefaultItemAnimator(){

                });
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    public Bitmap setProfile(JSONObject data) throws JSONException {
        Bitmap bitmap = null;
        if(data.has("image")) {      // 있는지 확인
            JSONArray profileUri = data.getJSONArray("image");
            if (profileUri.length() != 0) {
                bitmap = ImageDownLoad.imageDownLoad(profileUri.getString(0));
            }
        }
        else {
            bitmap = ImageDownLoad.imageDownLoad(data.getString("profile"));
        }
        if (bitmap == null) {
            bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.basepicture, null)).getBitmap();
        }
        return bitmap;
    }


    // 뒤로가기
    public void Back(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(intent);
    }
}
