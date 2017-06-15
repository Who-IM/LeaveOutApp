package whoim.leaveout.Services;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * 포맷 서비스
 */

public class FomatService {

    // 비트맵 String 으로 인코딩
    public static String getStringFromBitmap(Bitmap bitmapPicture) {
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.NO_WRAP);
        try { byteArrayBitmapStream.close(); } catch (IOException e) { e.printStackTrace();}
        return encodedImage;
    }

    // GPS를 주소로 변환
    public static String getCurrentAddress(Context context, Location location){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            return "지오코더 서비스 사용불가";
        }
        if (addresses == null || addresses.size() == 0) {
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return addressToken(address.getAddressLine(0).toString());
        }
    }

    // 주소 토큰
    private static String addressToken(String address) {
        String token1 = "대한민국 ";
        return address.substring(token1.length());
    }

}
