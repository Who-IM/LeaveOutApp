﻿<%@ page language="java" contentType="text/html; charset=EUC-KR"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>

<% String profilePicTarget = ".\\leaveout\\files\\" + targetUserNumString + "\\profile\\1.jpg"; %>

	<div class="media">
			<a class="pull-left" href="#">
				<img class="media-object" src="<%=profilePicTarget%>" width="50" height="50" alt="...">
			</a>
			<a class="pull-right" href="#">
				<button type="button" class="btn btn-default">
				<i class="glyphicon glyphicon-plus-sign"></i> 친구 추가
				</button>
			</a>
			<div class="media-body">
				<h2 class="media-heading"><%=targetUserNameString%> 님</h2>
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit"<br>
			</div>
		</div>
		<div class="col-md-6">
			<h5><i class="glyphicon glyphicon-info-sign"></i><%=targetUserNameString%>님이 어디어디에 글을 썼는지 확인해보세요.</h5>
			<div id="map"></div>
		</div>
		<div class="col-md-6">
		<h5><i class="glyphicon glyphicon-info-sign"></i><%=targetUserNameString%>님이 어떤 카테고리로 글을 썼는지 확인해보세요.</h5>
		
		<%
		PreparedStatement pstmt5=null;
		ResultSet rs5=null;
	
		try {
			pstmt5=conn.prepareStatement("SELECT cate_text FROM category WHERE user_num=?");
			pstmt5.setString(1,userNumString);
			rs5=pstmt5.executeQuery();
  		
			while(rs5.next()){
				out.println("<button type='button' class='btn btn btn-primary'>");
				out.println("<i class='glyphicon glyphicon-tags'></i>   "+rs5.getString("cate_text"));
				out.println("</button>");
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
		%>
		
		<br><br>
		<ul class="list-group">
			<li class="list-group-item">
				<div class="media">
					<a class="pull-left" href="#">
						<img class="media-object" src="profile_default.jpg" width="50" height="50" alt="...">
					</a>
					<%
					PreparedStatement pstmt7=null;
					ResultSet rs7=null;
	
					try {
						pstmt7=conn.prepareStatement("SELECT * from content where user_num=?;");
						pstmt7.setString(1,userNumString);
						rs7=pstmt7.executeQuery();
  		
						while(rs7.next()){
							out.println("<h4 class='media-heading'>"+rs7.getString("address")+"<br></h4>");
							out.println("<i class='glyphicon glyphicon-flag'></i>"+rs7.getString("reg_time")+"<br>");
							out.println("<div id='carousel-example-generic' class='carousel slide'>");
							out.println("<ol class='carousel-indicators'>");
							out.println("<li data-target='#carousel-example-generic' data-slide-to='0' class='active'></li>");
							out.println("<li data-target='#carousel-example-generic' data-slide-to='1'></li></ol>");
							out.println("<div class='carousel-inner'>");
							out.println("<div class='item active'>");
							out.println("<img src=./content_default1.jpg alt='...'></div>");
							out.println("내용 넣어야됨");
							out.println("");
						}
					}catch(Exception e){
						e.printStackTrace();
					}		
					%>
					
					
				</div>
			</li>
		</ul>
		
</div>