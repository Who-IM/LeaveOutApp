package whoim.leaveout.Loading;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import whoim.leaveout.Server.SQLWeb2;

/**
 * 회전 로딩 다이얼로그
 */
public class LoadingSQLUploadDialog extends AsyncTask<Void,Void,Void> {

    private Context mContext;
    private ProgressDialog mAsyncDialog;        // 로딩 중 다이얼로그
    private LoadingSQLListener2 mLoadingSqlListener;           // WebSQL에 데이터를 보내고 처리할 데이터 리스너
    private Object mCaller;
    private int size;
    private int uploadsize;
    protected SQLWeb2 sqlWeb;                      // WebSQL 접속 객체
    protected ArrayList<JSONObject> responseData = new ArrayList();            // 응답받은 데이터
    protected ExecutorService mEexecutorService;

    // SQL로 보낸 데이터 처리(리스너 구현 및 로딩 다이얼로그 구현)
    public static LoadingSQLUploadDialog SQLSendStart(Context context, LoadingSQLListener2 loadingSQLListener, Object caller) {   // caller 어디서 호출 했는지 판단(필요 없을시 null)
        LoadingSQLUploadDialog LoadingSQLDialog = new LoadingSQLUploadDialog(context, caller);             // 로딩 다이얼 로그
        LoadingSQLDialog.setLoadingSqlListener(loadingSQLListener);
        LoadingSQLDialog.execute();        // 다이얼로그 시작
        return LoadingSQLDialog;
    }

    private LoadingSQLUploadDialog(Context context, Object caller) {     // 생성자
        super();
        mContext = context;
        mAsyncDialog = new ProgressDialog(mContext);
        mAsyncDialog.setCancelable(false);
        mEexecutorService = Executors.newCachedThreadPool();
        mCaller = caller;
    }

    public LoadingSQLUploadDialog(Context context) {     // 생성자
        super();
        mContext = context;
        mAsyncDialog = new ProgressDialog(mContext);
        mAsyncDialog.setCancelable(false);
        mEexecutorService = Executors.newCachedThreadPool();
    }

    @Override
    protected void onPreExecute() {
        if(mLoadingSqlListener != null) {
            size = mLoadingSqlListener.progressSetting(mAsyncDialog);
/*            mAsyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);        // 원형
            mAsyncDialog.setMessage("로딩중입니다..");        // 내용
            Log.d("ProgressStyle", mAsyncDialog.getProgress() + "");*/
        }

        // show dialog
        mAsyncDialog.show();        // 보여주기
    }


    @Override
    protected Void doInBackground(Void... params) {
        try {
            uploadsize = size;
            JSONObject sqldata = mLoadingSqlListener.getSQLQuery();
            if(sqldata != null) {       // sql 있는지 확인
                sqlWeb = new SQLWeb2(sqldata);
                responseData.add(mEexecutorService.submit(sqlWeb).get());       // 통신 시작
                uploadsize = size - 1;
            }
            if(responseData.size() != 0 && responseData.get(0) == null)
                return null;

            for (int i = 0; i < uploadsize; i++) {    // 업로드 있는지 확인
                JSONObject UploadData = mLoadingSqlListener.getUpLoad();   // 업로드 셋팅
                if (UploadData != null) {
                    sqlWeb = new SQLWeb2(UploadData);   // 업로드 셋팅 성공하면
                    responseData.add(mEexecutorService.submit(sqlWeb).get());       // 통신 시작
                }
                else {                // 업로드 셋팅 실패시
                    sqlWeb = null;
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    protected void onPostExecute(Void aVoid) {
        mAsyncDialog.dismiss();         // 종료
        if (mLoadingSqlListener != null) {              // 구현 했을경우 만 실행
            for (int i = 0; i < responseData.size(); i++) {            // 각 결과값 확인
                if (responseData.get(i) == null) {
                    Toast.makeText(mContext, "업로드에 실패 했습니다.", Toast.LENGTH_SHORT).show();
                    responseData = null;
                    break;
                }
            }
            try {
                mLoadingSqlListener.dataProcess(responseData, mCaller);     // WebSQL에서 받은 데이터 처리
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(mContext, "다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setLoadingSqlListener(LoadingSQLListener2 mLoadingSqlListener) {
        this.mLoadingSqlListener = mLoadingSqlListener;
    }
}
