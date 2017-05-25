package SQLControll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.sql.*;
import javax.sql.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.naming.*;

@SuppressWarnings({ "serial", "unchecked" })
public class Controller extends HttpServlet {

	PrintWriter out; // 웹 출력(응답) 스트림 (response)
	BufferedReader in; // 웹 입력(요청) 스트림 (request)
	Connection con; // db 접속

	private void process(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("euc-kr"); // 요청 charset 설정
		response.setContentType("text/html; charset=euc-kr"); // 응답 타입 및 charset 설정
		
		out = response.getWriter(); 			  // 웹 출력 스트림 생성
		JSONObject jsonObject = new JSONObject(); // 출력할 제이슨
		
		/* 테스트 중입니다. */
		JSONParser jsonParser = new JSONParser();
		JSONObject json = null;
		String line;
		in = request.getReader();
		while ((line = in.readLine()) != null)
			try {
				json = (JSONObject) jsonParser.parse(line);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		// ==================

		try {
			Context init = new InitialContext(); // web.xml 객체 생성
			DataSource ds = (DataSource) init.lookup("java:comp/env/jdbc/mysql"); // jdbc/mysql이라는 객체 가져오기
			con = ds.getConnection(); // DB 접속
			
			jsonObject.put("result", "성공");
			out.println(jsonObject.toJSONString()); // 웹 출력
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close(); // 출력 닫기
			in.close(); // 입력 닫기
			try {con.close();} catch(Exception e) {} // DB 닫기
		}
	}

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

}
