
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
		      <li><a href="#postModal" role="button" data-toggle="modal">글쓰기</a></li>
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
	
	<!-- Posting Modal Menu -->
	
	<!-- Posting Modal Menu -->
	
	
	<div class="modal fade" id="postModal" tabindex="-1" role="dialog" aria-labelledby="postModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h4>글 쓰기</h4>
				</div>
				<div class="modal-body">
					<div class="row hide" data-step="1" data-title="finding checks">
						  <center><i class="glyphicon glyphicon-ok"></i> <%=userNameString%>님이 저장하신 체크입니다. 글 쓰기에 이용할 체크를 골라주세요.</center><br>
						<div class="col-md-6">여기에 지도 넣기<br></div>
						<div class="col-md-6">
							체크 1<br>
							체크 2<br>
							체크 3<br>
						</div>
					</div>
					<div class="row hide" data-step="2" data-title="posting contents">
					 <center><i class="glyphicon glyphicon-alert"></i> 선택한 체크로 글을 작성합니다. 글 쓴 이후에는 해당 체크가 삭제됩니다.</center><br>
						<form class="form center-block">
						<div class="form-group">
							<textarea class="form-control input-lg" autofocus="" placeholder="What do you want to share?"></textarea>
						</div>
						<button type="button" class="btn btn-default"><i class="glyphicon glyphicon-camera"></i> 사진 추가 </button>
						Not Pictures Found
					</form>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default js-btn-step pull-left" data-orientation="cancel" data-dismiss="modal"></button>
					<button type="button" class="btn btn-default js-btn-step" data-orientation="previous"></button>
					<button type="button" class="btn btn-default js-btn-step" data-orientation="next"></button>
				</div>
			</div>
		</div>
	</div>
	
	<script>
	$('#postModal').modalSteps();
	</script>
	
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