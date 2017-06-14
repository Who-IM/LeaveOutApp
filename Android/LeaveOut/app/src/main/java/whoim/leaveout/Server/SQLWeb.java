package whoim.leaveout.Server;

import org.json.JSONObject;

import java.util.concurrent.Callable;

/**
 * Created by 사용자 on 2017-05-25.
 */

public class SQLWeb implements Callable<JSONObject> {

    private JSONObject request;

    public SQLWeb(JSONObject request) {
        this.request = request;
    }

    @Override
    public JSONObject call() throws Exception {
        return new WebControll().WebLoad(request);        // 실패시 null
    }
}
