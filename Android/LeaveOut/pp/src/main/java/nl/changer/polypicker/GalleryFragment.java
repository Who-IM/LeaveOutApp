package nl.changer.polypicker;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;

import nl.changer.polypicker.model.Image;


/**
 * Created by Gil on 04/03/2014.
 */
public class GalleryFragment extends Fragment {

    private static final String TAG = GalleryFragment.class.getSimpleName();

    private ImageGalleryAdapter mGalleryAdapter;
    private ImagePickerActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pp__fragment_gallery, container, false);

        mGalleryAdapter = new ImageGalleryAdapter(getActivity());
        GridView galleryGridView = (GridView) rootView.findViewById(R.id.pp__gallery_grid);
        mActivity = ((ImagePickerActivity) getActivity());

        Cursor imageCursor = null;
        try {
        	final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION};
            final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
            imageCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
            while (imageCursor.moveToNext()) {
                Uri uri = Uri.parse(imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                int orientation = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
                mGalleryAdapter.add(new Image(uri, orientation));
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(imageCursor != null && !imageCursor.isClosed()) {
				imageCursor.close();	
			}	
		}

        galleryGridView.setAdapter(mGalleryAdapter);
        galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Image image = mGalleryAdapter.getItem(i);
                if (!mActivity.containsImage(image)) {
                    mActivity.addImage(image);
                } else {
                    mActivity.removeImage(image);
                }

                // refresh the view to
                // mGalleryAdapter.getView(i, view, adapterView);
                mGalleryAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }


    // 사진 위치정보 저장할 클레스
    public class location_class {
        Float latitude;
        Float longitude;
    }

    // 이미지 gps 경로
    public class GeoDegree {
        private boolean valid = false;
        location_class lo_class;

        public GeoDegree(ExifInterface exif) {
            lo_class = new location_class();
            setLocation(exif);
        };

        // 이미지 위치정보 셋팅
        public void setLocation(ExifInterface exif) {
            String attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String attrLATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String attrLONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

            if ((attrLATITUDE != null) && (attrLATITUDE_REF != null) && (attrLONGITUDE != null) && (attrLONGITUDE_REF != null)) {
                valid = true;

                if (attrLATITUDE_REF.equals("N")) {
                    lo_class.latitude = convertToDegree(attrLATITUDE);
                } else {
                    lo_class.latitude = 0 - convertToDegree(attrLATITUDE);
                }

                if (attrLONGITUDE_REF.equals("E")) {
                    lo_class.longitude = convertToDegree(attrLONGITUDE);
                } else {
                    lo_class.longitude = 0 - convertToDegree(attrLONGITUDE);
                }
            }
        }

        private Float convertToDegree(String stringDMS) {
            Float result = null;
            String[] DMS = stringDMS.split(",", 3);

            String[] stringD = DMS[0].split("/", 2);
            Double D0 = new Double(stringD[0]);
            Double D1 = new Double(stringD[1]);
            Double FloatD = D0 / D1;

            String[] stringM = DMS[1].split("/", 2);
            Double M0 = new Double(stringM[0]);
            Double M1 = new Double(stringM[1]);
            Double FloatM = M0 / M1;

            String[] stringS = DMS[2].split("/", 2);
            Double S0 = new Double(stringS[0]);
            Double S1 = new Double(stringS[1]);
            Double FloatS = S0 / S1;
            result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));
            return result;
        };

        public boolean isValid() {
            return valid;
        }
    }


    class ViewHolder {
        ImageView mThumbnail;
        ImageView location;
        // This is like storing too much data in memory.
        // find a better way to handle this
        Image mImage;
    }

    public class ImageGalleryAdapter extends ArrayAdapter<Image> {

        public ImageGalleryAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.pp__grid_item_gallery_thumbnail, null);
                holder = new ViewHolder();
                holder.mThumbnail = (ImageView) convertView.findViewById(R.id.pp__thumbnail_image);
                holder.location = (ImageView) convertView.findViewById(R.id.pp__thumbnail_location_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Image image = getItem(position);
            boolean isSelected = mActivity.containsImage(image);

            ((FrameLayout) convertView).setForeground(isSelected ? getResources().getDrawable(R.drawable.gallery_photo_selected) : null);

            if (holder.mImage == null || !holder.mImage.equals(image)) {
                mActivity.mImageFetcher.loadImage(image.mUri, holder.mThumbnail, image.mOrientation);
                holder.mImage = image;
            }

            try {
                ExifInterface exif = new ExifInterface(image.mUri.getPath());
                GeoDegree geoDegree = new GeoDegree(exif);

                if (geoDegree.isValid()) {
                    holder.location.setVisibility(View.VISIBLE);
                }
                else {
                    holder.location.setVisibility(View.GONE);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }
}