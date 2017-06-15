package whoim.leaveout.Loading;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by SeongMun on 2017-05-27.
 */
public interface LoadingSQLListener {
    int getSize();
    JSONObject getSQLQuery();
    JSONObject getUpLoad(JSONObject resultSQL);
    void dataProcess(ArrayList<JSONObject> responseData, Object caller) throws JSONException;
}
