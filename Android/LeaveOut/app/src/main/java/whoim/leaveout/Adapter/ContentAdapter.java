package whoim.leaveout.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import whoim.leaveout.Loading.LoadingSQLDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.R;
import whoim.leaveout.Server.ImageDownLoad2;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.User.UserInfo;

/**
 * Created by Use on 2017-06-13.
 */

public class ContentAdapter extends BaseAdapter {

    public class ContentItem {
        public int contentnum;
        public Bitmap profile;
        public String name;
        public String location;
        public String time;
        public String recom_num;
        public String views_num;
        public String contents;
        public ArrayList<String> imagelist;
    }

    private class ContentViewHolder {
        public ImageView profile;
        public TextView name;
        public TextView location;
        public TextView time;
        public TextView recom_num;
        public TextView views_num;
        public TextView contents;
        public ImageView mycomment;
        public Button commentbtn;
        public Button viewlikebtn;
        public GridView contentimagegrid;
        public ListView commentlist;
        public EditText commentedit;
    }

    private Context mContext;
    private ArrayList<ContentItem> mDataList = new ArrayList();
    private HashMap<Integer, GridAdapter2> mImageGridAdapterList = new HashMap();
    private HashMap<Integer, CommentAdapter> mCommentAdapterList = new HashMap();

    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance();

    public ContentAdapter(Context context) {
        mContext = context;
    }

