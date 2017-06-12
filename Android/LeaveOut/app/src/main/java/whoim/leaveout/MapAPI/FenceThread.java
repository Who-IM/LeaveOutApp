package whoim.leaveout.MapAPI;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import whoim.leaveout.Server.SQLWeb;

/**
 * Created by SeongMun on 2017-06-12.
 */

public class FenceThread extends AsyncTask<JSONObject,Void,JSONObject> {

    private ExecutorService service = Executors.newCachedThreadPool();      // 스레드 풀
    private FenceUIListener fenceUIListener;

    public interface FenceUIListener {
        void FenceUI(JSONObject result) throws JSONException;
    }

    public FenceThread(FenceUIListener fenceUIListener) {
        this.fenceUIListener = fenceUIListener;
    }

    @Override
    protected JSONObject doInBackground(JSONObject... params) {
        try {
            return service.submit(new SQLWeb(params[0])).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(JSONObject result) {
        try {
            fenceUIListener.FenceUI(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
