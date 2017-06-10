package whoim.leaveout.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 전송할 데이터 만들기
 */

public final class SQLDataService {

    /* sql : 쿼리 문, size : 검색할 갯수(-1 : 전체, 0 : 없음), type : select or update */
    public static JSONObject getSQLJSONData(String sql, int size , String type) {

        JSONObject requestData = new JSONObject();      // 요청할 데이터
        try {
            requestData.put("check","mysql");           // sql 방식

            JSONObject sqlData = new JSONObject();      // sql에 필요한 데이터 (subJSON)
            sqlData.put("type",type);       // 타입
            sqlData.put("query",sql);       // 쿼리문
            if(size != 0) sqlData.put("size",size);       // 검색할 갯수

            requestData.put("sql",sqlData); // 요청할 데이터에 넣기

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestData;
    }

    // 동적 쿼리 문
    public static JSONObject getDynamicSQLJSONData(String sql, DataQueryGroup data, int size, String type) {
        char ch = sql.charAt(sql.length()-1);
        int sqlsize =  (ch != '?') ? new StringTokenizer(sql,"?").countTokens() - 1 : new StringTokenizer(sql,"?").countTokens();          // 동적 sql 토큰 갯수 확인(끝 부분이 ?가 아니면 1개 빼기)
        int datasize = data.size();                                             // 갯수 확인
        if(sqlsize != datasize) return null;                                    // 갯수가 다르면 오류이므로 null

        StringTokenizer DataToken= new StringTokenizer(data.toString(),"/");    // 데이터 토큰
        // sql에 데이터 넣기
        while(DataToken.hasMoreElements()) {
            sql = sql.replaceFirst("\\?",DataToken.nextToken());
        }

        return getSQLJSONData(sql,size,type);
    }

    // 동적 (?) 쿼리 만들기
    public static String getDynamicQuery(int size) {
        StringBuilder sqldata = new StringBuilder();
        for(int i = 0; i < size; i++) {
            sqldata.append("?");
            if(i != (size-1)) sqldata.append(",");
        }
        return sqldata.toString();
    }

    // 데이터 번들 값 추가(한 개)
    public static JSONObject putBundleValue(JSONObject requestData, String group, String key, Object data) {
        JSONObject bundle = null;       // 번들 데이터
        try {
            if(requestData.has(group))      // 있는지 없는지 판단
                bundle = (JSONObject) requestData.get(group);
            else {
                requestData.put(group, new JSONObject());        // 없을경우 추가
                bundle = requestData.getJSONObject(group);
            }
            bundle.put(key, data);       // 데이터 추가
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestData;
    }

    // 데이터 번들 값 추가(여러 개)
    public static JSONObject putBundleArray(JSONObject requestData, String group, ArrayList data) {
        JSONObject bundle = null;       // 번들 데이터
        JSONArray jsonArray = new JSONArray();
        try {
            if(requestData.has(group))
                bundle = (JSONObject) requestData.get(group); // 있는지 없는지 판단
            else {
                requestData.put(group, new JSONObject());        // 없을경우 추가
                bundle = requestData.getJSONObject(group);
            }
            for (Object o : data) jsonArray.put(o);
            bundle.put("array", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestData;
    }

    // 동적용 sql에 보낼 data 만들기
    public static final class DataQueryGroup {

        private static DataQueryGroup mInstance;        // 싱글 톤
        private StringBuilder stringBuilder;
        private int index;

        public static DataQueryGroup getInstance() {
            if(mInstance == null)
                mInstance = new DataQueryGroup();

            return mInstance;
        }

        private DataQueryGroup() {
            this.stringBuilder = new StringBuilder();
            this.index = 0;
        }

        // 데이터 추가
        public void addString(String data) {
            if(index == 0) stringBuilder.append("\""+data+"\"");
            else stringBuilder.append("/\"" + data +"\"");

            index++;
        }

        public void addInt(int data) {
            if(index == 0) stringBuilder.append(data);
            else stringBuilder.append("/" + data);

            index++;
        }

        public void addDouble(double data) {
            if(index == 0) stringBuilder.append(data);
            else stringBuilder.append("/" + data);

            index++;
        }

        public void addBoolean(boolean data) {
            if(index == 0) stringBuilder.append(data);
            else stringBuilder.append("/" + data);

            index++;
        }

        // 사이즈
        public int size() {
            return index;
        }

        // 초기화
        public void clear() {
            stringBuilder.setLength(0);
            index = 0;
       }

        // 데이터 꺼내기
        public String toString() {
            return stringBuilder.toString();
        }
    }


}
