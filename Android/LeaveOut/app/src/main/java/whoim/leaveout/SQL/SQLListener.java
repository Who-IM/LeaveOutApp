package whoim.leaveout.SQL;

import org.json.JSONObject;

/**
 * Created by SeongMun on 2017-05-27.
 */
public interface SQLListener {
    JSONObject getDataSend();
    void dataProcess(JSONObject responseData);
}
