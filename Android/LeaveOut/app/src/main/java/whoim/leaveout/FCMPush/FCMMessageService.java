package whoim.leaveout.FCMPush;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import whoim.leaveout.FriendRequestActivity;
import whoim.leaveout.MainActivity;
import whoim.leaveout.R;

/**
 * Created by Admin on 2017-07-20.
 */

public class FCMMessageService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    String title;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            title = URLDecoder.decode(remoteMessage.getData().get("title"), "UTF-8");
            if(title.equals("friendadd")) {
                String name = URLDecoder.decode(remoteMessage.getData().get("name"), "UTF-8");
                friendAddPush(name);
            }
            else {
                // 테스트용
                String msg = remoteMessage.getData().get("message");
                messagePush(URLDecoder.decode(msg, "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // 테스트 용
    private void messagePush(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("FCM Push Test")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle("FCM BIG")
                            .bigText(messageBody));

        sendNotification(notificationBuilder);
    }

    private void friendAddPush(String name) {
        Intent intent = new Intent(this, FriendRequestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("친구 요청!")
                .setContentText(name + "가 친구를 요청 하였습니다")
                .setContentIntent(pendingIntent);

        sendNotification(notificationBuilder);
    }



    private void sendNotification(NotificationCompat.Builder notificationBuilder) {

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
