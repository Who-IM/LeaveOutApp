package whoim.leaveout.Loading;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import whoim.leaveout.Server.SQLWeb2;

/**
 * 회전 로딩 다이얼로그
 */
public abstract class LoadingSQLUploadDialog extends AsyncTask<Void,Void,Void> {

    private Context mContext;
    private ProgressDialog mAsyncDialog;        // 로딩 중 다이얼로그
    protected SQLWeb2 sqlWeb;                      // WebSQL 접속 객체
    protected JSONObject responseData;            // 응답받은 데이터
    protected ExecutorService mEexecutorService;
    protected boolean loding = true;

    public LoadingSQLUploadDialog(Context context) {     // 생성자
        super();
        mContext = context;
        mAsyncDialog = new ProgressDialog(mContext);
        mAsyncDialog.setCancelable(false);
        mEexecutorService = Executors.newCachedThreadPool();
    }

    @Override
    protected void onPreExecute() {
        mAsyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);        // 원형
        mAsyncDialog.setMessage("로딩중입니다..");        // 내용

        // show dialog
        mAsyncDialog.show();        // 보여주기
    }
}
