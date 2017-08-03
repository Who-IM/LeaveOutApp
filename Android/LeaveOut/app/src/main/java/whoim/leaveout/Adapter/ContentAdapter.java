package whoim.leaveout.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import whoim.leaveout.FriendProfileActivity;
import whoim.leaveout.Loading.LoadingSQLDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.R;
import whoim.leaveout.Server.ImageDownLoad;
import whoim.leaveout.Server.SQLDataService;
import whoim.leaveout.User.UserInfo;

import static java.lang.Integer.parseInt;

/**
 * Created by Use on 2017-06-13.
 */

public class ContentAdapter extends BaseAdapter {

    public class ContentItem {
        public int contentnum;
        public Bitmap profile;
        public String name;
        public String email;
        public String location;
        public String time;
        public String recom_num;
        public String views_num;
        public String contents;
        public ArrayList<String> imagelist;
        public Bitmap mycommentprofile;
        public CommentAdapter commentAdapter;
    }

    private class ContentViewHolder {
        public ImageButton profile;
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
        public Button viewcomment_btn;
        public Button like_btn;
        public ImageButton declaration_btn;
    }

    private Context mContext;
    private ArrayList<ContentItem> mDataList = new ArrayList();

    private ArrayList<Button> comment_btn = new ArrayList<>();

    private HashMap<Integer, GridAdapter> mImageGridAdapterList = new HashMap();

    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance();

    public ContentAdapter(Context context) {
        mContext = context;
    }

    public void recycle() {
        for(int i = 0; i < mImageGridAdapterList.size(); i++) {
            if(mImageGridAdapterList.get(i) != null && mImageGridAdapterList.get(i).getListData() != null) {
                for (GridAdapter.GridItem gridItem : mImageGridAdapterList.get(i).getListData()) {
                    if (gridItem.Image != null) gridItem.Image.recycle();
                }
            }
        }
    }

