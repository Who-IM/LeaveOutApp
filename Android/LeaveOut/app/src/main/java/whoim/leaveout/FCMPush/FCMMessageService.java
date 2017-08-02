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
import whoim.leaveout.Services.ApplicationPackageRetriever;
import whoim.leaveout.loginActivity;

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
            if(title.equals("friendadd")) {     // 친구 추가 알림
                String name = URLDecoder.decode(remoteMessage.getData().get("name"), "UTF-8");
                friendAddPush(name);
            }
            else if(title.equals("friendadd_ok")) {
                String name = URLDecoder.decode(remoteMessage.getData().get("name"), "UTF-8");
                friendAdd_OkPush(name);
            }
            else if(title.equals("friendadd_no")) {
                String name = URLDecoder.decode(remoteMessage.getData().get("name"), "UTF-8");
                friendAdd_NoPush(name);
            }
            else {  // 테스트용
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

    // 친구 추가 요청 푸시
    private void friendAddPush(String name) {
        Intent intent = new Intent();
        ApplicationPackageRetriever applicationPackageRetriever = new ApplicationPackageRetriever(this);    // 앱 켜져있는지 확인
        String[] PackageString = applicationPackageRetriever.get();
        if(PackageString.length > 0 && PackageString[0].equals("whoim.leaveout") && loginActivity.LOGIN_CHECK == true) {       // 앱이 켜져있고 로그인 완료 했을 시
            intent.setClass(this, FriendRequestActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        else {      // 앱이 꺼져있으면
            intent.setClass(this, loginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("moveAction","FriendRequestActivity");      // 친구 요청 목록 까지 보내기
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("친구 요청!")
                .setContentText(name + "가 친구를 요청 하였습니다")
                .setContentIntent(pendingIntent);

        sendNotification(notificationBuilder);
    }

    // 친구 수락 요청 푸시
    private void friendAdd_OkPush(String name) {
        Intent intent = new Intent();
        ApplicationPackageRetriever applicationPackageRetriever = new ApplicationPackageRetriever(this);    // 앱 켜져있는지 확인
        String[] PackageString = applicationPackageRetriever.get();
        if(PackageString.length > 0 && PackageString[0].equals("whoim.leaveout") && loginActivity.LOGIN_CHECK == true) {       // 앱이 켜져있고 로그인 완료 했을 시
            intent.setClass(this, FriendRequestActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        else {      // 앱이 꺼져있으면
            intent.setClass(this, loginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("moveAction","FriendRequestActivity");      // 친구 요청 목록 까지 보내기
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("친구 수락!")
                .setContentText(name + "가 친구 요청을 수락 하였습니다")
                .setContentIntent(pendingIntent);

        sendNotification(notificationBuilder);
    }

    private void friendAdd_NoPush(String name) {
        Intent intent = new Intent();
        ApplicationPackageRetriever applicationPackageRetriever = new ApplicationPackageRetriever(this);    // 앱 켜져있는지 확인
        String[] PackageString = applicationPackageRetriever.get();
        if(PackageString.length > 0 && PackageString[0].equals("whoim.leaveout") && loginActivity.LOGIN_CHECK == true) {       // 앱이 켜져있고 로그인 완료 했을 시
            intent.setClass(this, FriendRequestActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        else {      // 앱이 꺼져있으면
            intent.setClass(this, loginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("moveAction","FriendRequestActivity");      // 친구 요청 목록 까지 보내기
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("친구 거절")
                .setContentText(name + "가 친구 요청을 거절 하였습니다.")
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
