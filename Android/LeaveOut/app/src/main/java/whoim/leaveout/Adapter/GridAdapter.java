package whoim.leaveout.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import whoim.leaveout.R;

public class GridAdapter extends BaseAdapter {
    Context context;
    private ArrayList<grid_ListData> mListData = new ArrayList<grid_ListData>();


    public GridAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 생성자로 값을 받아 셋팅
    public void addItem(Drawable image) {
        grid_ListData addInfo = null;
        addInfo = new grid_ListData();
        addInfo.Image = image;

        mListData.add(addInfo);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        grid_ViewHolder holder = null;

        if (convertView == null) {
            holder = new grid_ViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.collect_view_image, null);

            holder.Image = (ImageView) convertView.findViewById(R.id.collect_grid_image);

            convertView.setTag(holder);
        }else{
            holder = (grid_ViewHolder) convertView.getTag();
        }

        final grid_ListData mData = mListData.get(position);

        // 이미지 처리
        if (mData.Image != null) {
            holder.Image.setVisibility(View.VISIBLE);
            holder.Image.setImageDrawable(mData.Image);
        }else{
            holder.Image.setVisibility(View.GONE);
        }

        return convertView;
    }

    // ------------ grid listview -------------
    private class grid_ViewHolder {
        public ImageView Image;
    }

    class grid_ListData {
        public Drawable Image;
    }
}