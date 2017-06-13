package whoim.leaveout.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import whoim.leaveout.R;
import whoim.leaveout.Server.ImageDownLoad2;

/**
 * Created by Use on 2017-06-13.
 */

public class ContentAdapter extends BaseAdapter {

    public class ContentItem {
        public int content;
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
        int index;
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
        public GridView contentimage;

    }

    private ArrayList<ContentItem> mDataList = new ArrayList();
    private ArrayList<GridAdapter2> imageGridList = new ArrayList();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            imageGridList.get(msg.what).addItem((Bitmap) msg.obj);
            imageGridList.get(msg.what).notifyDataSetChanged();
        }
    };

    // 생성자로 값을 받아 셋팅
    public void addItem(Bitmap image, int content, String name, String location, String time, String recom_num, String views_num, String contents, ArrayList<String> imagelist) {
        ContentItem addInfo = new ContentItem();
        addInfo.content = content;
        addInfo.profile = image;
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
        holder.contentimage = (GridView) convertView.findViewById(R.id.public_view_article_grid);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContentViewHolder holder = null;
        // 글쓰기 이미지
        ContentItem mData = mDataList.get(position);

        if(convertView == null) {
            holder = new ContentViewHolder();
            holder.index = position;
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.public_view_article, parent, false);
            findViews(holder,convertView);

            convertView.setTag(holder);
        }
        else {
            holder = (ContentViewHolder) convertView.getTag();
        }

        // 이미지 처리
        if (mData.profile != null) {
            holder.profile.setVisibility(View.VISIBLE);           //  게시글 프로필 사진
            holder.profile.setImageBitmap(mData.profile);
            holder.mycomment.setVisibility(View.VISIBLE);       // 댓글 사진
            holder.mycomment.setImageBitmap(mData.profile);
        }else{
            holder.mycomment.setVisibility(View.GONE);
            holder.profile.setVisibility(View.GONE);
        }

        // textView 처리
        holder.name.setText(mData.name);
        holder.location.setText(mData.location);
        holder.time.setText(mData.time);
        holder.recom_num.setText(mData.recom_num);
        holder.views_num.setText(mData.views_num);
        holder.contents.setText(mData.contents);


        if(mData.imagelist.size() != 0 && holder.index == position) {
            imageGridList.add(new GridAdapter2());
            holder.contentimage.setAdapter(imageGridList.get(position));
            for(String uri : mData.imagelist) {
                contetntImageDownLoad(uri,position);
            }
        }
        else {
            imageGridList.add(null);
        }


        return convertView;
    }

    private void contetntImageDownLoad(final String uri, final int position) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = ImageDownLoad2.imageDownLoad(uri);
                Message message = Message.obtain(handler,position,bitmap);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
}
