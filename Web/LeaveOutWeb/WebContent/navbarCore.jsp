
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
                         <div class="input-group-btn">
							<a href="contentDetails.jsp?user_num=<%=userNumString%>" class="btn btn-default" role="button" type="submit"><i class="glyphicon glyphicon-search"></i></a>
                         </div>
                       </div>
                   </form>
			  </li>
		      <li><a href="posting.jsp?user_num=<%=userNumString%>">글쓰기</a></li>
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