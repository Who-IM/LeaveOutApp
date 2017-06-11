package whoim.leaveout.Server;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SeongMun on 2017-06-11.
 */

public class ImageDownLoad extends AsyncTask<String,Void,Bitmap> {
    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            URL url = new URL(params[0]); // URL 주소를 이용해서 URL 객체 생성

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(3000);  // 접속 제한시간
            conn.setReadTimeout(5000);     // 입력스트림 읽어오는 제한시간
            conn.setDoInput(true);
            conn.connect();

            is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);

        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }
}
