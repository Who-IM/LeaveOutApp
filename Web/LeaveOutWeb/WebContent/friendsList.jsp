<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>
	
   <ul class="list-group">
<%
      PreparedStatement pstmt6=null;
      ResultSet rs6 = null;
      
      try
      {
      pstmt6=conn.prepareStatement("SELECT user.name, user.user_num " +
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
         out.println("<a href='profileDetails.jsp?user_num="+userNumString+"&target_user="+rs6.getString("user.user_num")+"&locx=36&locy=128\'>");
         out.println("<i class='glyphicon glyphicon-user'></i>"+rs6.getString("user.name"));
         out.println("</a> </li>");

      }
   }
   catch(Exception e)
   {
   e.printStackTrace();
   }
      
%>
</ul>


<i class="glyphicon glyphicon-exclamation-sign"></i><br>
더 만들어 보세요!<br>
