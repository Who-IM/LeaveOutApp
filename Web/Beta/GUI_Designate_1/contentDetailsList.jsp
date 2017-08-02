<%@ page language="java" contentType="text/html; charset=EUC-KR"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>



<html>
<head>
    <title>LeaveOut</title>
	
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	
	
	<meta name="generator" content="Bootply" />
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

	<!-- Bootstrap Core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">
	<!--[if lt IE 9]>
		<script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->
		
		
	<style>
	  body {
		padding-top : 40px;
      }
	 
	  #map {
        height: 100%;
      }
     </style>
</head>

<body>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/scripts.js"></script>
	<script src="jquery.min.js"></script>
	<!-- session check -->
	
	<!-- contents -->
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
	
	   
	<!-- pager -->
	<div class="footer" align="center">
		<ul class="pagination">
			<li class="disabled"><a href="#">≪</a></li>
			<li class="active"><a href="#">1</a></li>
			<li><a href="#">2</a></li>
			<li><a href="#">3</a></li>
			<li><a href="#">4</a></li>
			<li><a href="#">5</a></li>
			<li><a href="#">≫</a></li>
		</ul>
	</div>
	
	
	</body>
</html>