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
		String targetUserNumString = request.getParameter("target_user");
		String targetUserNameString = "null";
		String foundLocx = request.getParameter("locx");
		String foundLocy = request.getParameter("locy");
		
		Connection conn=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		PreparedStatement pstmt4=null;
		ResultSet rs4=null;
	
		try {
			Context init = new InitialContext();
			DataSource ds = (DataSource) init.lookup("java:comp/env/jdbc/mysql");
			conn = ds.getConnection();
  		
			pstmt=conn.prepareStatement("SELECT * FROM user WHERE user_num=?");
			pstmt.setString(1,userNumString);
			rs=pstmt.executeQuery();
			
			pstmt4=conn.prepareStatement("SELECT * FROM user WHERE user_num=?");
			pstmt4.setString(1,targetUserNumString);
			rs4=pstmt4.executeQuery();
  		
			if(rs.next() && rs4.next()){
				//sesson-OK
				userNameString = rs.getString("name");
				targetUserNameString = rs4.getString("name");
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
  		
  		pstmt2=conn.prepareStatement("SELECT loc_x, loc_y FROM content where user_num=?");
  		pstmt2.setString(1,targetUserNumString);
		rs2=pstmt2.executeQuery();
		
		while(rs2.next()){
			String resultx = rs2.getString("loc_x");
			String resulty = rs2.getString("loc_y");
			
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
	
	<!-- contents -->
	<div id="profile_contents" class="col-md-10">
		<%@ include file="./profileContents.jsp"%>
	</div>
	 
	<div id="friends_List" class="col-md-2">
	 <%@ include file="friendsList.jsp"%>
	 </div>
	
	<script>
      function initMap() {
		  var setloc = {lat:<%=foundLocx%>, lng:<%=foundLocy%>};
          var map = new google.maps.Map(document.getElementById('map'), {
          zoom: 7,
          center: setloc
        });

        // Create an array of alphabetical characters used to label the markers.
        var labels = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';

        // Add some markers to the map.
        // Note: The code uses the JavaScript Array.prototype.map() method to
        // create an array of markers based on a given "locations" array.
        // The map() method here has nothing to do with the Google Maps API.
        markers = locations.map(function(location, i) {
          var marker = new google.maps.Marker({
              position: location,
              label: labels[i % labels.length]
          });
		  
		  google.maps.event.addListener(marker, 'click', function() {
			  var lat = location.lat;
		      var lng = location.lng;
		      window.location.href="contentView.jsp?user_num=<%=userNumString%>&locx="+lat+"&locy="+lng;
		  });
		
		  return marker;
        });

        // Add a marker clusterer to manage the markers.
        var markerCluster = new MarkerClusterer(map, markers,
            {imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'}
        );        
    }
    </script>
    
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
	
	<!-- Google Map Script -->
	<script async defer
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDJ0-6wfd7a6AVfTR2HdzA3QQtlXwx51S4&callback=initMap">
    </script>
	  <script src="https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/markerclusterer.js">
    </script>
	
	<%@ include file="./navbarCore.jsp" %>
	
	</body>
</html>