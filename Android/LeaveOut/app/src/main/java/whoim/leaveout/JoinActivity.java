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

    LoadingDialog mLoadingDialog;            // 로딩 다이얼 로그
    private SQLDataService.DataStringGroup dataStringGroup = new SQLDataService.DataStringGroup();          // sql에 필요한 데이터 그룹
    private String mInsertSQL = "insert into user(id,password,name,email,phone_num) values(?,?,?,?,?)";     // 유저 추가 sql
    private String mSelectSQL;       // 유저 검색 sql
    private boolean[] overcheck = {false,false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_layout);

        mIdInsert = (EditText) findViewById(R.id.id_insert);
        mPassInsert = (EditText) findViewById(R.id.pw_insert);
        mRePassInsert = (EditText) findViewById(R.id.re_pw_insert);
        mName_insert = (EditText) findViewById(R.id.name_insert);
        mMail_insert = (EditText) findViewById(R.id.mail_insert);

        mMail_insert.setOnEditorActionListener(new TextView.OnEditorActionListener() {       // 키보드에 완료 버튼 리스너 설정
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
                updateSQLData();        // 유저 추가 sql
                break;
            case R.id.join_cancel:      // 취소 버튼
                finish();               // 액티비티 종료
                break;
        }
    }

    public void onOverClicked(View view) {
        switch (view.getId()) {
            case R.id.id_overlap:       // 확인
                if(!editCheck(mIdInsert)) return;       // 입력 없을 시 리턴
                mSelectSQL = "select id from user where id = ?";
                dataStringGroup.clear();
                dataStringGroup.addString(mIdInsert.getText().toString());
                selectSQLData(view);
                break;
            case R.id.email_overlap:    // 메일
                if(!editCheck(mMail_insert)) return;    // 입력 없을 시 리턴
                mSelectSQL = "select email from user where email = ?";
                dataStringGroup.clear();
                dataStringGroup.addString(mMail_insert.getText().toString());
                selectSQLData(view);
                break;
        }
    }

    private boolean editCheck(EditText editText) {
        if (mIdInsert.getText().toString().equals("")) {
            if(editText.getId() == R.id.id_insert)
                Toast.makeText(JoinActivity.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
            else if(editText.getId() == R.id.mail_insert)
                Toast.makeText(JoinActivity.this, "메일을 입력해주세요", Toast.LENGTH_SHORT).show();
            mIdInsert.requestFocus();       // 포커스 이동
            keyBoardPutDown(false);         // 키보드 보이기
            return false;
        }
        return true;
    }

    // edit 체크 확인(true 체크 완료, false 체크 실패)
    private boolean editCheckAll() {
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
        if (editCheckAll()) {
            LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
                @Override
                public JSONObject getDataSend() {
                    dataStringGroup.clear();
                    dataStringGroup.addString(mIdInsert.getText().toString());        // id
                    dataStringGroup.addString(mPassInsert.getText().toString());      // 패스워드
                    dataStringGroup.addString(mName_insert.getText().toString());     // 이름
                    dataStringGroup.addString(mMail_insert.getText().toString());     // 메일
                    TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    dataStringGroup.addString(mgr.getLine1Number());      //  폰번호
                    return SQLDataService.getDynamicSQLJSONData(mInsertSQL, dataStringGroup, 0, "update");     // update SQL 제이슨
                }

                @Override
                public void dataProcess(JSONObject responseData, Object caller) throws JSONException {
                    Log.d("responseData", responseData.toString());
                    if (!(responseData.getString("result").equals("error")))
                        Toast.makeText(JoinActivity.this, "가입이 완료 되었습니다.", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(JoinActivity.this, "잠시 후 다시 시도해 주십시오.", Toast.LENGTH_LONG).show();
                }
            };
            SQLDataSendStart(loadingSQLListener, null);
        }
    }

    private void selectSQLData(Object caller) {
        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public JSONObject getDataSend() {
                return SQLDataService.getDynamicSQLJSONData(mSelectSQL.toString(),dataStringGroup,-1,"select");     // select SQL 제이슨
            }

            @Override
            public void dataProcess(JSONObject responseData, Object caller) throws JSONException {
                Log.d("responseData", responseData.toString());
                if (caller instanceof View) {
                    View v = (View) caller;
                    if (responseData.getJSONArray("result").length() != 0) {
                        if (v.getId() == R.id.id_overlap) {
                            Toast.makeText(JoinActivity.this, "ID 중복이 있습니다.", Toast.LENGTH_LONG).show();
                            overcheck[0] = false;
                        }
                        else if (v.getId() == R.id.email_overlap) {
                            Toast.makeText(JoinActivity.this, "메일이 중복이 있습니다.", Toast.LENGTH_LONG).show();
                            overcheck[1] = false;
                        }
                    }
                    else {
                        if (v.getId() == R.id.id_overlap) {
                            Toast.makeText(JoinActivity.this, "ID 사용 가능합니다.", Toast.LENGTH_LONG).show();
                            overcheck[0] = true;
                        }
                        else if (v.getId() == R.id.email_overlap) {
                            Toast.makeText(JoinActivity.this, "메일 사용가능합니다.", Toast.LENGTH_LONG).show();
                            overcheck[1] = true;
                        }
                    }
                }   // if -- END --
            }   // dataProcess -- END --
        };   // loadingSQLListener -- END --

        SQLDataSendStart(loadingSQLListener, caller);
    }

    // SQL로 보낸 데이터 처리(리스너 구현 및 로딩 다이얼로그 구현)
    private void SQLDataSendStart(LoadingSQLListener loadingSQLListener, Object caller) {   // caller 어디서 호출 했는지 판단(필요 없을시 null)
        mLoadingDialog = new LoadingDialog(this, caller);             // 로딩 다이얼 로그
        mLoadingDialog.setSqlListener(loadingSQLListener);
        mLoadingDialog.execute();        // 다이얼로그 시작
    }

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

}
