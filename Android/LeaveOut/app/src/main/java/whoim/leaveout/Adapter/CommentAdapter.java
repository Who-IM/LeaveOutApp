package whoim.leaveout.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import whoim.leaveout.CommentActivity;
import whoim.leaveout.Loading.LoadingSQLDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.R;
import whoim.leaveout.Server.SQLDataService;

/**
 * Created by SeongMun on 2017-06-13.
 */

public class CommentAdapter extends BaseAdapter {

    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance();
    Context mContext = null;
    int comm_num;

    public CommentAdapter(Context context) {
        mContext = context;
    }

    public class CommentListData {
        public int content_num;
        public Bitmap profile;
        public String name;
        public String comment;
        public String time;
        public int user_num;
    }

    private class CommentViewHolder {
        public ImageView profileview;
        public TextView nameview;
        public TextView commentview;
        public TextView timeview;
    }

    private ArrayList<CommentListData> mListData = new ArrayList<CommentListData>();

    // 생성자로 값을 받아 셋팅
    public void addItem(int contentnum ,Bitmap profile, String name, String comment, String time, int user_num) {
        CommentListData addInfo = new CommentListData();
        addInfo.content_num = contentnum;
        addInfo.profile = profile;
        addInfo.name = name;
        addInfo.comment = comment;
        addInfo.time = time;
        addInfo.user_num = user_num;

        mListData.add(addInfo);
    }

    public ArrayList<CommentListData> getListData() {
        return mListData;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final CommentViewHolder holder;
        if (convertView == null) {
            holder = new CommentViewHolder();
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.public_view_article_comment,parent, false);

            holder.profileview = (ImageView) convertView.findViewById(R.id.public_view_article_comment_image);
            holder.nameview = (TextView) convertView.findViewById(R.id.public_view_article_comment_name);
            holder.commentview = (TextView) convertView.findViewById(R.id.public_view_article_comment_text);
            holder.timeview = (TextView) convertView.findViewById(R.id.public_view_article_comment_time);

            convertView.setTag(holder);
        }else{
            holder = (CommentViewHolder) convertView.getTag();
        }

        final CommentListData data = mListData.get(position);

        // 이미지 처리
        if (data.profile != null) {
            holder.profileview.setVisibility(View.VISIBLE);
            holder.profileview.setImageBitmap(data.profile);
        }else{
            holder.profileview.setVisibility(View.GONE);
        }

        // textView 처리
        holder.nameview.setText(data.name);
        holder.commentview.setText(data.comment);
        holder.timeview.setText(data.time);

        Button btn = (Button) convertView.findViewById(R.id.public_view_article_comment_btn2);
        btn.setTag(position);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                CommentListData data = mListData.get(pos);
                select_Comment_num(data.user_num, data.time, data.content_num);
            }
        });

        return convertView;
    }

    private void select_Comment_num(final int user_num, final String time, final int content_num) {

        final String sql = "select comm_num from comment " +
                           "where user_num = ? AND reg_time = ?";

        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public JSONObject getSQLQuery() {
                mDataQueryGroup.clear();
                mDataQueryGroup.addInt(user_num);
                mDataQueryGroup.addString(time);
                return SQLDataService.getDynamicSQLJSONData(sql,mDataQueryGroup,-1,"select");
            }
            @Override
            public JSONObject getUpLoad(JSONObject resultSQL) {
                return null;
            }

            @Override
            public void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException {
                JSONArray jspn = responseData.get(0).getJSONArray("result");
                for(int i =0; i < jspn.length(); i++) {
                    JSONObject j = jspn.getJSONObject(i);
                    comm_num = j.getInt("comm_num");
                }

                Intent recomment = new Intent(mContext.getApplicationContext(), CommentActivity.class);
                recomment.putExtra("content_num", content_num);
                recomment.putExtra("comm_num", comm_num);
                recomment.putExtra("user_num", user_num);
                mContext.startActivity(recomment);
            }
        };
        LoadingSQLDialog.SQLSendStart(this.mContext, loadingSQLListener, ProgressDialog.STYLE_SPINNER,null);
    }
}
