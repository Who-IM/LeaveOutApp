package whoim.leaveout.FCMPush;

import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Admin on 2017-07-20.
 */

public class FCMInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
    }

    public void sendRegistrationToServer(final String token) {
        // Add custom implementation, as needed.
        new Thread() {
            @Override
            public void run() {
                try {
                    final OkHttpClient client = new OkHttpClient();
                    RequestBody body = new FormBody.Builder()
                            .add("Token", token)
                            .build();

                    //request
                    final Request request = new Request.Builder()
                            .url("http://106.249.39.40:8080/FCMControll")
                            .post(body)
                            .build();


                    client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.run();
    }

}