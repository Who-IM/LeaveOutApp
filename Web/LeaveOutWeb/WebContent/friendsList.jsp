<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>

<!-- contents -->
<!DOCTYPE html>
<html>
<body>
	<ul class="list-group">
<%
		PreparedStatement pstmt6=null;
		ResultSet rs6 = null;
		
		try
		{
		pstmt6=conn.prepareStatement("SELECT user.name " +
									"FROM friend inner join user " +
									"on friend.friend_num = user.user_num " +
									"WHERE friend.user_num = ? " +
									"AND friend.request = 0 " + 
									"order by user.name asc; ");

		pstmt6.setString(1, userNumString);
		rs6=pstmt6.executeQuery();

		while(rs6.next())
		{
			//sesson-OK
			//friendNumString = rs.getString("name");
			out.println("<li class='list-group-item'>");
			out.println("<i class='glyphicon glyphicon-user'></i>"+rs6.getString("user.name")+"</li>");
		}
	}
	catch(Exception e)
	{
	e.printStackTrace();
	}
%>
</ul>
</body>
</html>