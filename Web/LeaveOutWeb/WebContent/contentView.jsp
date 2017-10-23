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
		String Locx = request.getParameter("locx");
		String Locy = request.getParameter("locy");
		
		out.println("<script>");
		out.println("var locations = [{lat:"+Locx+",lng:"+Locy+"}];");
		out.println("</script>");
		
		Connection conn=null;
		PreparedStatement numst=null;
		ResultSet numrs=null;
	
		try {
			Context init = new InitialContext();
			DataSource ds = (DataSource) init.lookup("java:comp/env/jdbc/mysql");
			conn = ds.getConnection();
			
			numst=conn.prepareStatement("SELECT name FROM user WHERE user_num = ?");
			numst.setString(1,userNumString);
			numrs=numst.executeQuery();
  		
			if(numrs.next()){
				//sesson-OK
				userNameString = numrs.getString("name");
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
	
	<%
		PreparedStatement locationst=null;
		ResultSet locationrs=null;
		
		try {
			locationst=conn.prepareStatement("SELECT name FROM user WHERE user_num = ?");
			locationst.setString(1,userNumString);
			locationrs=locationst.executeQuery();
  		
			if(locationrs.next()){
				//sesson-OK
				userNameString = locationrs.getString("name");
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
	 <div id="map" class="col-md-5">
	 <script>
      function initMap() {
		  var setloc = {lat:<%=Locx%>, lng: <%=Locy%>};
		  var map = new google.maps.Map(document.getElementById('map'), {
          	zoom: 16,
          	center: setloc
          });
        
          var labels = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
          
          var markers = locations.map(function(location, i) {
            var marker = new google.maps.Marker({
              position: location,
              label: labels[i % labels.length]
            });
       
            google.maps.event.addListener(marker, 'mouseover', function() {
            	var infoWindow = new google.maps.InfoWindow({map: map});
                var geocoder = new google.maps.Geocoder();
      		    var mylatlng = new google.maps.LatLng(<%=Locx%>, <%=Locy%>);
      		    infoWindow.setPosition(setloc);
                
      		    geocoder.geocode({'latLng' : mylatlng}, function(results, status) {
      			    if (status == google.maps.GeocoderStatus.OK) {
      			  	    if (results[0]) {
      			  		    infoWindow.setContent(results[0].formatted_address);
      				    }
      			    } else {
      				    alert("Geocoder failed due to: " + status);
      			    }
      		    });
		    });
            
		    return marker;
          });
	
          var markerCluster = new MarkerClusterer(map, markers, 
              {imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'}
          );
      }
      </script>
	 </div>
	 
	 <div id="content_View_List" class="col-md-5">
	   <%@ include file="./contentViewList.jsp"%>
	 </div>
	 
	 <div id="friends_List" class="col-md-2">
		<%@ include file="friendsList.jsp"%>
	 </div>
	 
	 <script src="https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/markerclusterer.js"></script>
	 <!-- Google Map Script -->
	 <script async defer
 		  src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDJ0-6wfd7a6AVfTR2HdzA3QQtlXwx51S4&callback=initMap">
   	 </script>
	
	<%@ include file="./navbarCore.jsp" %>
	
	</body>
</html>