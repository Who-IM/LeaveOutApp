package whoim.leaveout.Loading;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SeongMun on 2017-05-27.
 */
public interface LoadingSQLListener {
    JSONObject getDataSend();
    void dataProcess(JSONObject responseData, Object caller) throws JSONException;
}
