<%@ page contentType="text/html;charset=EUC-KR" %>
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>

<html>
<body>

<!-- script references -->
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
<script src="js/scripts.js"></script>

<%
	String searchs = new String(request.getParameter("searchs").getBytes("ISO-8859-1"),"KSC5601");
	String userNumString = request.getParameter("userNum");
	String targetNumString = null;
	boolean nameflag = false;
	boolean addressflag = false;
	String locx = null;
	String locy = null;

	// 이름검색
	Connection conn=null;
	PreparedStatement pstmt=null;
	ResultSet rs=null;
	
	try {
		Context init = new InitialContext();
		DataSource ds = (DataSource) init.lookup("java:comp/env/jdbc/mysql");
		conn = ds.getConnection();
		
		pstmt=conn.prepareStatement("select name from user");
		rs=pstmt.executeQuery();
		
		while(rs.next()){
			if(searchs.equals(rs.getString("name"))) {
				nameflag = true;
				break;
			}
		}
	}catch(Exception e){
		e.printStackTrace();
	}
	
	// 검색한게 이름일 경우
	if(nameflag == true) {
		try {
			PreparedStatement numst=null;
			ResultSet numrs=null;
			
			numst=conn.prepareStatement("select user_num from user where name = ?");
			numst.setString(1,searchs);
			numrs=numst.executeQuery();
			
			while(numrs.next()){
				targetNumString = numrs.getString("user_num");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	// address 검색
	PreparedStatement st2=null;
	ResultSet rs2=null;
	
	try {
		st2=conn.prepareStatement("select address from content");
		rs2=st2.executeQuery();
		
		while(rs2.next()){
			if(searchs.equals(rs2.getString("address"))) {
				addressflag = true;
				break;
			}
		}
	}catch(Exception e){
		e.printStackTrace();
	}
	
	// 검색한게 address일 경우
		if(addressflag == true) {
			try {
				PreparedStatement addressst=null;
				ResultSet addressrs=null;
				
				addressst=conn.prepareStatement("select loc_x, loc_y from content where address = ?");
				addressst.setString(1,searchs);
				addressrs=addressst.executeQuery();
				
				while(addressrs.next()){
					locx = addressrs.getString("loc_x");
					locy = addressrs.getString("loc_y");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
%>

<script>
if(<%=nameflag%>==true) {
	location.href="http://localhost:8080/profileDetails.jsp?user_num=<%=userNumString%>&target_user=<%=targetNumString%>&locx=36&locy=128"
}

if(<%=addressflag%>==true) {
	location.href="http://localhost:8080/contentView.jsp?user_num=<%=userNumString%>&locx=<%=locx%>&locy=<%=locy%>"
}

if(<%=nameflag%>==false && <%=addressflag%>==false) {
	alert("검색 결과가 없습니다.");
	var referrer2 =  document.referrer;
	location.href = referrer2;
}
</script>

</body>
</html>