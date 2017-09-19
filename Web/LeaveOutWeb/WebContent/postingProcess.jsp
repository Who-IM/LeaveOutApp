<%@ page language="java" contentType="text/html; charset=EUC-KR"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>



<html>
<head>
    <title>LeaveOut posting...</title>
	
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
     </style>
</head>

<body>

	<!-- session check -->
	<%
		String userNumString = request.getParameter("user_num");
		String userNameString = "null";
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
            
	<%@ include file="./navbarCore.jsp"%>
	
	<!-- contents -->
	
	 <div id="map" class="col-md-10">
	 <script>
      // Note: This example requires that you consent to location sharing when
      // prompted by your browser. If you see the error "The Geolocation service
      // failed.", it means you probably did not give permission for the browser to
      // locate you.

      function initMap() {
        var map = new google.maps.Map(document.getElementById('map'), {
          center: {lat: 35.7982051, lng: 125.6298855},
          zoom: 6
        });
		
		var labels = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
        var infoWindow = new google.maps.InfoWindow({map: map});

        // Try HTML5 geolocation.
        if (navigator.geolocation) {
          navigator.geolocation.getCurrentPosition(function(position) {
            var pos = {
              lat: position.coords.latitude,
              lng: position.coords.longitude
            };

            infoWindow.setPosition(pos);
            infoWindow.setContent('위치를 찾았습니다.');
            map.setCenter(pos);
		    location.href='locfounded.jsp?user_num='+<%=userNumString%>+'&locx='+pos.lat+'&locy='+pos.lng;
          }, function() {
            handleLocationError(true, infoWindow, map.getCenter());
          });
        } else {
          // Browser doesn't support Geolocation
          handleLocationError(false, infoWindow, map.getCenter());
        }
      }

      function handleLocationError(browserHasGeolocation, infoWindow, pos) {
        infoWindow.setPosition(pos);
        infoWindow.setContent(browserHasGeolocation ?
                              '위치를 찾지 못했습니다.' :
                              '브라우저가 위치찾기를 지원하지 않습니다.');
		location.href='locfailed.jsp?user_num='+<%=userNumString%>;
      }
    </script>
	 
	</div>
	<div id="friends_List" class="col-md-2">
		<%@ include file="friendsList.jsp"%>
	</div>
	   
	<!-- Google Map Script -->
	<script async defer
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyD6-pEFLyPAV7u9lfsX5k98469JweBpebs&callback=initMap">
    </script>
	  <script src="https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/markerclusterer.js">
    </script>
	
	
	<%
	String selectedCheckString = request.getParameter("selectCheck");
	String uploadedImgFileString = request.getParameter("uploadImgFile");
	String uploadedContentString = request.getParameter("uploadContent");
	%>
	
	
	<script>
	alert("등록할 게시물은 다음과 같습니다.\n글쓴이 : " + <%=userNumString%> + "\n선택한 체크 : " + <%=selectedCheckString%> +  "\n올린 이미지 : " + <%=uploadedImgFileString%> + "\n");
	alert("게시 내용 : " + <%=uploadedContentString%>);
	
	</script>
	</body>
</html>  
    
    
    
    
    <!-- /container -->

</body>
</html>