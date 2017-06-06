package Controll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.sql.*;
import javax.sql.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import DBSQLServer.DBSQL;

import javax.naming.*;

@SuppressWarnings({ "serial", "unchecked" })
public class PhoneController extends HttpServlet {

	PrintWriter out; // 웹 출력(응답) 스트림 (response)
	BufferedReader in; // 웹 입력(요청) 스트림 (request)
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.process(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.process(request, response);
	}
	
	// 처리 함수
	private void process(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("euc-kr"); // 요청 charset 설정
		response.setContentType("text/html; charset=euc-kr"); // 응답 타입 및 charset 설정

		try {
			out = response.getWriter(); // 웹 출력(응답용) 스트림 생성
			in = request.getReader();	// 웹 입력(요청용) 스트림 생성
			JSONObject resJSON = null;
			// 데이터 가져온 후 dataProcess 작동 
			// 출력할(응답 데이터) 요청 스트림 안될경우 null 반환 
			resJSON = (in != null) ? getInputData(): null;
			
			if(resJSON == null) {	// 응답 제이슨을 안 만들어진경우
				resJSON = new JSONObject();
				resJSON.put("result", "error");
			}
			out.println(resJSON.toJSONString()); // 웹 출력
//			debuging();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			out.close(); // 출력 닫기
			in.close(); // 입력 닫기
		}
		
	}
	
	/* 디버그 용 */
	private void debuging() {
		/*===============*/
		DataSource ds = null;	// 데이터 소스 정보
		Connection con; // db 접속
		PreparedStatement pstmt;		// sql 쿼리
		ResultSet rs;	// SQL 결과
		
		try {
			Context init = new InitialContext(); // web.xml 객체 생성
			ds = (DataSource) init.lookup("java:comp/env/jdbc/mysql"); // jdbc/mysql이라는 객체 가져오기
			con = ds.getConnection();
			pstmt = con.prepareStatement("SELECT * FROM content");	// SQL 쿼리문 객체 생성
			rs = pstmt.executeQuery();			// SQL 쿼리 작동
			
			ResultSetMetaData resultmeta = rs.getMetaData();
			
			// size -1 일 경우 전부 데이터 넣기 
			int size = resultmeta.getColumnCount();
			while(rs.next()) {
				for(int i = 1; i <= size; i++) {		// 가져올 데이터 제이슨에 집어넣기
					System.out.println(resultmeta.getColumnTypeName(i));
					System.out.println(rs.getObject(i));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}			// SQL 서버 접속
		/*===============*/
	}
	
	/* 요청한 곳에서 데이터 가져오기 */
	private JSONObject getInputData() {

		JSONObject json = null;
		String line;
		StringBuffer inbuffer = new StringBuffer();
		try {
			while ((line = in.readLine()) != null) {	// 데이터 가져오는중
				inbuffer.append(line);
			}
			if(!(inbuffer.toString().equals("")))
				json = (JSONObject) new JSONParser().parse(inbuffer.toString());

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return dataProcess(json);		// 받은 데이터 처리 함수 
	}
	
	/* 받은 데이터 처리 함수 */
	private JSONObject dataProcess(JSONObject data) {
		JSONObject resJSON = null;
		
		if (data == null) {	// 데이터가 없는경우
			return null;
		}
		else {		// 데이터 있을 경우
			String maindata = (String) data.get("check");
			if (maindata.equals("mysql")) {		// 요청하는 데이터  mysql 방식
				DBSQL dbsql = new DBSQL();		// SQL 전용 관리 객체 생성
				JSONObject subdata = (JSONObject) data.get("sql");		// sql 타입 가져오기
				String type = (String) subdata.get("type");				// type (select or update)
				String sql = (String) subdata.get("query");				// 쿼리문
				
				if (type.equals("select")) {							// select 일 경우
					int size = ((Long)subdata.get("size")).intValue();	// 검색할 갯수
					resJSON = dbsql.getPhoneSelect(sql,size);			// 꺼내오기
				}
				else if(type.equals("update")) {						// update 일 경우
					resJSON = dbsql.getPhoneUpdate(sql);			// 업데이트
				}
			}
		}

		return resJSON;

	}

}
