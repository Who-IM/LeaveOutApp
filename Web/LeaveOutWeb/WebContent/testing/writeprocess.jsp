<%@ page language="java" contentType="text/html; charset=EUC-KR"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.*"%>

<%
	String idstring=null;

	if (session.getAttribute("id")!=null){
		idstring=(String)session.getAttribute("id");
	}else{
		out.println("<script>");
		out.println("alert('로그인 세션을 확인헤주세요.');");
		out.println("location.href='loginForm.jsp'");
		out.println("</script>");
	}

	
	String usernumstring=request.getParameter("usernum");
	String locxstring=request.getParameter("locx");
	String locystring=request.getParameter("locy");
	String locnamestring=request.getParameter("locname");
	String regtimestring=request.getParameter("regtime");
	String contentinputstring=request.getParameter("contentinput");
	String filedirstring;
	String filedir;
	
	
	Connection conn=null;
	PreparedStatement pstmt=null;
	ResultSet rs=null;
	
	String seqnum = "1";
	
	try {
  		Context init = new InitialContext();
  		DataSource ds = (DataSource) init.lookup("java:comp/env/jdbc/mysql");
  		conn = ds.getConnection();
  		
  		pstmt=conn.prepareStatement("SELECT content_num FROM content");
  		rs=pstmt.executeQuery();
  		

		if(rs.last()){
			if(rs.getRow() == 0){
				seqnum = "1";
			}
			else{
				seqnum = (rs.getRow() + 1) + "";
			}
		}
		
	}catch(Exception e){
		e.printStackTrace();
 	}
	
	filedirstring = "/leaveout/contents/" + seqnum + ".txt";
	filedir = application.getRealPath(filedirstring);
	out.println(filedirstring);
%>

writeprocessing...

<%
	PrintWriter writer = null;
	try{
	 writer = new PrintWriter(filedir);
	writer.println(contentinputstring);
	}catch(IOException e){
		e.printStackTrace();
	}finally{
		try{
			writer.close();
		}catch(Exception e){
		}
	}
	
	Connection conn2=null;
	PreparedStatement pstmt2=null;
	
	try {

		pstmt2=conn.prepareStatement("INSERT INTO content(user_num, reg_time, loc_x, loc_y, loc_name, files) VALUES(?, ?, ?, ?, ?, ?)");
  		pstmt2.setString(1,usernumstring);
  		pstmt2.setString(2,regtimestring);
  		pstmt2.setString(3,locxstring);
  		pstmt2.setString(4,locystring);
  		pstmt2.setString(5,locnamestring);
		pstmt2.setString(6,filedirstring);
  		int result=pstmt2.executeUpdate();
		
		if(result!=0){  			
  			out.println("<script>");
  		  	out.println("location.href='writeok.jsp'");
  		  	out.println("</script>");
  		}else{
  			out.println("<script>");
  	  		out.println("location.href='writefail.jsp'");
  	  		out.println("</script>");	
  		}
	}catch(Exception e){
		e.printStackTrace();
 	}
%>
