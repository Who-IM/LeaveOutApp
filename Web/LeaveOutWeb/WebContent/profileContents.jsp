<%@ page language="java" contentType="text/html; charset=EUC-KR"%>
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>

<% String profilePicTarget = ".\\leaveout\\files\\"+targetUserNumString+"\\profile\\1.jpg"; %>

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
	
	<%
	String picNum = null;
	PreparedStatement pstmt5=null;
	ResultSet rs5=null;

	PreparedStatement pstmtContent=null;
	ResultSet rsContent=null;
	
	try {
		pstmt5=conn.prepareStatement("SELECT cate_text FROM category WHERE user_num=?");
		pstmt5.setString(1,targetUserNumString);
		rs5=pstmt5.executeQuery();
 		
		while(rs5.next()){
			out.println("<button type='button' class='btn btn btn-primary'>");
			out.println("<i class='glyphicon glyphicon-tags'></i>   "+rs5.getString("cate_text"));
			out.println("</button>");
		}
	}catch(Exception e){
		e.printStackTrace();
	}		
	%>
	
	
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">

	<script>
	var slideIndex = 1; // 초기페이지 
	
	function plusDivs(n , contentseq) {
	  showDivs(slideIndex += n, contentseq);
	}
	
	function showDivs(n, contentseq) {
	  var i;
	  var x = document.getElementsByClassName("mySlides"+contentseq);
	  if (n > x.length) {slideIndex = 1}    
	  if (n < 1) {slideIndex = x.length}
	  for (i = 0; i < x.length; i++) {
	     x[i].style.display = "none";  
	  }
	  $("#mypage"+contentseq).text(slideIndex); // 현제페이지 설정
	  $("#totalpage"+contentseq).text(x.length); // total 페이지 설정
	  x[slideIndex-1].style.display = "block";
	}
	</script>

	<script>
	function default_Img() {
		document.getElementById("profileimg").src = "profile_default.jpg";
	}
	</script>

	<br><br>
	<ul class="list-group">
		<li class="list-group-item">
			<div class="media">
				<%
				PreparedStatement pstmt7=null;
				ResultSet rs7=null;
				String contentNum = null;
				String contentTarget = null;
				String contentPicTarget = null;
				int contentseq=0;
				
				try {
					pstmt7=conn.prepareStatement("SELECT * from content where user_num=?;");
					pstmt7.setString(1,targetUserNumString);
					rs7=pstmt7.executeQuery();  	

					while(rs7.next()){
						contentseq++;
						String regtime = rs7.getString("reg_time");
						
						// content의 프로필 사진
						out.println("<a class='pull-left' href='#'>");
						out.println("<img class='media-object' id='profileimg' src="+profilePicTarget+" width='50' height='50' onerror='default_Img();' alt='...'></a>");
						
						// 제목 시간
						out.println("<h4 class='media-heading'>"+rs7.getString("address")+"<br></h4>");
						out.println("<i class='glyphicon glyphicon-flag'></i>"+regtime.substring(0, regtime.length()-2)+"<br>");
						
						// 사진 파일 경로 설정
						File path = new File("C:\\Users\\sudiWIN\\eclipse-workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps"
								+ "\\LeaveOutWeb\\leaveout\\files\\"+targetUserNumString+"\\content\\"+rs7.getString("content_num"));
						String files[] = path.list();
						int number = files.length - 1;
						for(int i = 0; i < files.length; i++) {
							if(files[i].equals("comment")) {
								number -= 1;
							}
						}
						
						// class, id name 설정
						String slidename = "mySlides"+contentseq;
						String mypage = "mypage"+contentseq;
						String totalpage = "totalpage"+contentseq;
						
						// 이미지 띄우기 (slide)
						out.println("<div class='w3-content w3-display-container'>");
						for(int i = 1; i <= number; i++){
							contentPicTarget = ".\\leaveout\\files\\"+targetUserNumString+"\\content\\"+rs7.getString("content_num")+"\\"+i+".jpg";
							out.println("<img class="+slidename+" src="+contentPicTarget+" style='width:100%; height:60%'/>");
						}
						
						if(number > 1) {
							// 사진안에 페이지 수
							out.println("<div class='carousel-caption'>");
							out.println("<b id="+mypage+" style='color:#2A241A'></b>"); // 현재페이지 -> javascript로 반영
							out.println("<b id='temp' style='color:#2A241A'>/</b>");
							out.println("<b id="+totalpage+" style='color:#2A241A'></b>"); // total페이지 -> javascript로 반영
							out.println("</div>");
							// End - 사진안에 페이지 수
							
							// 이미지 변경 button ( "<" ">" )
							out.println("<button class='w3-button w3-black w3-display-left' onclick='plusDivs(-1, "+contentseq+")'>&#10090;</button>");
							out.println("<button class='w3-button w3-black w3-display-right' onclick='plusDivs(1, "+contentseq+")'>&#10091;</button>");
							out.println("</div>");
							// End 이미지 띄우기
						}
						
						//텍스트 파일 위치 컴퓨터 마다 경로 변경
						contentTarget = "C:\\Users\\sudiWIN\\eclipse-workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps"
						+ "\\LeaveOutWeb\\leaveout\\files\\"+targetUserNumString+"\\content\\"+rs7.getString("content_num")+"\\text.txt";
						
						// 글내용 셋팅
						FileReader fr = new FileReader(contentTarget); //파일읽기객체생성
						BufferedReader br = new BufferedReader(fr); //버퍼리더객체생성
						String line = null; 
						while((line=br.readLine())!=null){ //라인단위 읽기
						    out.println(line); 
						}
						out.println("<hr>");
						br.close();
						fr.close();
						
						// 초기 이미지 셋팅
						out.println("<script>");
						for(int i = 1; i <= contentseq; i++){
							out.println("showDivs(1, "+i+");");
						}
						out.println("</script>");
						
						File commentpath = new File(path + "\\comment");
						String commentfiles[] = commentpath.list();
						if(commentfiles != null) {
							for(int i = 0; i < commentfiles.length; i++) {
								String commentImagePath = ".\\leaveout\\files\\"+commentfiles[i]+"\\profile\\1.jpg";
								String commentTarget = "C:\\Users\\sudiWIN\\eclipse-workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps"
										+ "\\LeaveOutWeb\\leaveout\\files\\"+targetUserNumString+"\\content\\"+rs7.getString("content_num")+
										"\\comment\\"+commentfiles[i];
								
								File commentseqpath = new File(commentTarget);
								String commetseqfiles[] = commentseqpath.list();
								
								// 댓글이 존재할 경우
								if(commetseqfiles != null) {
									for(int j = 0; j < commetseqfiles.length; j++) {
										String commetReaderpath = commentTarget + "\\" + commetseqfiles[j] + "\\text.txt";
										String commetseq = "/leaveout/files/"+targetUserNumString+"/content/"+rs7.getString("content_num")+
												           "/comment/"+commentfiles[i]+"/"+commetseqfiles[j];
										
										FileReader fr2 = new FileReader(commetReaderpath); //파일읽기객체생성
										BufferedReader br2 = new BufferedReader(fr2); //버퍼리더객체생성
										
										out.println("<div class='media'>");
										out.println("<a class='pull-left' href='#'>");
										out.println("<img src="+commentImagePath+" width='50' height='50'></a>");
										out.println("<div class='media-body'>");
											
										out.println("<h5 class='media-heading'>");
										String line2 = null; 
										while((line2=br2.readLine())!=null){ //라인단위 읽기
										    out.println(line2); 
										}
										out.println("</h5>");
										
										// 사진 및 시간
										PreparedStatement commentinfops=null;
										ResultSet commentinfors=null;
												
										commentinfops=conn.prepareStatement("select user.name,comment.user_num, comment.reg_time from comment " +
												                            "INNER JOIN user on user.user_num = ? " +
												                            "where comment.files = ?;");
										commentinfops.setString(1,commentfiles[i]);
										commentinfops.setString(2,commetseq);
										commentinfors=commentinfops.executeQuery();

										while(commentinfors.next()){
											String time = commentinfors.getString("reg_time");
											out.println(commentinfors.getString("name")+"님의 댓글<br>");
											out.println(time.substring(0, time.length()-2)+"<br>");
										}
										out.println("</div>");
										out.println("</div>");
									}
								}
							}
							out.println("<br>");
						}
						
						String areaid = "textarea" + contentseq;
						String makefilepath = "/leaveout/files/"+targetUserNumString+"/content/"+rs7.getString("content_num")+
                                			  "/comment";
						
						out.println("<div");
						out.println("<label>");
						out.println("<i class='glyphicon glyphicon-font'></i> 댓글을 입력해주세요.<br>");
						out.println("</label><div>");
						
						out.println("<form method='post' action='profileContentsProcess.jsp'");
						out.println("<input type='hidden' name='hiddenarea'></input>");
						out.println("<input type='hidden' name='makefilepath' value="+makefilepath+"></input>");
						out.println("<input type='hidden' name='targetUserNumString' value="+targetUserNumString+"></input>");
						out.println("<input type='hidden' name='content_num' value="+rs7.getString("content_num")+"></input>");
						out.println("<input type='hidden' name='userNumString' value="+userNumString+"></input>");
						out.println("<div class='row'>");
						out.println("<div class='col-md-10'>");
						out.println("<textarea class='form-control' id="+areaid+" name='textarea' rows='3' placeholder='글 내용을 입력해 주세요.'></textarea></div>");
						out.println("<div class='col-md-2'>");
						out.println("<button type='submit' class='btn btn-default' style='height:76px' onclick='commentsubmit("+contentseq+")'>완료</button></div></div><form><br>");
					}
					
				}catch(Exception e){
					e.printStackTrace();
				}	
				
				%>
			</div>
	<script>
	function commentsubmit(contentseq) {
		var xx = $("#textarea"+contentseq).val();
		$("input[name=hiddenarea]").val(xx);
	}
	</script>
		</li>
	</ul>
</div>