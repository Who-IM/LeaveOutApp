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
	  #profile_contents {
		padding-top : 40px;
      }
	  #map {
        height: 100%;
      }
     </style>
</head>

<body>

	<!-- session check -->
	<%
		String userNumString = request.getParameter("user_num");
		String userNameString = "null";
		String foundLocx = request.getParameter("locx");
		String foundLocy = request.getParameter("locy");
		
		
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
	
		try {
			Context init = new InitialContext();
			DataSource ds = (DataSource) init.lookup("java:comp/env/jdbc/MysqlDB");
			conn = ds.getConnection();
  		
			pstmt=conn.prepareStatement("SELECT * FROM user WHERE user_num=?");
			pstmt.setString(1,userNumString);
			rs=pstmt.executeQuery();
  		
			if(rs.next()){
				//sesson-OK
				userNameString = rs.getString("name");
			}
			else {
				out.println("<script>");
				out.println("alert('존재하지 않는 회원입니다.');");
				out.println("location.href='index.jsp'");
				out.println("</script>");
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
	%>
	
	<!-- create Map marker info -->
	<%
	out.println("<script>");
	out.println("var locations = [");
		
	PreparedStatement pstmt2=null;
	PreparedStatement pstmt3=null;
	ResultSet rs2=null;
	ResultSet rs3=null;
	
	int cnt = 0;
	try {
  		//Context init = new InitialContext();
  		//DataSource ds = (DataSource) init.lookup("java:comp/env/jdbc/MysqlDB");
  		//conn = ds.getConnection();
  		
  		pstmt2=conn.prepareStatement("SELECT loc_x FROM content");
		rs2=pstmt2.executeQuery();
		
		pstmt3 = conn.prepareStatement("SELECT loc_y FROM content");
  		rs3=pstmt3.executeQuery();
		
		while(rs2.next() && rs3.next()){
			String resultx = rs2.getString("loc_x");
			String resulty = rs3.getString("loc_y");
			
			if(cnt != 0){
				out.print(",");
			}
			out.println("{lat: " + resultx +", lng: " + resulty + "}");
			cnt++;
		}
	
	}catch(Exception e){
		e.printStackTrace();
 	}
	
	out.println("]");
	out.println("</script>");
	%>
	
	
	<!-- Google Map Script -->
	<script async defer
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyC15raLc2ZNVvQ86f5xEHAsKBC57KiMx7s&callback=initMap">
    </script>
	  <script src="https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/markerclusterer.js">
    </script>
	
	
	<script>
      // Note: This example requires that you consent to location sharing when
      // prompted by your browser. If you see the error "The Geolocation service
      // failed.", it means you probably did not give permission for the browser to
      // locate you.

      function initMap() {
		  var setloc = {lat:<%=foundLocx%>, lng: <%=foundLocy%>};
          var map = new google.maps.Map(document.getElementById('map'), {
          zoom: 13,
          center: setloc
        });

        // Create an array of alphabetical characters used to label the markers.
        var labels = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';

        // Add some markers to the map.
        // Note: The code uses the JavaScript Array.prototype.map() method to
        // create an array of markers based on a given "locations" array.
        // The map() method here has nothing to do with the Google Maps API.
        var markers = locations.map(function(location, i) {
          return new google.maps.Marker({
            position: location,
            label: labels[i % labels.length]
          });
        });

        // Add a marker clusterer to manage the markers.
        var markerCluster = new MarkerClusterer(map, markers,
            {imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'});
      }
    </script>

	<!-- script references -->
	<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
	<script src="js/scripts.js"></script>
	<script src="js/bootstrap.min.js"></script>
		<script src="jquery-bootstrap-modal-steps.js"></script>
	<script type="text/javascript">
	// iframe resize
	function autoResize(i)
	{
		var iframeHeight=
		(i).contentWindow.document.body.scrollHeight;
		(i).height=iframeHeight+20;
		var iframeWidth=
		(i).contentWindow.document.body.scrollWidth;
		(i).width=iframeWidth+20;
	}
</script>
    
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
			  <li><a href="profileDetails.jsp?user_num=<%=userNumString%>"><%=userNameString%>님 환영합니다.</a></li>
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
	<!-- contents -->
	
	 <div id="profile_contents" class="col-md-10">
	 	<div class="media">
			<a class="pull-left" href="#">
				<img class="media-object" src="profile_default.jpg" width="50" height="50" alt="...">
			</a>
			<a class="pull-right" href="#">
				<button type="button" class="btn btn-default">
				<i class="glyphicon glyphicon-plus-sign"></i> 친구 추가
				</button>
			</a>
			<div class="media-body">
				<h2 class="media-heading">최수디 님</h2>
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit"<br>
			</div>
		</div>
		<div class="col-md-6">
			<h5><i class="glyphicon glyphicon-info-sign"></i>최수디님이 어디어디에 글을 썼는지 확인해보세요.</h5>
			<div id="map"></div>
		</div>
		<div class="col-md-6">
		<h5><i class="glyphicon glyphicon-info-sign"></i>최수디님이 어떤 카테고리로 글을 썼는지 확인해보세요.</h5>
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
	</div>
	 <iframe src="friendsList.jsp" class="col-md-2" onload="autoResize(this);" scrolling="no" frameborder="0"></iframe>
	   

	</body>
</html>