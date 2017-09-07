<%@ page language="java" contentType="text/html; charset=EUC-KR"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>

<html>
<head>
    <title>Login...</title>
	
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	
	<!-- Bootstrap Core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">
	
	<!-- Theme Fonts -->
    <link href="css/logintheme.css" rel="stylesheet" type="text/css">
	
	
    <script src="https://use.typekit.net/ayg4pcz.js"></script>
    <script>try{Typekit.load({ async: true });}catch(e){}</script>

</head>

<body>


   <script language="JavaScript" src="script.js"> </script>
    <div class="container">
      <h1 class="welcome text-center">로그인 중입니다...</h1>
    </div><!-- /container -->
	
	<%
		String idString=request.getParameter("inputId");
		String passString=request.getParameter("inputPassword");
		String userNumString;
		
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
	
		try {
			Context init = new InitialContext();
			DataSource ds = (DataSource) init.lookup("java:comp/env/jdbc/mysql");
			conn = ds.getConnection();
  		
			pstmt=conn.prepareStatement("SELECT * FROM user WHERE id=?");
			pstmt.setString(1,idString);
			rs=pstmt.executeQuery();
  		
			if(rs.next()){
				if(passString.equals(rs.getString("password"))){
					userNumString = rs.getString("user_num");
					out.println(userNumString);
					out.println("<script>");
					out.println("location.href='main.jsp?user_num="+userNumString+"'");
					out.println("</script>");
				}
			}
  		
			out.println("<script>");
			out.println("alert('아이디와 비밀번호를 다시 확인해주세요.');");
			out.println("location.href='index.jsp'");
			out.println("</script>");
		}catch(Exception e){
			e.printStackTrace();
		}
	%>

</body>
</html>