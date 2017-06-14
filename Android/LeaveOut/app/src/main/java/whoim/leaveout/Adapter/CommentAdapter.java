package whoim.leaveout.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import whoim.leaveout.R;

/**
 * Created by SeongMun on 2017-06-13.
 */

public class CommentAdapter extends BaseAdapter {

    public class CommentListData {
        public int content_num;
        public Bitmap profile;
        public String name;
        public String comment;
        public String time;
    }

    private class CommentViewHolder {
        public ImageView profileview;
        public TextView nameview;
        public TextView commentview;
        public TextView timeview;
    }

    private ArrayList<CommentListData> mListData = new ArrayList<CommentListData>();

    // 생성자로 값을 받아 셋팅
    public void addItem(int contentnum ,Bitmap profile, String name, String comment, String time) {
        CommentListData addInfo = new CommentListData();
        addInfo.content_num = contentnum;
        addInfo.profile = profile;
        addInfo.name = name;
        addInfo.comment = comment;
        addInfo.time = time;

        mListData.add(addInfo);
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

        CommentViewHolder holder;
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

        CommentListData data = mListData.get(position);

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

        int totalHeight = 0;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < this.getCount(); i++) {
            convertView.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += convertView.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = parent.getLayoutParams();

        params.height = totalHeight;
        parent.setLayoutParams(params);


        return convertView;
    }
}
