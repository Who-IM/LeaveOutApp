package whoim.leaveout.Loading;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 회전 로딩 다이얼로그
 */
public abstract class LoadingDialogBin extends AsyncTask<Void,Object,Void> {

    private Context mContext;
    public ProgressDialog mAsyncDialog;    // 로딩 중 다이얼로그
    protected ArrayList<JSONObject> result = new ArrayList();


    public LoadingDialogBin(Context context) {     // 생성자
        super();
        mContext = context;
        mAsyncDialog = new ProgressDialog(mContext);
        mAsyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mAsyncDialog.setMessage("로딩중입니다.");        // 내용
        mAsyncDialog.setCancelable(false);
    }


    @Override
    protected void onPreExecute() {
        mAsyncDialog.show();        // 보여주기
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mAsyncDialog.dismiss();         // 종료
    }

}
