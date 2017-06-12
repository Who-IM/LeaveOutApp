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

import whoim.leaveout.Server.SQLWeb;

/**
 * 회전 로딩 다이얼로그
 */
public final class LoadingSQLDialog extends AsyncTask<Void,Integer,Void> {

    private Context mContext;
    private int style;                      // 로딩 다이얼로그 스타일
    private ProgressDialog mAsyncDialog;    // 로딩 중 다이얼로그
    private LoadingSQLListener mLoadingSqlListener;           // WebSQL에 데이터를 보내고 처리할 데이터 리스너
    private Object mCaller;

    private int size;
    private int uploadsize;

    private ExecutorService mExecutorService;        // 스레드 풀 시스템
    private SQLWeb sqlWeb;                      // WebSQL 접속 객체
    private ArrayList<JSONObject> responseData = new ArrayList();            // 응답받은 데이터
    private int mCompleteCount;

    // SQL로 보낸 데이터 처리(리스너 구현 및 로딩 다이얼로그 구현)
    public static LoadingSQLDialog SQLSendStart(Context context, LoadingSQLListener loadingSQLListener, int style ,Object caller) {   // caller 어디서 호출 했는지 판단(필요 없을시 null)
        LoadingSQLDialog LoadingSQLDialog = new LoadingSQLDialog(context,style,caller);             // 로딩 다이얼 로그
        LoadingSQLDialog.setLoadingSqlListener(loadingSQLListener);
        LoadingSQLDialog.execute();        // 다이얼로그 시작
        return LoadingSQLDialog;
    }

    private LoadingSQLDialog(Context context, int style, Object caller) {     // 생성자
        super();
        mContext = context;
        mAsyncDialog = new ProgressDialog(mContext);
        mAsyncDialog.setCancelable(false);
        mExecutorService = Executors.newCachedThreadPool();
        mCaller = caller;
        this.style = style;
    }

    @Override
    protected void onPreExecute() {
        if(mLoadingSqlListener != null) {
            size = (mLoadingSqlListener.getSize() != 0) ? mLoadingSqlListener.getSize() : 1;       // 보낼 갯수 사이즈 지정(0이 아닌경우 넣고 0인경우 1로 대처)
            if(style == ProgressDialog.STYLE_SPINNER || size == 1) {       // 원형
                mAsyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mAsyncDialog.setMessage("로딩중입니다.");        // 내용
                style = ProgressDialog.STYLE_SPINNER;
            }
            else if(style == ProgressDialog.STYLE_HORIZONTAL || size > 1){     // 바
                mAsyncDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mAsyncDialog.setMessage("업데이트 중입니다.");        // 내용
                style = ProgressDialog.STYLE_HORIZONTAL;
            }
        }

        // show dialog
        mAsyncDialog.show();        // 보여주기
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            uploadsize = size;
            JSONObject sqldata = mLoadingSqlListener.getSQLQuery();
            if(sqldata != null) {
                sqlWeb = new SQLWeb(sqldata);
                responseData.add(mExecutorService.submit(sqlWeb).get());       // 통신 시작
                if(responseData.get(responseData.size()-1) == null) return null;
                uploadsize = size - 1;
            }

            for (int i = 0; i < uploadsize; i++) {    // 업로드 있는지 확인
                JSONObject UploadData = mLoadingSqlListener.getUpLoad();   // 업로드 셋팅
                if (UploadData != null) {
                    sqlWeb = new SQLWeb(UploadData);   // 업로드 셋팅 성공하면
                    responseData.add(mExecutorService.submit(sqlWeb).get());       // 통신 시작
                    if (responseData.get(responseData.size() - 1) == null) return null;
                    if (style == ProgressDialog.STYLE_HORIZONTAL) publishProgress(100 / (uploadsize));
                }
                else {                // 업로드 셋팅 실패시
                    sqlWeb = null;
                    return null;
                }
            }
            if(style == ProgressDialog.STYLE_HORIZONTAL) Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        mAsyncDialog.setProgress(values[0] + mAsyncDialog.getProgress());
    }

    protected void onPostExecute(Void aVoid) {
        mAsyncDialog.dismiss();         // 종료
        if (mLoadingSqlListener != null) {              // 구현 했을경우 만 실행
            for (int i = 0; i < responseData.size(); i++) {            // 각 결과값 확인
                if (responseData.get(i) == null) {
                    Toast.makeText(mContext, "다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                    responseData= null;
                    break;
                }
            }
            try {
                if(responseData != null) mLoadingSqlListener.dataProcess(responseData, mCaller);     // WebSQL에서 받은 데이터 처리
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(mContext, "다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setLoadingSqlListener(LoadingSQLListener mLoadingSqlListener) {
        this.mLoadingSqlListener = mLoadingSqlListener;
    }
}
