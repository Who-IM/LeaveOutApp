<%@ page language="java" contentType="text/html; charset=EUC-KR"%>
<%@ page import="java.sql.*" %>
<%
	Connection conn=null;
	
	//Oracle DBMS 2015.2.4
	//String driver="oracle.jdbc.driver.OracleDriver";
	//String url="jdbc:oracle:thin:@localhost:1521:ORCL";

	//MySQL DBMS
	String driver="com.mysql.jdbc.Driver";
	String url="jdbc:mysql://localhost:3306/test";
	
	Boolean connect=false;
	
	try{
		Class.forName(driver);
		conn=DriverManager.getConnection(url,"root","mysql");
		
		connect=true;
		
		conn.close();
	}catch(Exception e){
		connect=false;
		e.printStackTrace();
	}
%>
<html>
<head>
<title>JDBC ���� �׽�Ʈ ����</title>
</head>
<body>
<h3>
<%if(connect==true){ %>
	����Ǿ����ϴ�.
<%}else{ %>
	���ῡ �����Ͽ����ϴ�.
<%} %>
</h3>
</body>
</html>
