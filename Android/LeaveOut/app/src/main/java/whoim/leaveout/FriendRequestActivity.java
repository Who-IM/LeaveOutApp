package whoim.leaveout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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
        private String email;

        public friendrequest_data() {}

        public friendrequest_data(int usernum, Bitmap image, String name, String email) {
            this.usernum = usernum;
            this.image = image;
            this.name = name;
            this.email = email;
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

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
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

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.nextFriendProfile();
                }
            });

            viewHolder.okbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.AddFriend_OK();
                }
            });

            viewHolder.cancelbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.AddFriend_NO();
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

            public void nextFriendProfile() {
                final int position = getAdapterPosition();
                friendrequest_data data = request_list.get(position);
                Intent intent = new Intent(getApplicationContext(),FriendProfileActivity.class);
                intent.putExtra("user_num",data.getUsernum());
                intent.putExtra("name",data.getName());
                intent.putExtra("email",data.getEmail());
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }

            public void AddFriend_OK() {        // 친구 추가 수락
                final int position = getAdapterPosition();
                final String sql = "update friend set request = 0 where user_num = " + request_list.get(position).getUsernum();
                final String FriendAddSQL = "insert into friend values(" + userInfo.getUserNum() + "," + request_list.get(position).getUsernum() +",0) ON DUPLICATE KEY UPDATE request = 0";

                new LoadingDialogBin(FriendRequestActivity.this) {
                    @Override
                    protected Void doInBackground(Void... params) {
                        JSONObject result = new WebControll().WebLoad(SQLDataService.getSQLJSONData(sql,0,"update"));
                        Object[] objects = new Object[1];
                        try {
                            if(result.getInt("result") == 1) {      // 요청한 한 데이터베이스 수정 완료(친구 요청 수락 완료)
                                JSONObject result2 = new WebControll().WebLoad(SQLDataService.getSQLJSONData(FriendAddSQL,0,"update"));
                                if(result2.getInt("result") == 1) {     // 내 자신에도 친구 목록 추가(친구 수락 한뒤 자신에도 친구 DB 추가)
                                    final OkHttpClient client = new OkHttpClient();
                                    RequestBody body = new FormBody.Builder()
                                            .add("requestdata", "AddFriend_OK")
                                            .add("user_num", String.valueOf(request_list.get(position).getUsernum()))
                                            .add("friend_num", String.valueOf(userInfo.getUserNum()))
                                            .build();

                                    //request
                                    final Request request = new Request.Builder()
                                            .url(WebControll.WEB_IP + "/FCMPush")
                                            .post(body)
                                            .build();

                                    client.newCall(request).enqueue(new Callback() {        // 서버로 보내기
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                        }
                                    });
                                }
                                objects[0] = true;
                                publishProgress(objects);
                            }   // if -- end
                            else {
                                objects[0] = false;
                                publishProgress(objects);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(Object... values) {
                        if(((boolean)values[0]) == true) {
                            Toast.makeText(getApplicationContext(),"수락 하였습니다",Toast.LENGTH_SHORT).show();
                            request_list.remove(position);
                            friendrequestAdapter.this.notifyItemRemoved(position);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"잠시후 다시 시도해 주십시오.",Toast.LENGTH_SHORT).show();
                        }

                    }
                }.execute();
            }

            public void AddFriend_NO() {        // 친구 추가 거절
                final int position = getAdapterPosition();
                final String sql = "delete from friend where user_num = " + request_list.get(position).getUsernum();
                new LoadingDialogBin(FriendRequestActivity.this) {
                    @Override
                    protected Void doInBackground(Void... params) {
                        JSONObject result = new WebControll().WebLoad(SQLDataService.getSQLJSONData(sql,0,"update"));
                        Object[] objects = new Object[1];
                        try {
                            if(result.getInt("result") == 1) {
                                final OkHttpClient client = new OkHttpClient();
                                RequestBody body = new FormBody.Builder()
                                        .add("requestdata","AddFriend_NO")
                                        .add("user_num", String.valueOf(request_list.get(position).getUsernum()))
                                        .add("friend_num",String.valueOf(userInfo.getUserNum()))
                                        .build();

                                //request
                                final Request request = new Request.Builder()
                                        .url(WebControll.WEB_IP + "/FCMPush")
                                        .post(body)
                                        .build();

                                client.newCall(request).enqueue(new Callback() {        // 서버로 보내기
                                    @Override
                                    public void onFailure(Call call, IOException e) { }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException { }
                                });
                                objects[0] = true;
                                publishProgress(objects);
                            }   // if -- end
                            else {
                                objects[0] = false;
                                publishProgress(objects);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(Object... values) {
                        if(((boolean)values[0]) == true) {
                            Toast.makeText(getApplicationContext(),"거절 하였습니다",Toast.LENGTH_SHORT).show();
                            request_list.remove(position);
                            friendrequestAdapter.this.notifyItemRemoved(position);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"잠시후 다시 시도해 주십시오.",Toast.LENGTH_SHORT).show();
                        }

                    }
                }.execute();
            }

        }
    }
    /// ----------------- 여기까지 -----------------------

    private void setFriendReq() {
        String sql = "select user.user_num as user_num, name, email, profile " +
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
                        String email = data.getString("email");
                        Bitmap profile = setProfile(data);

                        Object[] objects = {user_num,profile,name,email};
                        publishProgress(objects);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                friendrequest_data temp = new friendrequest_data((int) values[0], (Bitmap) values[1], (String) values[2], (String) values[3]);
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
