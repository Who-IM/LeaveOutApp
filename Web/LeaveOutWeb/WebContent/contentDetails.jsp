<%@page import="java.util.StringTokenizer"%>
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
	  #friends_List{
		  padding-top : 40px;
	  }
	  #map {
        height: 100%;
      }
	  #content_Details_List{
		  padding-top : 40px;
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
		String mapbounds = request.getParameter("bounds");
		
		String Processbounds = "((";
		String temp = "";
		String bounds[] = new String[4];
		int bocnt = 0;
		boolean flag = false;
		
		// 배열을 하나하나씩 분리
		for(int i = 0; i < mapbounds.length(); i++) {
			if(mapbounds.charAt(i)>='0' && mapbounds.charAt(i)<='9' || mapbounds.charAt(i)=='.') {
				temp += mapbounds.charAt(i);
				bounds[bocnt] = temp;
			}
			else if(mapbounds.charAt(i)==',' || mapbounds.charAt(i)==')') {
				temp = "";
				bocnt++;
				
				if(bocnt==2 && !flag) {
					bocnt--;
					flag = true;
				}
			}
			if(bocnt==4) {
				break;
			}
		}
		
		// 댓글입력시 다시띄울 모아보기 좌표
		for(int i = 0; i < bounds.length; i++) {
			switch(i) {
			case 0: Processbounds += bounds[i]+","; break;
			case 1: Processbounds += bounds[i]+"),("; break;
			case 2: Processbounds += bounds[i]+","; break;
			case 3: Processbounds += bounds[i]+"))"; break;
			}
		}
		
		// 초기 map 보여줄 zoom ,x, y 셋팅
		String zoom = "13";
		if(foundLocx==null) {
			foundLocx="37";
			foundLocy="127";
			zoom="7";
		}
		
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
	
		try {
			Context init = new InitialContext();
			DataSource ds = (DataSource) init.lookup("java:comp/env/jdbc/mysql");
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
 
	<!-- contents -->
	 <div id="map" class="col-md-6">
	 <script>
	 var map;
      function initMap() {
		  var setloc = {lat:<%=foundLocx%>, lng: <%=foundLocy%>};
		  map = new google.maps.Map(document.getElementById('map'), {
          	zoom: <%=zoom%>,
          	center: setloc
          });
        
          var labels = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
          
          var markers = locations.map(function(location, i) {
            var marker = new google.maps.Marker({
              position: location,
              label: labels[i % labels.length]
            });
		  
		    google.maps.event.addListener(marker, 'click', function() {
			  alert("마커클릭");
		    });
		  
		    return marker;
          });
	
          var markerCluster = new MarkerClusterer(map, markers, 
              {imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'}
          );
            
      }
      </script>
	 </div>
	 
	 <div id="content_Details_List" class="col-md-6">
	   <%@ include file="./contentDetailsList.jsp"%>
	 </div>
	 
	 <script src="https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/markerclusterer.js"></script>
	 <!-- Google Map Script -->
	 <script async defer
 		  src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDJ0-6wfd7a6AVfTR2HdzA3QQtlXwx51S4&callback=initMap">
   	 </script>
	
	<%@ include file="./navbarCore.jsp" %>
	
	</body>
</html>