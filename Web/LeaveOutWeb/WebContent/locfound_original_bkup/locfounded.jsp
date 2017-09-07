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
	<script src="js/bootstrap.min.js"></script>
	<script src="js/scripts.js"></script>
            
	<!-- Contents Start -->
	<div class="navbar navbar-default navbar-fixed-top">
	  <div class="container">
		<!-- brand name -->
		<a class="navbar-brand" href="#">LeaveOut</a>
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
			  <li><a href="main.jsp?user_num=<%=userNumString%>">홈</a></li>
			  <li><a href="#">모아보기</a></li>
		      <li><a href="#">글쓰기</a></li>
			</ul>
			<!-- right side menu -->
			<ul class="nav navbar-nav navbar-right">
			  <li><a href="#"><%=userNameString%>님 환영합니다.</a></li>
		      <li><a href="index.jsp">로그아웃</a></li>
			</ul>			
		  </div>
	   </div>
	</div>
	
	<!-- contents -->
	
	 <div id="map"></div>
	   
	   
	
	
	</body>
</html>