<%@page contentType="text/html;charset=EUC-KR" %>
<%@page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>

<html>
<body>

<!-- script references -->
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
<script src="js/scripts.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="jquery-bootstrap-modal-steps.js"></script>

<%
	request.setCharacterEncoding("EUC-KR");

	String[] textarea = request.getParameterValues("textarea");
	String[] makefilepath = request.getParameterValues("makefilepath");
	String targetUserNumString = request.getParameter("targetUserNumString");
	String[] content_num = request.getParameterValues("content_num");
	String userNumString = request.getParameter("userNumString");
	String foundLocx = request.getParameter("locx");
	String foundLocy = request.getParameter("locy");
	String mapbounds = request.getParameter("mapbounds");
	String JspName = request.getParameter("jspName");
	int buttonNum=0;
	
	Connection conn=null;
	PreparedStatement pstmt=null;
	ResultSet rs=null;
	int max_num=1;
	
	Context init = new InitialContext();
	DataSource ds = (DataSource) init.lookup("java:comp/env/jdbc/mysql");
	conn = ds.getConnection();
	
	for(int i = 0; i < textarea.length; i++) {
		if(!textarea[i].equals("")) {
			buttonNum = i;
			
			String fileDir = "C:/Users/bu456/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/LeaveOutWeb/" + makefilepath[i]; //������ ������ ���丮
			String filePath = fileDir + "/" + userNumString; //������ ������ ��ü���
			
			try{
				File targetnameDir = new File(filePath); // ���ϰ�ü����
				if(!targetnameDir.exists()) {
					targetnameDir.mkdirs();
				}
			}catch (Exception e) { 
				System.out.println(e.toString()); //���� �߻��� �޽��� ���
			}
			
			try {
				pstmt=conn.prepareStatement("SELECT max(comm_num) as comm_num FROM comment");
				rs=pstmt.executeQuery();
				String textfile = filePath;
				
				if(rs.next()){
					max_num = rs.getInt("comm_num");
					max_num++;
				}
			
				textfile = textfile + "/" + max_num;
				File temp = new File(textfile); // ���ϰ�ü����
				if(!temp.exists()) {
					temp.mkdirs();
				}
				
				textfile = textfile + "/" + "text.txt";
				File f = new File(textfile); // ���ϰ�ü����
				f.createNewFile(); //���ϻ���
				
				FileWriter fw = new FileWriter(textfile); //���Ͼ��ⰴü����
				String data = textarea[buttonNum];
				fw.write(data); //���Ͽ��� �ۼ�
				fw.close(); //�����ڵ� �ݱ�
				
				String commetseq = "/leaveout/files/"+targetUserNumString+"/content/"+content_num[buttonNum]+
				           "/comment/"+userNumString+"/"+max_num;
				
				pstmt=conn.prepareStatement("insert into comment(comm_num, content_num, user_num, rec_cnt, reg_time, files) values(?,?,?,0,now(),?)");
				pstmt.setInt(1,max_num);
				pstmt.setInt(2,Integer.parseInt(content_num[buttonNum]));
				pstmt.setInt(3,Integer.parseInt(userNumString));
				pstmt.setString(4,commetseq);
				pstmt.executeUpdate();
				
				
			}catch(Exception e){
				e.printStackTrace();
			} 
		}
	}
%>

<script>
if("<%=JspName%>" == "profileDetails.jsp") {
	location.href="http://localhost:8080/<%=JspName%>?user_num=<%=userNumString%>&target_user=<%=targetUserNumString%>&locx=36&locy=128";
} else {
	location.href="http://localhost:8080/<%=JspName%>?user_num=<%=userNumString%>&bounds=<%=mapbounds%>";
}
</script>

</body>
</html>