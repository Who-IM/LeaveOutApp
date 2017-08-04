package Controll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import DBSQLServer.DBSQL;

@WebServlet("/FCMPush")
public class FCMPush extends HttpServlet {
	
    public static String simpleApiKey = "AAAABKkgK9c:APA91bHxWFUnlR6UVJecUNUQOjagYlsylmcYiT14Lw2KODgD7cKbI83MbdPUbVqF4eKu7NjB_-KInKJcvBypikVwxT7zAMx_h-mSMSw0T-vv9Qy6_iYgy_p2NOqzJDRZJgBbeZtqRdXC";
    public static String gcmURL = "https://android.googleapis.com/fcm/send";   
	
    ArrayList<String> token = new ArrayList<String>();    //token값을 ArrayList에 저장
    String MESSAGE_ID = String.valueOf(Math.random() % 100 + 1);    //메시지 고유 ID
    boolean SHOW_ON_IDLE = false;    //옙 활성화 상태일때 보여줄것인지
    int LIVE_TIME = 1;    //옙 비활성화 상태일때 FCM가 메시지를 유효화하는 시간
    int RETRY = 2;    //메시지 전송실패시 재시도 횟수
    
	PrintWriter out; // 웹 출력(응답) 스트림 (response)
	private Connection con; // db 접속
	PreparedStatement pstmt;
    String sql = null;
    ResultSet rs = null;
	private String requestdata;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			this.process(request, response);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			this.process(request, response);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void process(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		response.setContentType("text/html; charset=utf-8"); // 응답 타입 및 charset 설정
	    request.setCharacterEncoding("utf-8");
		out = response.getWriter(); // 웹 출력(응답용) 스트림 생성
		
	    DBSQL dbsql = new DBSQL(); 		// 데이터베이스 sql 객체 가져오기
	    Message message = null;
	    
	    requestdata = request.getParameter("requestdata");
		if(requestdata.equals("AddFriend")) {		// 친구 추가 요청 푸시 알림
			message = addFriend(request);
		}
		else if(requestdata.equals("AddFriend_OK")) {	// 친구 추가 수락
			message = addFriend_OK(request);
		}
		else if(requestdata.equals("AddFriend_NO")) {	// 친구 추가 수락
			message = addFriend_NO(request);
		}
		
	    if(message != null) {
	        PushStart(message);
	    }
	    
		if(out != null) out.close(); // 출력 닫기
		if(rs != null) rs.close();
		if(pstmt!=null) try{pstmt.close();}catch(SQLException ex){}
		if(con!=null) try{con.close();}catch(SQLException ex){}
	    
	}
	
	// 친구 추가 요청
	private Message addFriend(HttpServletRequest request) throws UnsupportedEncodingException, SQLException {
		Message message = null;
		DBSQL dbsql = new DBSQL(); // 데이터베이스 sql 객체 가져오기
		con = dbsql.getConnection();
		String user_num = request.getParameter("user_num");
		String friend_num = request.getParameter("friend_num");

		sql = "select token from fcm where user_num in (select friend_num from friend where user_num = ? and friend_num = ?)";
		pstmt = con.prepareStatement(sql);
		pstmt.setString(1, user_num);
		pstmt.setString(2, friend_num);
		rs = pstmt.executeQuery();// 쿼리를 실행 하라는 명령어
		while (rs.next()) { // 모든 등록ID를 리스트로 묶음
			token.add(rs.getString("token"));
		}

		if (token.size() == 0) {
			return null;
		}

		sql = "select name from user where user_num = " + user_num;
		pstmt = con.prepareStatement(sql);
		rs = pstmt.executeQuery();
		String name = null;
		while (rs.next()) {
			name = rs.getString(1);
		}

		message = new Message.Builder()
				.collapseKey(MESSAGE_ID)
				.delayWhileIdle(SHOW_ON_IDLE)
				.timeToLive(LIVE_TIME)
				.addData("title", "friendadd")
				.addData("name", URLEncoder.encode(name, "UTF-8"))
				.build();

		return message;
	}
	
	// 친구 추가 수락
	private Message addFriend_OK(HttpServletRequest request) throws UnsupportedEncodingException, SQLException {
		Message message = null;
		DBSQL dbsql = new DBSQL(); // 데이터베이스 sql 객체 가져오기
		con = dbsql.getConnection();
		String user_num = request.getParameter("user_num");
		String friend_num = request.getParameter("friend_num");
		
		sql = "select token from fcm where user_num = ?";
		pstmt = con.prepareStatement(sql);
		pstmt.setString(1, user_num);
		rs = pstmt.executeQuery();// 쿼리를 실행 하라는 명령어
		while (rs.next()) { // 모든 등록ID를 리스트로 묶음
			token.add(rs.getString("token"));
		}

		if (token.size() == 0) {
			return null;
		}

		sql = "select name from user where user_num = " + friend_num;
		pstmt = con.prepareStatement(sql);
		rs = pstmt.executeQuery();
		String name = null;
		while (rs.next()) {
			name = rs.getString(1);
		}

		message = new Message.Builder()
				.collapseKey(MESSAGE_ID)
				.delayWhileIdle(SHOW_ON_IDLE)
				.timeToLive(LIVE_TIME)
				.addData("title", "friendadd_ok")
				.addData("name", URLEncoder.encode(name, "UTF-8"))
				.build();
		
		return message;
		
	}
	
	// 친구 추가 거절
	private Message addFriend_NO(HttpServletRequest request) throws UnsupportedEncodingException, SQLException {
		Message message = null;
		DBSQL dbsql = new DBSQL(); // 데이터베이스 sql 객체 가져오기
		con = dbsql.getConnection();
		String user_num = request.getParameter("user_num");
		String friend_num = request.getParameter("friend_num");
		
		sql = "select token from fcm where user_num = ?";
		pstmt = con.prepareStatement(sql);
		pstmt.setString(1, user_num);
		rs = pstmt.executeQuery();// 쿼리를 실행 하라는 명령어
		while (rs.next()) { // 모든 등록ID를 리스트로 묶음
			token.add(rs.getString("token"));
		}

		if (token.size() == 0) {
			return null;
		}

		sql = "select name from user where user_num = " + friend_num;
		pstmt = con.prepareStatement(sql);
		rs = pstmt.executeQuery();
		String name = null;
		while (rs.next()) {
			name = rs.getString(1);
		}

		message = new Message.Builder()
				.collapseKey(MESSAGE_ID)
				.delayWhileIdle(SHOW_ON_IDLE)
				.timeToLive(LIVE_TIME)
				.addData("title", "friendadd_no")
				.addData("name", URLEncoder.encode(name, "UTF-8"))
				.build();
		
		return message;
		
	}
	
	private void PushStart(Message message) throws IOException {
	    Sender sender = new Sender(simpleApiKey);
        MulticastResult result1 = sender.send(message,token,RETRY);
        if (result1 != null) {
            List<Result> resultList = result1.getResults();
            for (Result result : resultList) {
                System.out.println(result.getErrorCodeName()); 
            }
            token.clear();
        }
	}

	

}
