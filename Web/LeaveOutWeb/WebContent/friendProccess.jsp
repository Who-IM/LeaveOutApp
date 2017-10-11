<%@page contentType="text/html;charset=EUC-KR" %>
<%@page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>

<%
String u_num = request.getParameter("u_num");
String f_num = request.getParameter("f_num");
String flag = flag = request.getParameter("flag");

Connection conn=null;
PreparedStatement pstmt=null;
ResultSet rs=null;

Context init = new InitialContext();
DataSource ds = (DataSource) init.lookup("java:comp/env/jdbc/mysql");
conn = ds.getConnection();

try {
	pstmt=conn.prepareStatement("delete from friend where user_num=? AND friend_num=?");
	pstmt.setInt(1,Integer.parseInt(u_num));
	pstmt.setInt(2,Integer.parseInt(f_num));
	pstmt.executeUpdate();
}catch(Exception e){
	e.printStackTrace();
}

if(flag == null) {
	try {
		pstmt=conn.prepareStatement("insert into friend values(?,?,0);");
		pstmt.setInt(1,Integer.parseInt(u_num));
		pstmt.setInt(2,Integer.parseInt(f_num));
		pstmt.executeUpdate();
		
		pstmt=conn.prepareStatement("insert into friend values(?,?,0);");
		pstmt.setInt(1,Integer.parseInt(f_num));
		pstmt.setInt(2,Integer.parseInt(u_num));
		pstmt.executeUpdate();
		
	}catch(Exception e){
		e.printStackTrace();
	}
}
%>

<script>
var referrer2 =  document.referrer;
location.href = referrer2;
</script>