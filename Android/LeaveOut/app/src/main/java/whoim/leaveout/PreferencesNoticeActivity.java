package whoim.leaveout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.tsengvn.typekit.TypekitContextWrapper;

//알림 현재 숨김표시 해놓음
public class PreferencesNoticeActivity extends AppCompatActivity  {
    Switch all; //스위치 전체
    Switch newLocation; //새로운 위치 스위치
    Switch fence;   //울타리글 스위치
    Switch comment; //댓글 스위치
    Switch addFriend;   //친구 추가 스위치
    Switch tagFriend;   //친구 태그 스위치
    boolean switch_flag = false;    //전체 스위치 꺼지는거 방지
    public static boolean swLocation = false;   //새로운 위치 알림
    public static boolean swFence = false;
    int count = 0;  //카운트가 5일경우 all 스위치 On
    SharedPreferences mSwitch;
    SharedPreferences.Editor editor ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_notice_layout);
        mSwitch = getSharedPreferences("switch", MODE_PRIVATE);
        editor = mSwitch.edit();
        noticeSwitch(); //스위치 전체 관리
        SwLoad();
    }

    private void noticeSwitch() {
        all = (Switch) findViewById(R.id.notice_all);
        fence = (Switch) findViewById(R.id.notice_fence);
        newLocation = (Switch) findViewById(R.id.notice_new_location);
        comment = (Switch) findViewById(R.id.notice_comment);
        addFriend = (Switch) findViewById(R.id.notice_add_friend);
        tagFriend = (Switch) findViewById(R.id.notice_tag_friend);

        all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {    //전체알람 스위치 켰을시 모든 스위치 켜기
                    switch_flag = true;
                    fence.setChecked(true);
                    newLocation.setChecked(true);
                    comment.setChecked(true);
                    addFriend.setChecked(true);
                    tagFriend.setChecked(true);
                } else if (isChecked == false) {    //전체 알람 스위치 껐을시 모든 스위치 끄기
                    if (switch_flag) {  //다른 스위치로 껏을시 모든 스위치 꺼지는거 방지
                        fence.setChecked(false);
                        newLocation.setChecked(false);
                        comment.setChecked(false);
                        addFriend.setChecked(false);
                        tagFriend.setChecked(false);
                    }
                }
            }
        });

        newLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    ++count;
                    editor.putBoolean("newLocation", isChecked);
                    if (count == 5) {
                        all.setChecked(true);
                    }
                    swLocation = true;
                } else {
                    --count;
                    editor.putBoolean("newLocation", isChecked);
                    switch_flag = false;
                    all.setChecked(false);
                    swLocation = false;
                }
                editor.commit();
            }
        });

        fence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    ++count;
                    editor.putBoolean("fence", isChecked);
                    if (count == 5) {
                        all.setChecked(true);
                    }
                    swFence = true;
                } else {
                    --count;
                    editor.putBoolean("newLocation", isChecked);
                    switch_flag = false;
                    all.setChecked(false);
                    swFence = false;
                }
                editor.commit();
            }
        });

        comment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    ++count;
                    editor.putBoolean("comment", isChecked);
                    if (count == 5) {
                        all.setChecked(true);
                    }
                } else {
                    --count;
                    editor.putBoolean("newLocation", isChecked);
                    switch_flag = false;
                    all.setChecked(false);
                }
                editor.commit();
            }
        });

        addFriend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    ++count;
                    editor.putBoolean("addFriend", isChecked);
                    if (count == 5) {
                        all.setChecked(true);
                    }
                } else {
                    --count;
                    editor.putBoolean("newLocation", isChecked);
                    switch_flag = false;
                    all.setChecked(false);
                }
                editor.commit();
            }
        });

        tagFriend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    ++count;
                    editor.putBoolean("tagFriend", isChecked);
                    if (count == 5) {
                        all.setChecked(true);
                    }
                } else {
                    --count;
                    editor.putBoolean("newLocation", isChecked);
                    switch_flag = false;
                    all.setChecked(false);

                }
                editor.commit();
            }
        });
    }

    public void SwLoad() {
        newLocation.setChecked(mSwitch.getBoolean("newLocation", false));
        fence.setChecked(mSwitch.getBoolean("fence", false));
        comment.setChecked(mSwitch.getBoolean("comment", false));
        addFriend.setChecked(mSwitch.getBoolean("addFriend", false));
        tagFriend.setChecked(mSwitch.getBoolean("tagFriend", false));
    }

    // 뒤로가기
    public void Back(View v) {
        finish();
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
