package whoim.leaveout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import whoim.leaveout.Loading.LoadingDialog;

public class JoinActivity extends AppCompatActivity {

    // 각 입력 칸
    EditText mIdInsert;
    EditText mPassInsert;
    EditText mRePassInsert;
    EditText mName_insert;
    EditText mMail_insert;

    // 중복 확인(이미지로)
    TextView mIdOverlap;
    TextView mEmailOverlap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_layout);

        mIdInsert = (EditText) findViewById(R.id.id_insert);
        mPassInsert = (EditText) findViewById(R.id.pw_insert);
        mRePassInsert = (EditText) findViewById(R.id.re_pw_insert);
        mName_insert = (EditText) findViewById(R.id.name_insert);
        mMail_insert = (EditText) findViewById(R.id.mail_insert);

        mIdOverlap = (TextView) findViewById(R.id.id_overlap);
        mEmailOverlap = (TextView) findViewById(R.id.email_overlap);

        // 포커스 확인 리스너
        View.OnFocusChangeListener FocusListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextView ImageView = null;
                if(v.getId() == R.id.id_insert) {       // id 칸 일경우
                    ImageView = mIdOverlap;             // id 옆에 뷰
                } else if(v.getId() == R.id.mail_insert) {  // 이메일 칸 일 경우
                    ImageView = mEmailOverlap;          // 메일 옆에 뷰
                }

                if(hasFocus)
                    ImageView.setVisibility(View.INVISIBLE);
                else
                    ImageView.setVisibility(View.VISIBLE);

            }
        };

        mIdInsert.setOnFocusChangeListener(FocusListener);      // 포커스 확인 리스너 설정
        mMail_insert.setOnFocusChangeListener(FocusListener);   // 포커스 확인 리스너 설정
        mMail_insert.setOnEditorActionListener(new TextView.OnEditorActionListener()        // 키보드에 완료 버튼 리스너 설정
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    Toast.makeText(getApplicationContext(),"로그인 성공",Toast.LENGTH_SHORT).show();

                    //키보드 내리기
                    InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    return true;
                }
                return false;
            }
        });



    }

    public void joinButton(View v) {
        Intent intent = new Intent(getApplicationContext(), loginActivity.class);
        startActivity(intent);
    }

    public void Join_cancelButton(View v) {
        Intent intent = new Intent(getApplicationContext(), loginActivity.class);
        startActivity(intent);
    }

    // 버튼 onclick 함수
    public void joinOnClicked(View view) {

        new LoadingDialog(this).execute();

    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

}
