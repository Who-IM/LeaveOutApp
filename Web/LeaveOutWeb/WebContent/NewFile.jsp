<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Insert title here</title>
</head>
<body>
<%
	out.print("request.getRequestURL():"+request.getRequestURL()+"..<br />");
	out.print("request.getRequestURI():"+request.getRequestURI()+"..<br />");
	out.print(request.getRequestURL().toString().replace(request.getRequestURI(),"/"));
%>

</body>
</html>