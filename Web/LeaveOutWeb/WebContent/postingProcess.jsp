#<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>
<%@ page import="com.oreilly.servlet.MultipartRequest" %>
<%@ page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy" %>
<%@ page import="java.io.*" %>


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
	<!-- session & parameter check -->
	<%
	
	String uploadPath = request.getRealPath("leaveout");
	int size = 15*1024*1024;
	
	MultipartRequest multi = new MultipartRequest(request,
				uploadPath, size, "EUC-KR", new DefaultFileRenamePolicy());
	String userNumString = multi.getParameter("user_num"); 
	String uploadedContentString = multi.getParameter("uploadContent");
	String selectedCheckString = multi.getParameter("selectCheck");
	String userNameString = null;
	int max_num = 1;
	
	
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
	
	
	PreparedStatement pstmt9=null;
	ResultSet rs9=null;
	
	String confileDir = "C:/Users/sudiWIN/eclipse-workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/LeaveOutWeb/leaveout/files/" + userNumString; //파일을 생성할 디렉토리
	String confilePath = confileDir + "/" + "content"; //파일을 생성할 전체경로
	
	String selectedCheckAddrStr = null;
	float checkLocX = 0;
	float checkLocY = 0;
	
	try{
		File contargetnameDir = new File(confilePath); // 파일객체생성
		if(!contargetnameDir.exists()) {
			contargetnameDir.mkdirs();
		}
	}catch (Exception e) { 
		System.out.println(e.toString()); //에러 발생시 메시지 출력
	}
	
	try {
		pstmt9=conn.prepareStatement("SELECT max(content_num) as content_num FROM content");
		rs9=pstmt9.executeQuery();
		String textfile = confilePath;
		
		if(rs9.next()){
			max_num = rs9.getInt("content_num");
			max_num++;
		}
	
		textfile = textfile + "/" + max_num;
		File temp = new File(textfile); // 파일객체생성
		if(!temp.exists()) {
			temp.mkdirs();
		}
		
		textfile = textfile + "/" + "text.txt";
		File f = new File(textfile); // 파일객체생성
		f.createNewFile(); //파일생성
		
		FileWriter fw = new FileWriter(textfile); //파일쓰기객체생성
		String data = uploadedContentString;
		fw.write(data); //파일에다 작성
		fw.close(); //파일핸들 닫기
		
		String commetseq = "/leaveout/files/"+userNumString+"/content/"+max_num;
		
		

		PreparedStatement pstmt10=null;
		ResultSet rs10=null;
		
		pstmt10 = conn.prepareStatement("select * FROM checks where user_num=?");
		pstmt10.setString(1,userNumString);
		rs10 = pstmt10.executeQuery();
		int chkcnt = 0;
		int selectedCheckInt = Integer.parseInt(selectedCheckString, 10);
		while(rs10.next()){
			if(rs10.getString("check_image") != "null") {
				chkcnt++;
			}
			if(chkcnt == selectedCheckInt){
				switch(chkcnt){
				case 1:
					selectedCheckAddrStr = multi.getParameter("checkLocation1");
					break;
				case 2:
					selectedCheckAddrStr = multi.getParameter("checkLocation2");
					break;
				case 3:
					selectedCheckAddrStr = multi.getParameter("checkLocation3");
					break;
				default:
						break;
				}
				checkLocX = rs10.getFloat("chk_x");
				checkLocY = rs10.getFloat("chk_y");
			}
		}
		
		//geocode 스크립트 넣기 안넣어도될듯
		%>
		<% 
		
		String imsistr = new String(selectedCheckAddrStr.getBytes("KSC5601"), "8859_1");
		String uploadSelectedCheckAddrStr = new String(imsistr.getBytes("8859_1"),"MS949");
		pstmt9=conn.prepareStatement("insert into content(content_num, user_num, view_cnt, rec_cnt, reg_time, visibility, fence, loc_x, loc_y, address, files) values(?, ?, 0, 0, now(), 1, 0, ?, ?, ?, ?)");
		pstmt9.setInt(1,max_num);
		pstmt9.setString(2,userNumString);
		pstmt9.setFloat(3,checkLocX);
		pstmt9.setFloat(4,checkLocY);
		pstmt9.setString(5,uploadSelectedCheckAddrStr);
		pstmt9.setString(6,commetseq);
		pstmt9.executeUpdate();
		
		
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
	
	
	</body>
</html>  
    

</body>
</html>
