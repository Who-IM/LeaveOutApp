package whoim.leaveout.Loading;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * 회전 로딩 다이얼로그
 */

public class LoadingDialog extends AsyncTask<Void,Void,Void> {

    ProgressDialog mAsyncDialog;

    public LoadingDialog(Context context) {
        super();
        mAsyncDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        mAsyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mAsyncDialog.setMessage("로딩중입니다..");

        // show dialog
        mAsyncDialog.show();
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            for (int i = 0; i < 5; i++) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mAsyncDialog.dismiss();
    }
}
