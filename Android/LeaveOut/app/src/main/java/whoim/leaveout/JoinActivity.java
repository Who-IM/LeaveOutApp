package whoim.leaveout;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
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
import whoim.leaveout.Server.SQLDataService;

public class JoinActivity extends AppCompatActivity {

    private InputMethodManager mInputMethodManager;     // 키보드 서비스

    // 각 입력 칸
    private EditText mIdInsert;
    private EditText mPassInsert;
    private EditText mRePassInsert;
    private EditText mName_insert;
    private EditText mMail_insert;

    private SQLDataService.DataQueryGroup mDataQueryGroup = SQLDataService.DataQueryGroup.getInstance();          // sql에 필요한 데이터 그룹
    private String mInsertSQL = "insert into user(id,password,name,email,phone_num) values(?,?,?,?,?)";     // 유저 추가 sql
    private String mSelectSQL;       // 유저 검색 sql
    private boolean[] overcheck = {false,false};    // 0 : id , 1 : email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_layout);

        mIdInsert = (EditText) findViewById(R.id.id_insert);
        mPassInsert = (EditText) findViewById(R.id.pw_insert);
        mRePassInsert = (EditText) findViewById(R.id.re_pw_insert);
        mName_insert = (EditText) findViewById(R.id.name_insert);
        mMail_insert = (EditText) findViewById(R.id.mail_insert);

