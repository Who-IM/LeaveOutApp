package whoim.leaveout;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

//알림 현재 숨김표시 해놓음
public class PreferencesNoticeActivity extends AppCompatActivity  {
    Switch all; //스위치 전체
    Switch fence;   //울타리글 스위치
    Switch newLocation; //새로운 위치 스위치
    Switch comment; //댓글 스위치
    Switch addFriend;   //친구 추가 스위치
    Switch tagFriend;   //친구 태그 스위치
    boolean switch_flag = false;
    public static boolean swLocation = false;
    int count = 0;  //카운트가 5일경우 all 스위치 On

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_notice_layout);


        noticeSwitch(); //스위치 전체 관리
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

                if (isChecked == true) {
                    switch_flag = true;
                    fence.setChecked(true);
                    newLocation.setChecked(true);
                    comment.setChecked(true);
                    addFriend.setChecked(true);
                    tagFriend.setChecked(true);
                    Toast.makeText(PreferencesNoticeActivity.this, "전체 스위치 ON", Toast.LENGTH_SHORT).show();
                } else if (isChecked == false) {
                    if (switch_flag) {
                        fence.setChecked(false);
                        newLocation.setChecked(false);
                        comment.setChecked(false);
                        addFriend.setChecked(false);
                        tagFriend.setChecked(false);
                    }
                    Toast.makeText(PreferencesNoticeActivity.this, "전체 스위치 OFF", Toast.LENGTH_SHORT).show();
                }


            }
        });
        fence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    ++count;
                    if (count == 5) {
                        all.setChecked(true);
                    }

                    Toast.makeText(PreferencesNoticeActivity.this, "울타리글 스위치 ON", Toast.LENGTH_SHORT).show();
                } else {
                    --count;
                    switch_flag = false;
                    all.setChecked(false);
                    Toast.makeText(PreferencesNoticeActivity.this, "울타리글 스위치 OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
        newLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    ++count;
                    if (count == 5) {
                        all.setChecked(true);
                    }
                    swLocation = true;
                    Toast.makeText(PreferencesNoticeActivity.this, "새로운 위치 스위치 ON", Toast.LENGTH_SHORT).show();
                } else {
                    --count;
                    switch_flag = false;
                    all.setChecked(false);
                    swLocation = false;
                    Toast.makeText(PreferencesNoticeActivity.this, "새로운 위치 스위치 OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
        comment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    ++count;
                    if (count == 5) {
                        all.setChecked(true);
                    }
                    Toast.makeText(PreferencesNoticeActivity.this, "댓글 스위치 ON", Toast.LENGTH_SHORT).show();
                } else {
                    --count;
                    switch_flag = false;
                    all.setChecked(false);
                    Toast.makeText(PreferencesNoticeActivity.this, "댓글 스위치 OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
        addFriend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    ++count;
                    if (count == 5) {
                        all.setChecked(true);
                    }
                    Toast.makeText(PreferencesNoticeActivity.this, "친구 추가 스위치 ON", Toast.LENGTH_SHORT).show();
                } else {
                    --count;
                    switch_flag = false;
                    all.setChecked(false);
                    Toast.makeText(PreferencesNoticeActivity.this, "친구 추가 스위치 OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tagFriend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    ++count;
                    if (count == 5) {
                        all.setChecked(true);
                    }
                    Toast.makeText(PreferencesNoticeActivity.this, "친구 태그 스위치 ON", Toast.LENGTH_SHORT).show();
                } else {
                    --count;
                    switch_flag = false;
                    all.setChecked(false);
                    Toast.makeText(PreferencesNoticeActivity.this, "친구 태그 스위치 OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
