package whoim.leaveout.Loading;

import android.app.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by SeongMun on 2017-05-27.
 */
public interface LoadingSQLListener2 {
    int progressSetting(ProgressDialog progressDialog);
    JSONObject getSQLQuery();
    JSONObject getUpLoad();
    void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException;
}
