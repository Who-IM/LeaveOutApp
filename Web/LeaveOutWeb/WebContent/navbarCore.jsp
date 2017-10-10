<%@page import="java.util.ArrayList"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>
<%@ page import="java.util.*"%>

	<!-- Contents Start -->
	<div class="navbar navbar-default navbar-fixed-top">
	  <div class="container">
		<!-- brand name -->
		<a class="navbar-brand" href="main.jsp?user_num=<%=userNumString%>">LeaveOut</a>
	      <div class="navbar-header">
		   <button class="navbar-toggle collapsed"
		           data-toggle="collapse" data-target="#target">
     			<span class="sr-only">Toggle navigation</span>
	     		<span class="icon-bar"></span>
		    	<span class="icon-bar"></span>
		    	<span class="icon-bar"></span>
		   </button>
	      </div>
		  <div class="collapse navbar-collapse" id="target">
			<!-- left side menu -->
			<ul class="nav navbar-nav">
			  <li>
                   <form class="navbar-form navbar-left">
                       <div class="input-group input-group-sm" style="max-width:360px;">
                         <input type="text" class="form-control" placeholder="Search" name="srch-term" id="srch-term">
                         <div class="input-group-btn" onclick="">
							<a class="btn btn-default" role="button" type="button"><i class="glyphicon glyphicon-search"></i></a>
                         </div>
                       </div>
                   </form>
			  </li>
			  <li onclick="mapbounds();"><a>모아보기</a>
			  			<script>
						function mapbounds() {
							var parameter ="";	
							parameter += "bounds="+map.getBounds();
								
							<%-- <a href="contentDetails.jsp?user_num=<%=userNumString%>" --%>
							location.href="contentDetails.jsp?user_num=<%=userNumString%>&"+parameter;
						}
						</script>
		      <li><a href="posting.jsp?user_num=<%=userNumString%>">글쓰기</a></li>
		      <li class="dropdown">
			      <a href="#" class="dropdown-toggle" id='dx' data-toggle="dropdown">
				      <%
				      PreparedStatement fst=null;
			  		  ResultSet frs=null;
			  		  int fcnt = 0;
			  		  
				      try {
				    	  fst=conn.prepareStatement("select count(*) as count from friend " +
									                "where friend_num = ? AND request = 1;");
				    	  
				    	  fst.setString(1,userNumString);
				    	  frs=fst.executeQuery();
				  		
						  while(frs.next()){
							   fcnt = Integer.parseInt(frs.getString("count"));
							   out.println(fcnt);
						  }
					  }catch(Exception e){
						  e.printStackTrace();
					  }
				      %>
			      </a>
			      <ul class="dropdown-menu">
				      <%
				      PreparedStatement fst2=null;
			  		  ResultSet frs2=null;
			  		  String waitRequest[] = new String[fcnt];
			  		  Integer fuser_num[] = new Integer[fcnt];
			  		  Integer ffriend_num[] = new Integer[fcnt];
			  		  int ftemp = 0;
			  		  
				      try {
				    	  fst2=conn.prepareStatement("select * from friend " +
				                                     "inner join user on user.user_num = friend.user_num " +
									                 "where friend_num = ? AND request = 1;");
				    	  fst2.setString(1,userNumString);
				    	  frs2=fst2.executeQuery();
				  		
						  while(frs2.next()){
							  waitRequest[ftemp] = frs2.getString("name");
							  fuser_num[ftemp] = frs2.getInt("user_num");
							  ffriend_num[ftemp] = frs2.getInt("friend_num");
							  ftemp++;
						  }
					  }catch(Exception e){
						  e.printStackTrace();
					  }
				      
				     
				      if(waitRequest.length != 0) {
				    	  out.println("<table>");
					      for(int i = 0; i < waitRequest.length; i++) {
					    	  out.println("<td><li>"+waitRequest[i]+"<td>");
					    	  out.println("<td><button type='button' onclick='fsuccess("+fuser_num[i]+", "+ffriend_num[i]+")'>수락</button></td>");
					    	  out.println("<td><button type='button' onclick='ffail(-1, "+fuser_num[i]+", "+ffriend_num[i]+")'>거절</button></td><tr></li>");
					      }
					      
					      out.println("</table>");
					      out.println("<script>");
					      out.println("function fsuccess(u_num, f_num) {");
					      out.println("location.href='friendProccess.jsp?u_num='+u_num+'&f_num='+f_num;");
					      out.println("}");
					      out.println("function ffail(flag, u_num, f_num) {");
					      out.println("location.href='friendProccess.jsp?u_num='+u_num+'&f_num='+f_num+'&flag='+flag;");
					      out.println("}");
					      out.println("</script>");
				      }
				      else {
				    	  out.println("친구요청이 없습니다.");
				      }
				      %>
				  </ul>
		      </li>
			</ul>
			<!-- right side menu -->
			<ul class="nav navbar-nav navbar-right">
			  <li><a href="profileDetails.jsp?user_num=<%=userNumString%>&target_user=<%=userNumString%>&locx=36&locy=128"><%=userNameString%>님 환영합니다.</a></li>
		      <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="glyphicon glyphicon-user"></i></a>
                        <ul class="dropdown-menu">
                          <li><a href="#settingModal" role="button" data-toggle="modal">설정</a></li>
                          <li><a href="index.jsp">로그아웃</a></li>
                        </ul>
               </li>
			</ul>		
		  </div>
	   </div>
	</div>
	
	<div id="settingModal" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
					설정
				</div>
				<div class="modal-body">
				설정할 내용
				</div>
			<div class="modal-footer">
					<div>
						<button type="button" class="btn btn-default" data-dismiss="modal">닫기</button>
						<button type="button" class="btn btn-primary" area-hidden="true">설정 저장</button>
					</div>	
				</div>
			</div>
		</div>
	</div>
	