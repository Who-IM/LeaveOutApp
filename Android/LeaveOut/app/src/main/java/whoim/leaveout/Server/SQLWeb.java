package whoim.leaveout.Server;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 사용자 on 2017-05-25.
 */

public class SQLWeb extends AsyncTask<JSONObject, Void, JSONObject> {

    HttpURLConnection mCon;
    BufferedWriter mBufferedWriter;
    BufferedReader mBufferedReader;

    @Override
    protected JSONObject doInBackground(JSONObject... params) {

        try {
            URL url = new URL("http://106.249.39.40:8080/controll"); // URL화 한다.
            mCon = (HttpURLConnection) url.openConnection();                 // 접속 객체 생성
//            mCon.setRequestProperty("Content-Type", "application/json");      // 타입설정(application/json) 형식으로 전송
            mCon.setRequestProperty("Content-Type", "text/html");               // 타입설
            mCon.setConnectTimeout(10000);  // 접속 제한시간
            mCon.setReadTimeout(10000);     // 입력스트림 읽어오는 제한시간
            mCon.setRequestMethod("POST");  // POST방식 통신
            mCon.setDoOutput(true);         // 쓰기모드 지정
            mCon.setDoInput(true);          // 읽기모드 지정

            mBufferedWriter = new BufferedWriter(new OutputStreamWriter(mCon.getOutputStream(), "euc-kr"));       // 접속한 출력 스트림 생성
            mBufferedWriter.write(params[0].toString());        // 여기서 각 필요한 데이터 보내기
            mBufferedWriter.flush();        // 보내기

            mBufferedReader = new BufferedReader(new InputStreamReader(mCon.getInputStream(), "euc-kr"));       // 접속한 입력 스트림 생성
            StringBuilder sb = new StringBuilder();         // 스트링빌더 생성
            String json;        // 스트림으로 꺼낸것을 임시 저장
            while ((json = mBufferedReader.readLine()) != null) {        // 스트림 뽑아내기
                sb.append(json + "\n");
            }

            return new JSONObject(sb.toString());       // 뽑아낸것을 제이슨으로 객체로 만들어 리턴

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(mBufferedWriter != null) mBufferedWriter.close();
                if(mBufferedReader != null) mBufferedReader.close();
                if(mCon != null) mCon.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;        // 실패시 null
    }

}