    private Handler imagehandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mImageGridAdapterList.get(msg.what).addItem((Bitmap) msg.obj);
            mImageGridAdapterList.get(msg.what).notifyDataSetChanged();
        }
    };

    // 생성자로 값을 받아 셋팅
    public void addItem(Bitmap profile, int contentnum, String name, String location, String time, String recom_num, String views_num,
                        String contents, ArrayList<String> imagelist,Bitmap mycommentprofile, CommentAdapter commentAdapter, String email) {
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
        addInfo.mycommentprofile = mycommentprofile;
        addInfo.commentAdapter = commentAdapter;
        addInfo.email = email;
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

    public void removeall()
    {
/*        for(int i = 0; i < mDataList.size(); i++) {
             mDataList.remove(i);
        }*/
        mDataList.clear();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void findViews(ContentViewHolder holder, View convertView) {
        holder.profile = (ImageButton) convertView.findViewById(R.id.public_view_article_Image);
        holder.name = (TextView) convertView.findViewById(R.id.public_view_article_name);
        holder.declaration_btn = (ImageButton) convertView.findViewById(R.id.public_view_article_declaration);  //왜이럼???
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
        holder.viewcomment_btn = (Button) convertView.findViewById(R.id.public_view_article_comment_btn);
        holder.like_btn = (Button) convertView.findViewById(R.id.public_view_article_like_btn);
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

        final ContentItem mData = mDataList.get(position);      // 데이터 아이템 꺼내기

        // 프로필 이미지 처리 및 댓글 사진 처리
        if (mData.profile != null) {
            holder.profile.setVisibility(View.VISIBLE);           //  게시글 프로필 사진
            holder.profile.setImageBitmap(mData.profile);
            holder.mycomment.setVisibility(View.VISIBLE);       // 댓글 사진
            holder.mycomment.setImageBitmap(mData.mycommentprofile);
        }

        // 프로필 사진 클릭시
        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mData.email == null) return;     // 이메일이 없을 경우 사용 불가하기

                Intent intent = new Intent(mContext, FriendProfileActivity.class);
                intent.putExtra("name",mData.name);
                intent.putExtra("email",mData.email);
                intent.putExtra("contentnum",mData.contentnum);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);
            }
        });

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
                mImageGridAdapterList.put(position, new GridAdapter());
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
        if (mDataList.get(position).commentAdapter == null) {
            mDataList.get(position).commentAdapter = new CommentAdapter(mContext);     // 어뎁터 없을경우 생성 (재활용을 위해)
        }

        holder.commentlist.setAdapter(mDataList.get(position).commentAdapter);   // 어뎁터 등록
        setListViewHeightBasedOnChildren(holder.commentlist); // 리스트뷰 펼처보기(한화면에)

        holder.commentedit.setTag(position);
        final ContentViewHolder finalHolder = holder;

        //신고하기
        holder.declaration_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText etEdit = new EditText(mContext);
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("신고 내용을 입력하주세요");
                dialog.setView(etEdit);
                // OK 버튼 이벤트
                dialog.setPositiveButton("보내기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        declarationInsertSQLData(mData.contentnum, etEdit.getText().toString());
                        Toast.makeText(mContext, "전송되었습니다. 내용 : " + etEdit.getText(), Toast.LENGTH_LONG).show();
                    }
                });
                // Cancel 버튼 이벤트
                dialog.setNegativeButton("취소",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });

        // 댓글 쳐서 보여주기기
        holder.commentedit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    int pos = (int) view.getTag();  // 포지션값 받아오기

                    // 빈칸 입력시 입력 x
                    if (view.getText().toString().equals("") == false) {
                        commentInsertSQL(mDataList.get(pos).contentnum, view, finalHolder.commentlist);

                        int view_num = Integer.parseInt(mData.views_num);
                        viewnumInsertSQL(++view_num, mData.contentnum);
                        mData.views_num = view_num+"";

                        ContentAdapter.this.notifyDataSetChanged();

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

        // 댓글보기 클릭시
        holder.viewcomment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalHolder.commentlist.getCount() == 0) {
                    Toast.makeText(mContext, "입력된 댓글이 없습니다.", Toast.LENGTH_SHORT).show();
                }

                if(finalHolder.commentlist.getVisibility() == View.GONE) {
                    finalHolder.commentlist.setVisibility(View.VISIBLE);
                } else {
                    finalHolder.commentlist.setVisibility(View.GONE);
                }
                setListViewHeightBasedOnChildren(finalHolder.commentlist);
            }
        });

        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rec_cnt = parseInt(mData.recom_num);
                likeInsertSQL(++rec_cnt, mData.contentnum);
                mData.recom_num = rec_cnt+"";

                ContentAdapter.this.notifyDataSetChanged();
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
                Bitmap bitmap = ImageDownLoad.imageDownLoad(uri);
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
                        Toast.makeText(mContext, "등록 되었습니다.", Toast.LENGTH_SHORT).show();

                        int pos = (int) view.getTag();

                        if (responseData.get(1) != null) {
                            String time = responseData.get(1).getJSONArray("result").getJSONObject(0).getString("reg_time");
                            Bitmap profile = userInfo.getProfile();
                            if (profile == null) {
                                profile = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.basepicture, null)).getBitmap();
                            }
                            addComment(pos, mDataList.get(pos).contentnum, profile, userInfo.getName(), view.getText().toString(), time, userInfo.getUserNum());  // 데이터 셋팅
                            setListViewHeightBasedOnChildren(commentlist); // 리스트뷰 펼처보기(한화면에)
                            view.setText("");   // 내용 초기화
                        }
                    }
                }
                else {
                    Toast.makeText(mContext, "다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        LoadingSQLDialog.SQLSendStart(mContext, loadingSQLListener, ProgressDialog.STYLE_SPINNER, null);
    }

    // 댓글 입력 완료 했을시
    private void likeInsertSQL(final int rec_cnt, final int contentnum) {
        final String sql = "update content set rec_cnt = ? where content_num = ?";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(rec_cnt);
                mDataQueryGroup.addInt(contentnum);
                JSONObject data = SQLDataService.getDynamicSQLJSONData(sql, mDataQueryGroup, 0, "update");       // sql 셋팅
                return data;
            }

            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
               return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                if (responseData != null) {
                    if (!responseData.get(0).getString("result").equals("error")) {
                        Toast.makeText(mContext, "추천되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(mContext, "다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        LoadingSQLDialog.SQLSendStart(mContext, loadingSQLListener, ProgressDialog.STYLE_SPINNER, null);
    }

    // 댓글 입력 완료 했을시
    private void viewnumInsertSQL(final int view_cnt, final int contentnum) {
        final String sql = "update content set view_cnt = ? where content_num = ?";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(view_cnt);
                mDataQueryGroup.addInt(contentnum);
                JSONObject data = SQLDataService.getDynamicSQLJSONData(sql, mDataQueryGroup, 0, "update");       // sql 셋팅
                return data;
            }

            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
            }
        };
        LoadingSQLDialog.SQLSendStart(mContext, loadingSQLListener, ProgressDialog.STYLE_SPINNER, null);
    }

    // 신고하기 게시물 확인
    private void declarationInsertSQLData(final int contentnum, final String text) {

            final String sql = "Insert into declaration " +
                    "Values (?, ?, ?);";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }   //쿼리 겟수

            @Override
            public JSONObject getSQLQuery() {   //쿼리 처리
                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(contentnum);
                mDataQueryGroup.addInt(UserInfo.getInstance().getUserNum());
                mDataQueryGroup.addString(text);
                return SQLDataService.getDynamicSQLJSONData(sql,mDataQueryGroup,0,"update");
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {

            }
        };
        LoadingSQLDialog.SQLSendStart(mContext,loadingSQLListener, ProgressDialog.STYLE_SPINNER,null);
    }

    // 댓글 추가
    private void addComment(int pos, int contentnum, Bitmap bitmap, String name, String comment, String time, int user_num) {
        final CommentAdapter commentAdapter = mDataList.get(pos).commentAdapter;
        commentAdapter.addItem(contentnum, bitmap, name, comment, time, user_num);
        commentAdapter.notifyDataSetChanged();   // 데이터 변화시
    }


}
