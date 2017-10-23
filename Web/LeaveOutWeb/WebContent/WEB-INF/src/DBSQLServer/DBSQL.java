package DBSQLServer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



public class DBSQL {
	
	private DataSource ds;	// 데이터 소스 정보
	private Connection con; // db 접속
	private PreparedStatement pstmt;		// sql 쿼리
	private ResultSet rs;	// SQL 결과
	
	public DBSQL() {
		try {
			Context init = new InitialContext(); // web.xml 객체 생성
			ds = (DataSource) init.lookup("java:comp/env/jdbc/mysql"); // jdbc/mysql이라는 객체 가져오기
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("연결 실패");
		}
	}
	
	public Connection getConnection() {
		try {
			if(con == null || con.isClosed()) con = ds.getConnection();			// SQL 서버 접속
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			// SQL 서버 접속
		return con;
	}

	// 폰 연동 한 select 문
	@SuppressWarnings("unchecked")
	public JSONObject getPhoneSelect(String sql, int size) {
		
		JSONObject resJSON = null;		// 응답용 데이터
		try {
			if(con == null || con.isClosed()) con = ds.getConnection();			// SQL 서버 접속
			pstmt = con.prepareStatement(sql);	// SQL 쿼리문 객체 생성
			rs = pstmt.executeQuery();			// SQL 쿼리 작동
			ResultSetMetaData resultmeta = rs.getMetaData();
			
			// size -1 일 경우 전부 데이터 넣기 
			size = (size != -1) ? size : resultmeta.getColumnCount();
			
			resJSON = new JSONObject();	// 응답용 데이터 객체 생성
			JSONArray jsonArray = new JSONArray();	// select 나온 데이터를 넣을 배열 객체 생성
			
			while(rs.next()) {
				JSONObject data = new JSONObject();
				for(int i = 1; i <= size; i++) {		// 가져올 데이터 제이슨에 집어넣기
					if(rs.getObject(i) instanceof Timestamp) {		// 시간 방식 객체이면 String 가져오기 
						data.put(resultmeta.getColumnLabel(i), rs.getString(i));
						continue;
					}
					data.put(resultmeta.getColumnLabel(i), rs.getObject(i));
				}
				jsonArray.add(data);		// 데이터 추가
			}
			resJSON.put("result", jsonArray);		// 응답할 제이슨에 데이터 넣기
			
		} catch (Exception e) {		// SQL 에러
			e.printStackTrace();
			return null;
		}
		finally {
			if(rs!=null) try{rs.close();}catch(SQLException ex){}
			if(pstmt!=null) try{pstmt.close();}catch(SQLException ex){}
			if(con!=null) try{con.close();}catch(SQLException ex){}
		}
		
		return resJSON;
		
	}
	
	// 폰 연동 Update 문
	@SuppressWarnings("unchecked")
	public JSONObject getPhoneUpdate(String sql) {

		JSONObject resJSON = null; // 응답용 데이터

		try {
			if(con == null || con.isClosed()) con = ds.getConnection();			// SQL 서버 접속
			pstmt = con.prepareStatement(sql); 	// SQL 쿼리문 객체 생성
			int result = pstmt.executeUpdate();	// SQL 업데이트 (반환 값 : 업데이트 된 행의 갯수)

			resJSON = new JSONObject(); 	// 응답용 데이터 객체 생성
			resJSON.put("result", result);	// 결과값 
			
		} catch (Exception e) { // SQL 에러
			e.printStackTrace();
			return null;
		} finally {
			if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
			if (con != null)try {con.close();} catch (SQLException ex) {}
		}

		return resJSON;
	}
	
	

}