    private Handler imagehandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mImageGridAdapterList.get(msg.what).addItem((Bitmap) msg.obj);
            mImageGridAdapterList.get(msg.what).notifyDataSetChanged();
        }
    };

    // 생성자로 값을 받아 셋팅
    public void addItem(Bitmap profile, int contentnum, String name, String location, String time, String recom_num, String views_num, String contents, ArrayList<String> imagelist) {
        ContentItem addInfo = new ContentItem();
        addInfo.contentnum = contentnum;
        addInfo.profile = profile;
        addInfo.name = name;
        addInfo.location = location;
        addInfo.time = time;
        addInfo.recom_num = recom_num;
        addInfo.views_num = views_num;
        addInfo.contents = contents;
        addInfo.imagelist = imagelist;
        mDataList.add(addInfo);
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void findViews(ContentViewHolder holder, View convertView) {
        holder.profile = (ImageView) convertView.findViewById(R.id.public_view_article_Image);
        holder.name = (TextView) convertView.findViewById(R.id.public_view_article_name);
        holder.location = (TextView) convertView.findViewById(R.id.public_view_article_location);
        holder.time = (TextView) convertView.findViewById(R.id.public_view_article_time);
        holder.recom_num = (TextView) convertView.findViewById(R.id.public_view_article_recom_num);
        holder.views_num = (TextView) convertView.findViewById(R.id.public_view_article_views_num);
        holder.contents = (TextView) convertView.findViewById(R.id.public_view_article_contents);
        holder.mycomment = (ImageView) convertView.findViewById(R.id.public_view_article_mycomment_image);
        holder.commentbtn = (Button) convertView.findViewById(R.id.public_view_article_comment_btn);
        holder.viewlikebtn = (Button) convertView.findViewById(R.id.public_view_article_like_btn);
        holder.contentimagegrid = (GridView) convertView.findViewById(R.id.public_view_article_grid);
        holder.commentlist = (ListView) convertView.findViewById(R.id.public_view_article_comment_list);
        holder.commentedit = (EditText) convertView.findViewById(R.id.public_view_article_comment_editText);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ContentViewHolder holder = null;
        // 글쓰기 이미지

        if (convertView == null) {
            holder = new ContentViewHolder();
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.public_view_article, parent, false);
            findViews(holder, convertView);

            convertView.setTag(holder);
        } else {
            holder = (ContentViewHolder) convertView.getTag();
        }

        final ContentItem mData = mDataList.get(position);
        // 프로필 이미지 처리 및 댓글 사진 처리
        if (mData.profile != null) {
            holder.profile.setVisibility(View.VISIBLE);           //  게시글 프로필 사진
            holder.profile.setImageBitmap(mData.profile);
            holder.mycomment.setVisibility(View.VISIBLE);       // 댓글 사진
            holder.mycomment.setImageBitmap(mData.profile);
        } else {
            holder.mycomment.setVisibility(View.GONE);
            holder.profile.setVisibility(View.GONE);
        }

        // textView 처리(이름 , 주소, 게시글내용,조회수 및 추천수)
        holder.name.setText(mData.name);
        holder.location.setText(mData.location);
        holder.time.setText(mData.time);
        holder.recom_num.setText(mData.recom_num);
        holder.views_num.setText(mData.views_num);
        holder.contents.setText(mData.contents);


        // 게시글 이미지 보여주기
        if (mData.imagelist.size() != 0) {
            if (mImageGridAdapterList.get(position) == null)
                mImageGridAdapterList.put(position, new GridAdapter2());
            holder.contentimagegrid.setAdapter(mImageGridAdapterList.get(position));
            holder.contentimagegrid.setVisibility(View.VISIBLE);
            if (mImageGridAdapterList.get(position).getCount() != mData.imagelist.size()) {
                for (String uri : mData.imagelist) {
                    contetntImageDownLoad(uri, position);
                }
            }
        } else if (mData.imagelist.size() == 0) {
            mImageGridAdapterList.put(position, null);
            holder.contentimagegrid.setVisibility(View.GONE);
        }
        holder.contentimagegrid.setAdapter(mImageGridAdapterList.get(position));


        // 댓글 부분
        if (mCommentAdapterList.get(position) == null) {
            mCommentAdapterList.put(position, new CommentAdapter());     // 어뎁터 없을경우 생성 (재활용을 위해)
            commentselectSQL(mData.contentnum,position,holder.commentlist);
        }
        holder.commentlist.setAdapter(mCommentAdapterList.get(position));   // 어뎁터 등록
        setListViewHeightBasedOnChildren(holder.commentlist); // 리스트뷰 펼처보기(한화면에)


        holder.commentedit.setTag(position);
        final ContentViewHolder finalHolder = holder;

        // 댓글 쳐서 보여주기기
        holder.commentedit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    int pos = (int) view.getTag();  // 포지션값 받아오기

                    // 빈칸 입력시 입력 x
                    if (view.getText().toString().equals("") == false) {
                        commentInsertSQL(mDataList.get(pos).contentnum, view, finalHolder.commentlist);
                        // 입력했는데 감춰져있으면 보이게 셋팅
                        if (finalHolder.commentlist.getVisibility() == View.GONE) {
                            finalHolder.commentlist.setVisibility(View.VISIBLE);
                        }

                    }
                    return true;
                }
                return false;
            }
        });
        return convertView;
    }

    // 리스트뷰 펼처보기(한화면에)
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    // 게시글 이미지
    private void contetntImageDownLoad(final String uri, final int position) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = ImageDownLoad2.imageDownLoad(uri);
                Message message = Message.obtain(imagehandler, position, bitmap);
                imagehandler.sendMessage(message);
            }
        };
        thread.start();
    }


    // 댓글 입력 완료 했을시
    private void commentInsertSQL(final int contentnum, final TextView view, final ListView commentlist) {
        final String sql = "insert into comment(content_num,user_num,reg_time) values(?,?,now())";
        final UserInfo userInfo = UserInfo.getInstance();

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 2;
            }

            @Override
            public JSONObject getSQLQuery() {
                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(contentnum);
                mDataQueryGroup.addInt(userInfo.getUserNum());          // 유저 번호
                JSONObject data = SQLDataService.getDynamicSQLJSONData(sql, mDataQueryGroup, 0, "update");       // sql 셋팅
                SQLDataService.putBundleValue(data, "upload", "usernum", userInfo.getUserNum());                 // 번들 데이터 더 추가(유저 id)
                SQLDataService.putBundleValue(data, "upload", "contentnum", contentnum);                 // 번들 데이터 더 추가(유저 id)
                SQLDataService.putBundleValue(data, "upload", "path", "comment");
                SQLDataService.putBundleValue(data, "upload", "context", "text");
                SQLDataService.putBundleValue(data, "upload", "text", view.getText().toString());        // 번들 데이터 더 추가(내용)
                return data;
            }

            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                String sql = "select reg_time " +
                        "from comment " +
                        "where user_num = " + userInfo.getUserNum() + " and content_num = " + contentnum +
                        " order by comm_num desc " +
                        "Limit 1";
                return SQLDataService.getSQLJSONData(sql, -1, "select");
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                if (responseData != null) {
                    if (!responseData.get(0).getString("result").equals("error")) {
                        Toast.makeText(mContext, "good", Toast.LENGTH_SHORT).show();
                        int pos = (int) view.getTag();

                        if (responseData.get(1) != null) {
                            String time = responseData.get(1).getJSONArray("result").getJSONObject(0).getString("reg_time");
                            Bitmap profile = userInfo.getProfile();
                            if (profile == null) {
                                profile = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.basepicture, null)).getBitmap();
                            }
                            addComment(pos, mDataList.get(pos).contentnum, profile, userInfo.getName(), view.getText().toString(), time);  // 데이터 셋팅
                            setListViewHeightBasedOnChildren(commentlist); // 리스트뷰 펼처보기(한화면에)
                            view.setText("");   // 내용 초기화
                        }
                    }
                }
            }
        };
        LoadingSQLDialog.SQLSendStart(mContext, loadingSQLListener, ProgressDialog.STYLE_SPINNER, null);
    }

    private final Handler commentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                JSONObject result = new JSONObject(msg.getData().getString("result"));
                Log.d("dd",result.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    // 올라온 댓글 가져오기
    private void commentselectSQL(final int contentnum, final int position, final ListView commentlist) {
        final String sql = "select comm_num, rec_cnt, reg_time, files, name, profile " +
                "from comment join user on comment.user_num = user.user_num " +
                "where content_num = " + contentnum;


/*        Thread thread = new Thread() {
            @Override
            public void run() {
                JSONObject data = SQLDataService.getSQLJSONData(sql, -1, "select");
                SQLDataService.putBundleValue(data, "download", "context", "files");
                SQLDataService.putBundleValue(data, "download", "context2", "profile");
                JSONObject result = test(data);

                try {
                    JSONArray array = result.getJSONArray("result");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject resultdata = result.getJSONArray("result").getJSONObject(i);
                        Bitmap profile = null;

                        JSONArray profileUri = resultdata.getJSONArray("image");
                        if (profileUri.length() != 0) {
                            profile = ImageDownLoad2.imageDownLoad(profileUri.getString(0));
                        }
                        if (profile == null) {
                            profile = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.basepicture, null)).getBitmap();
                        }
                        addComment(position, contentnum, profile, resultdata.getString("name"), resultdata.getString("text"), resultdata.getString("reg_time"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
                    thread.start();
        };*/
                AsyncTask<Void,Object,Void> AsyncTask = new AsyncTask<Void, Object, Void>() {

                    Bitmap profile = null;
                    JSONObject AsyncTaskresult;

                    @Override
                    protected Void doInBackground(Void... params) {
                        JSONObject data = SQLDataService.getSQLJSONData(sql, -1, "select");
                        SQLDataService.putBundleValue(data, "download", "context", "files");
                        SQLDataService.putBundleValue(data, "download", "context2", "profile");
                        AsyncTaskresult = test(data);

                        try {
                            JSONArray array = AsyncTaskresult.getJSONArray("result");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject resultdata = AsyncTaskresult.getJSONArray("result").getJSONObject(i);

                                JSONArray profileUri = resultdata.getJSONArray("image");
                                if (profileUri.length() != 0) {
                                    profile = ImageDownLoad2.imageDownLoad(profileUri.getString(0));
                                }
                                if (profile == null) {
                                    profile = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.basepicture, null)).getBitmap();
                                }
                                Object[] objects = new Object[4];
                                objects[0] = profile;
                                objects[1] = resultdata.getString("name");
                                objects[2] = resultdata.getString("text");
                                objects[3] = resultdata.getString("reg_time");
                                publishProgress(objects);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(Object... values) {
                        addComment(position, contentnum, (Bitmap) values[0], (String) values[1],(String) values[2],(String) values[3]);
                    }
                };
                AsyncTask.execute();



/*                Message message = Message.obtain(commentHandler,position,commentlist);
                Bundle bundle = new Bundle();
                bundle.putString("result",result.toString());
                message.setData(bundle);
                commentHandler.sendMessage(message);*/



    }


    private JSONObject test(JSONObject request) {
        HttpURLConnection mCon = null;
        BufferedWriter mBufferedWriter = null;
        BufferedReader mBufferedReader = null;
        try {
            URL url = new URL("http://192.168.35.145:8080/controll"); // URL화 한다.
            mCon = (HttpURLConnection) url.openConnection();                 // 접속 객체 생성
//            mCon.setRequestProperty("Content-Type", "application/json");      // 타입설정(application/json) 형식으로 전송
            mCon.setRequestProperty("Content-Type", "text/html");               // 타입설
            mCon.setConnectTimeout(10000);  // 접속 제한시간
            mCon.setReadTimeout(10000);     // 입력스트림 읽어오는 제한시간
            mCon.setRequestMethod("POST");  // POST방식 통신
            mCon.setDoOutput(true);         // 쓰기모드 지정
            mCon.setDoInput(true);          // 읽기모드 지정

            mBufferedWriter = new BufferedWriter(new OutputStreamWriter(mCon.getOutputStream(), "utf-8"));       // 접속한 출력 스트림 생성
            mBufferedWriter.write(request.toString());        // 여기서 각 필요한 데이터 보내기
            mBufferedWriter.flush();        // 보내기

            mBufferedReader = new BufferedReader(new InputStreamReader(mCon.getInputStream(), "utf-8"));       // 접속한 입력 스트림 생성
            StringBuilder sb = new StringBuilder();         // 스트링빌더 생성
            String json;        // 스트림으로 꺼낸것을 임시 저장
            while ((json = mBufferedReader.readLine()) != null) {        // 스트림 뽑아내기
                sb.append(json + "\n");
            }

            return new JSONObject(sb.toString());       // 뽑아낸것을 제이슨으로 객체로 만들어 리턴

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (mBufferedWriter != null) mBufferedWriter.close();
                if (mBufferedReader != null) mBufferedReader.close();
                if (mCon != null) mCon.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 댓글 추가
    private void addComment(int pos, int contentnum, Bitmap bitmap, String name, String comment, String time) {
        final CommentAdapter commentAdapter = mCommentAdapterList.get(pos);
        commentAdapter.addItem(contentnum, bitmap, name, comment, time);
        commentAdapter.notifyDataSetChanged();   // 데이터 변화시
    }


}
