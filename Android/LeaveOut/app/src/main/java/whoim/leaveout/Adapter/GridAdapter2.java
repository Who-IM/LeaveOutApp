package whoim.leaveout.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import whoim.leaveout.R;

public class GridAdapter2 extends BaseAdapter {

    private class GridViewHolder {
        public ImageView Image;
    }

    public class GridItem {
        public Bitmap Image;
    }

    private ArrayList<GridItem> mListData = new ArrayList();

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

    // 생성자로 값을 받아 셋팅
    public void addItem(Bitmap image) {
        GridItem addInfo = new GridItem();
        addInfo.Image = image;
        mListData.add(addInfo);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridViewHolder holder = null;

        if (convertView == null) {
            holder = new GridViewHolder();
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.public_view_article_image, parent, false);

            holder.Image = (ImageView) convertView.findViewById(R.id.public_view_article_grid_image);

            convertView.setTag(holder);
        }else{
            holder = (GridViewHolder) convertView.getTag();
        }

        GridItem mData = mListData.get(position);

        // 이미지 처리
        if (mData.Image != null) {
            holder.Image.setVisibility(View.VISIBLE);
            holder.Image.setImageBitmap(mData.Image);
        }else{
            holder.Image.setVisibility(View.GONE);
        }

        return convertView;
    }
}