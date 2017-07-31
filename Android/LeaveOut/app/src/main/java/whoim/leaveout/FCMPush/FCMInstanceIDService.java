package whoim.leaveout.FCMPush;

import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import whoim.leaveout.Server.WebControll;

/**
 * Created by Admin on 2017-07-20.
 */

public class FCMInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
    }

    public static void sendRegistrationToServer(String usernum, String token) {
        // Add custom implementation, as needed.
        final OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("user_num",usernum)
                .add("Token", token)
                .build();

        //request
        final Request request = new Request.Builder()
                .url(WebControll.WEB_IP+"/FCMControll")
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

}