<%@page import="DBSQLServer.DBSQL"%>
<%@ page import="java.sql.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	PreparedStatement pstmt;
    String sql = null;
    String usernum = null;
    String token = null;
    
    request.setCharacterEncoding("utf-8");
    
    DBSQL dbsql = new DBSQL(); 		// 데이터베이스 sql 객체 가져오기
    
    usernum = request.getParameter("user_num");
    token = request.getParameter("Token");
	// System.out.println(token);
    
    if( token == null || token.equals("") ){
        out.println("토큰값이 전달 되지 않았습니다.");
    }else{
        // 토큰값 전달시 쿼리문 입력할곳임
        sql = "insert into fcm values(?,?) ON DUPLICATE KEY UPDATE user_num = ?";
        pstmt = dbsql.getConnection().prepareStatement(sql);
        
        pstmt.setString(1,usernum);
        pstmt.setString(2,token);
        pstmt.setString(3,usernum);
        
        pstmt.executeUpdate();//쿼리를 실행 하라는 명령어
        
		if(pstmt!=null) try{pstmt.close();}catch(SQLException ex){}
		if(dbsql.getConnection()!=null) try{dbsql.getConnection().close();}catch(SQLException ex){}
		// System.out.println("등록완료");
    }
%>

