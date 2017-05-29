package whoim.leaveout.Loading;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import whoim.leaveout.SQL.SQLListener;
import whoim.leaveout.SQL.SQLWeb;

/**
 * 회전 로딩 다이얼로그
 */
public class LoadingDialog extends AsyncTask<Void,Void,Void> {

    private Context mContext;
    private ProgressDialog mAsyncDialog;        // 로딩 중 다이얼로그
    private SQLListener mSqlListener;           // WebSQL에 데이터를 보내고 처리할 데이터 리스너
    private SQLWeb sqlWeb;                      // WebSQL 접속 객체
    private JSONObject responseData;            // 응답받은 데이터

    public LoadingDialog(Context context) {
        super();
        mContext = context;
        mAsyncDialog = new ProgressDialog(mContext);
        mAsyncDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        mAsyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);        // 원형
        mAsyncDialog.setMessage("로딩중입니다..");        // 내용

        // show dialog
        mAsyncDialog.show();        // 보여주기

        if(mSqlListener != null) {      // 구현 했을경우 만 실행
            try {
                sqlWeb = new SQLWeb();
                sqlWeb.execute(mSqlListener.getDataSend());     // 접속하기 구현한 리스너에서 데이터 넣기
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... params) {
        if(mSqlListener != null) {      // 구현 했을경우 만 실행
            try {
                responseData = sqlWeb.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mAsyncDialog.dismiss();         // 종료
        if(responseData != null && mSqlListener != null) {      // 구현 했을경우 만 실행
            mSqlListener.dataProcess(responseData);     // WebSQL에서 받은 데이터 처리
        }
        else {
            Toast.makeText(mContext,"다시 시도해 주십시오.",Toast.LENGTH_SHORT).show();
        }
    }

    public void setSqlListener(SQLListener mSqlListener) {
        this.mSqlListener = mSqlListener;
    }
}