        init();     // 초기화

    }

    // 초기화
    private void init() {
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);      // 키보드 서비스

        TextWatcher textWatcher =  new TextWatcher() {
            String before;
            // 입력하기 전에(입력 전)
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {this.before = s.toString();}
            // 입력되는 텍스트에 변화가 있을 때(입력 중)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            // 입력이 끝났을 때(입력 완료)
            @Override
            public void afterTextChanged(Editable editable) {      // 입력이 있으면 다시 중복 체크
                if(!before.equals(editable.toString())) {
                    if (getCurrentFocus() == mIdInsert) {
                        overcheck[0] = false;
                    }
                    else if (getCurrentFocus() == mMail_insert) {
                        overcheck[1] = false;
                    }
                }
            }
        };

        mIdInsert.addTextChangedListener(textWatcher);      // 입력 변화 이벤트
        mMail_insert.addTextChangedListener(textWatcher);   // 입력 변화 이벤트

        mMail_insert.setOnEditorActionListener(new TextView.OnEditorActionListener() {       // 키보드에 완료 버튼 리스너 설정
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mInputMethodManager.hideSoftInputFromWindow(mMail_insert.getWindowToken(),0);      // 키보드 내리기
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
                mDataQueryGroup.clear();
                mDataQueryGroup.addString(mIdInsert.getText().toString());
                selectSQLData(view);
                break;
            case R.id.email_overlap:    // 메일
                if(!editCheck(mMail_insert)) return;    // 입력 없을 시 리턴
                mSelectSQL = "select email from user where email = ?";
                mDataQueryGroup.clear();
                mDataQueryGroup.addString(mMail_insert.getText().toString());
                selectSQLData(view);
                break;
        }
    }

    private boolean editCheck(EditText editText) {
        if (editText.getText().toString().equals("")) {
            if(editText.getId() == R.id.id_insert)
                Toast.makeText(JoinActivity.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
            else if(editText.getId() == R.id.mail_insert)
                Toast.makeText(JoinActivity.this, "메일을 입력해주세요", Toast.LENGTH_SHORT).show();
            editText.requestFocus();       // 포커스 이동
            mInputMethodManager.showSoftInput(editText,InputMethodManager.SHOW_FORCED);         // 키보드 보이기
            return false;
        }
        return true;
    }

    // edit 체크 확인(true 체크 완료, false 체크 실패)
    private boolean editCheckAll() {
        if (mIdInsert.getText().toString().equals("")) {
            Toast.makeText(JoinActivity.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
            mIdInsert.requestFocus();       // 포커스 이동
            mInputMethodManager.showSoftInput(mIdInsert,InputMethodManager.SHOW_FORCED);         // 키보드 보이기
            return false;
        }
        if (mPassInsert.getText().toString().equals("")) {
            Toast.makeText(JoinActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            mPassInsert.requestFocus();
            mInputMethodManager.showSoftInput(mPassInsert,InputMethodManager.SHOW_FORCED);         // 키보드 보이기
            return false;
        }
        if (mRePassInsert.getText().toString().equals("")) {
            Toast.makeText(JoinActivity.this, "비밀번호 확인를 입력해주세요", Toast.LENGTH_SHORT).show();
            mRePassInsert.requestFocus();
            mInputMethodManager.showSoftInput(mRePassInsert,InputMethodManager.SHOW_FORCED);         // 키보드 보이기
            return false;
        }
        if (mName_insert.getText().toString().equals("")) {
            Toast.makeText(JoinActivity.this, "이름를 입력해주세요", Toast.LENGTH_SHORT).show();
            mName_insert.requestFocus();
            mInputMethodManager.showSoftInput(mName_insert,InputMethodManager.SHOW_FORCED);         // 키보드 보이기
            return false;
        }
        if (mMail_insert.getText().toString().equals("")) {
            Toast.makeText(JoinActivity.this, "메일을 입력해주세요", Toast.LENGTH_SHORT).show();
            mMail_insert.requestFocus();
            mInputMethodManager.showSoftInput(mMail_insert,InputMethodManager.SHOW_FORCED);         // 키보드 보이기
            return false;
        }
        if(!overcheck[0]) {
            Toast.makeText(JoinActivity.this, "ID 중복 체크를 해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!overcheck[1]) {
            Toast.makeText(JoinActivity.this, "email 중복 체크를 해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // sql 업데이트
    private void updateSQLData() {
        if (editCheckAll()) {
            LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
                @Override
                public JSONObject getDataSend() {
                    mDataQueryGroup.clear();
                    mDataQueryGroup.addString(mIdInsert.getText().toString());        // id
                    mDataQueryGroup.addString(mPassInsert.getText().toString());      // 패스워드
                    mDataQueryGroup.addString(mName_insert.getText().toString());     // 이름
                    mDataQueryGroup.addString(mMail_insert.getText().toString());     // 메일
                    TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    mDataQueryGroup.addString(mgr.getLine1Number());      //  폰번호
                    return SQLDataService.getDynamicSQLJSONData(mInsertSQL, mDataQueryGroup, 0, "update");     // update SQL 제이슨
                }

                @Override
                public void dataProcess(JSONObject responseData, Object caller) throws JSONException {
                    Log.d("responseData", responseData.toString());
                    if (!(responseData.getString("result").equals("error"))) {        // 에러가 아닐경우
                        Toast.makeText(JoinActivity.this, "가입이 완료 되었습니다.", Toast.LENGTH_LONG).show();
                        JoinActivity.this.finish();     // 액티비티 종료
                    }
                    else
                        Toast.makeText(JoinActivity.this, "잠시 후 다시 시도해 주십시오.", Toast.LENGTH_LONG).show();
                }
            };
            LoadingDialog.SQLSendStart(this,loadingSQLListener, null);      // 로딩 다이얼로그 및 sql 전송
        }
    }

    private void selectSQLData(Object caller) {
        LoadingSQLListener loadingSQLListener = new LoadingSQLListener() {
            @Override
            public JSONObject getDataSend() {
                return SQLDataService.getDynamicSQLJSONData(mSelectSQL.toString(), mDataQueryGroup,-1,"select");     // select SQL 제이슨
            }

            @Override
            public void dataProcess(JSONObject responseData, Object caller) throws JSONException {
                Log.d("responseData", responseData.toString());
                if (caller instanceof View) {
                    View v = (View) caller;
                    if (responseData.getJSONArray("result").length() != 0) {
                        if (v.getId() == R.id.id_overlap) {     // 중복일 경우
                            Toast.makeText(JoinActivity.this, "ID 중복이 있습니다.", Toast.LENGTH_LONG).show();
                            overcheck[0] = false;
                        }
                        else if (v.getId() == R.id.email_overlap) {
                            Toast.makeText(JoinActivity.this, "메일이 중복이 있습니다.", Toast.LENGTH_LONG).show();
                            overcheck[1] = false;
                        }
                    }
                    else {      // 중복이 아닐경우
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

        LoadingDialog.SQLSendStart(this,loadingSQLListener, caller);        // 로딩 다이얼로그 및 sql 전송
    }

/*    // SQL로 보낸 데이터 처리(리스너 구현 및 로딩 다이얼로그 구현)
    private void SQLSendStart(LoadingSQLListener loadingSQLListener, Object caller) {   // caller 어디서 호출 했는지 판단(필요 없을시 null)
        mLoadingDialog = new LoadingDialog(this, caller);             // 로딩 다이얼 로그
        mLoadingDialog.setSqlListener(loadingSQLListener);
        mLoadingDialog.execute();        // 다이얼로그 시작
    }*/

    // 폰트 바꾸기
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

}
