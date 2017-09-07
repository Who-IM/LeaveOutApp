<%
	String profilePicTarget = ".\\leaveout\\files\\" + targetUserNumString + "\\profile\\1.jpg";
%>

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
		<button type="button" class="btn btn btn-primary">
			<i class="glyphicon glyphicon-tags"></i> 영진전문대
		</button>
		<button type="button" class="btn btn btn-success">
			<i class="glyphicon glyphicon-tags"></i> 먹방
		</button>
		<button type="button" class="btn btn btn-info">
			<i class="glyphicon glyphicon-tags"></i> 여행
		</button>
		<button type="button" class="btn btn-warning">
			<i class="glyphicon glyphicon-tags"></i> 탈주!
		</button>
		<button type="button" class="btn btn-danger">
			<i class="glyphicon glyphicon-tags"></i> 일상
		</button>
		<button type="button" class="btn btn-default">
			<i class="glyphicon glyphicon-tags"></i> 경주
		</button>
		
		<br><br>
		<ul class="list-group">
			<li class="list-group-item">
				<div class="media">
					<a class="pull-left" href="#">
						<img class="media-object" src="profile_default.jpg" width="50" height="50" alt="...">
					</a>
					<div class="media-body">
						<h4 class="media-heading">정말로 재미있었다. 굿굿<br></h4>
							<i class="glyphicon glyphicon-flag"></i>영진전문대 에서 2017.07.31. 11:32<br>
							<div id="carousel-example-generic" class="carousel slide">
							<!-- Indicators -->
							<ol class="carousel-indicators">
								<li data-target="#carousel-example-generic" data-slide-to="0" class="active"></li>
								<li data-target="#carousel-example-generic" data-slide-to="1"></li>
							</ol>

								<!-- Wrapper for slides -->
								<div class="carousel-inner">
									<div class="item active">
										<img src="./content_default1.jpg" alt="...">
											<div class="carousel-caption">
											<h3>h3도 쓸수 있어요1</h3>
											<p>Hello World!.1</p>
											</div>
									</div>
									<div class="item">
										<img src="./content_default2.jpg" alt="...">
											<div class="carousel-caption">
											<h3>h3도 쓸수 있어요2</h3>
											<p>Hello World!..2</p>
											</div>
									</div>
								</div>

								<!-- Controls -->
								<a class="left carousel-control" href="#carousel-example-generic" data-slide="prev">
									<span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
								</a>
								<a class="right carousel-control" href="#carousel-example-generic" data-slide="next">
									<span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
								</a>
							</div>
						최수디님의 글(4시간 전)
						<div class="media">
							<a class="pull-left" href="#">
								<img class="media-object" src="profile_default.jpg" width="50" height="50" alt="...">
							</a>
							<div class="media-body">
							<h4 class="media-heading">우와, 정말로 재밌었겠다!</h4>
							박수디님의 댓글 (3시간 전)<br>
							2017.07.31. 12:32<br>
							</div>
						</div>
					</div>
				</div>
			</li>
		</ul>
		
</div>