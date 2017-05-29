package whoim.leaveout;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import whoim.leaveout.Loading.LoadingDialog;
import whoim.leaveout.Loading.LoadingSQLListener;
import whoim.leaveout.SQL.SQLDataService;

public class JoinActivity extends AppCompatActivity {

    // 각 입력 칸
    private EditText mIdInsert;
    private EditText mPassInsert;
    private EditText mRePassInsert;
    private EditText mName_insert;
    private EditText mMail_insert;

    // 중복 확인(이미지로)
    private TextView mIdOverlap;
    private TextView mEmailOverlap;

    LoadingDialog mLoadingDialog;            // 로딩 다이얼 로그
    private SQLDataService.DataStringGroup dataStringGroup = new SQLDataService.DataStringGroup();          // sql에 필요한 데이터 그룹
    private String mInsertSQL = "insert into user(id,password,name,email,phone_num) values(?,?,?,?,?)";     // 유저 추가 sql
    private StringBuilder mSelectSQL = new StringBuilder("select * from user");       // 유저 검색 sql

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
                if (v.getId() == R.id.id_insert) {       // id 칸 일경우
                    ImageView = mIdOverlap;             // id 옆에 뷰
                } else if (v.getId() == R.id.mail_insert) {  // 이메일 칸 일 경우
                    ImageView = mEmailOverlap;          // 메일 옆에 뷰
                }
                if (hasFocus)       // 포커스 확인
                    ImageView.setVisibility(View.INVISIBLE);    // 표시 x
                else
                    ImageView.setVisibility(View.VISIBLE);      // 표시 o
            }
        };

        mIdInsert.setOnFocusChangeListener(FocusListener);      // 포커스 확인 리스너 설정(ID입력 칸)
        mMail_insert.setOnFocusChangeListener(FocusListener);   // 포커스 확인 리스너 설정(메일 입력칸)
        mMail_insert.setOnEditorActionListener(new TextView.OnEditorActionListener()        // 키보드에 완료 버튼 리스너 설정
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    keyBoardPutDown(true);      // 키보드 내리기
                    updateSQLData();            // 유저 추가 sql
                    return true;
                }
                return false;
            }
        });
    }

    // 버튼 onClick 함수
    public void joinOnClicked(View view) {
        switch (view.getId()) {
            case R.id.join_insert:      // 가입 버튼 눌럿을시
//                updateSQLData();        // 유저 추가 sql
                selectSQLData();
                break;
            case R.id.join_cancel:      // 취소 버튼
                finish();               // 액티비티 종료
                break;
        }
    }

    // edit 체크 확인(true 체크 완료, false 체크 실패)
    private boolean editCheck() {
        if (mIdInsert.getText().toString().equals("")) {
            Toast.makeText(JoinActivity.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
            mIdInsert.requestFocus();       // 포커스 이동
            keyBoardPutDown(false);         // 키보드 보이기
            return false;
        }
        if (mPassInsert.getText().toString().equals("")) {
            Toast.makeText(JoinActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            mPassInsert.requestFocus();
            keyBoardPutDown(false);
            return false;
        }
        if (mRePassInsert.getText().toString().equals("")) {
            Toast.makeText(JoinActivity.this, "비밀번호 확인를 입력해주세요", Toast.LENGTH_SHORT).show();
            mRePassInsert.requestFocus();
            keyBoardPutDown(false);
            return false;
        }
        if (mName_insert.getText().toString().equals("")) {
            Toast.makeText(JoinActivity.this, "이름를 입력해주세요", Toast.LENGTH_SHORT).show();
            mName_insert.requestFocus();
            keyBoardPutDown(false);
            return false;
        }
        if (mMail_insert.getText().toString().equals("")) {
            Toast.makeText(JoinActivity.this, "메일을 입력해주세요", Toast.LENGTH_SHORT).show();
            mMail_insert.requestFocus();
            keyBoardPutDown(false);
            return false;
        }
        return true;
    }

    // 키보드 보이기 및 숨기기
    private void keyBoardPutDown(boolean hideCheck) {
        InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        if(hideCheck) immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);                            // 키보드 안 보이기
        else immhide.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);        // 키보드 보이기

    }

    // sql 업데이트
    private void updateSQLData() {
        if (editCheck()) {
            dataStringGroup.clear();
            dataStringGroup.add(mIdInsert.getText().toString());        // id
            dataStringGroup.add(mPassInsert.getText().toString());      // 패스워드
            dataStringGroup.add(mName_insert.getText().toString());     // 이름
            dataStringGroup.add(mMail_insert.getText().toString());     // 메일
            TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            dataStringGroup.add(mgr.getLine1Number());      //  폰번호
            SQLDataSendStart(SQLDataService.getDynamicSQLJSONData(mInsertSQL,dataStringGroup,0,"update"));     // SQL 서비스 시작
        }
    }

    private void selectSQLData() {
        dataStringGroup.clear();
        SQLDataSendStart(SQLDataService.getSQLJSONData(mSelectSQL.toString(),-1,"select"));
    }

    // SQL로 보낸 데이터 처리(리스너 구현 및 로딩 다이얼로그 구현)
    private void SQLDataSendStart(final JSONObject requestData) {
        mLoadingDialog = new LoadingDialog(this);             // 로딩 다이얼 로그
        mLoadingDialog.setSqlListener(new LoadingSQLListener() {            // 로딩 데이터 프로세스 리스너
            @Override
            public JSONObject getDataSend() {       // 송신 할 데이터
                return requestData;
            }

            @Override
            public void dataProcess(JSONObject responseData) throws JSONException {      // 수신된 데이터 받은 뒤 프로세스
                Log.d("responseData", responseData.toString());
                if (!(responseData.getString("result").equals("error")))
                    Toast.makeText(JoinActivity.this, "가입이 완료 되었습니다.", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(JoinActivity.this, "잠시 후 다시 시도해 주십시오.", Toast.LENGTH_LONG).show();
            }
        });
        mLoadingDialog.execute();        // 다이얼로그 시작
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

}
