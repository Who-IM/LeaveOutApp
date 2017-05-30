package whoim.leaveout.CustomFont;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * 시작 전 커스텀 하기 (폰트)
 */
public class CustomStartApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Typekit.getInstance().addCustom1(Typekit.createFromAsset(this, "BMJUA_ttf.ttf"))
                             .addCustom2(Typekit.createFromAsset(this, "GodoB.ttf"))
                             .addCustom3(Typekit.createFromAsset(this, "GodoM.ttf"));
    }
}
