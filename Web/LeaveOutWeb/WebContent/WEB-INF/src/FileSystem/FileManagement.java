package FileSystem;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Base64;

import DBSQLServer.DBSQL;

public class FileManagement {
	
	public void fileTextUpload(JSONObject jsonupload, HttpServletRequest request) {
		int usernum = ((Long)jsonupload.get("usernum")).intValue();
		String path = (String)jsonupload.get("path");
		String text = (String)jsonupload.get("text");
		String filedirstring = null;
		String filedir = null;
		DBSQL dbsql = new DBSQL();		// SQL 전용 관리 객체 생성
		String sql = null;
		int contentnum = 0;
		if(path.equals("content")) {	// 게시글
			sql = "select content_num from content where user_num = " + usernum + " order by content_num desc Limit 1";
			JSONObject selectdata = dbsql.getPhoneSelect(sql,1);
			JSONArray array = (JSONArray) selectdata.get("result");
			contentnum = (int) ((JSONObject)array.get(0)).get("content_num");
			filedirstring = "/leaveout/files/" + usernum + "/" + path + "/" + contentnum;
		}
		
		filedir = request.getServletContext().getRealPath(filedirstring);		// 경로
		File dir = new File(filedir);
		if(!dir.exists()) dir.mkdirs();		// 폴더가 없을경우 만들기

		String realfiledir = filedir + "\\text.txt";		// 파일 만들기
		
		PrintWriter writer = null;					// 파일 업로드
		try {
			writer = new PrintWriter(realfiledir);
			writer.print(text);
			if(writer != null && path.equals("content")) {	// 게시글
				sql = "update content set files = \"" + filedirstring + "\" where content_num = " + contentnum;
				dbsql.getPhoneUpdate(sql);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {if(writer!=null) writer.close();}
	}
	
	public void fileImageUpload(JSONObject jsonupload, HttpServletRequest request) {
		int usernum = ((Long)jsonupload.get("usernum")).intValue();
		String path = (String)jsonupload.get("path");
		JSONArray imagearray = (JSONArray) jsonupload.get("array");
		byte[] decoded = null;		// 디코딩
		
		String filedirstring = null;
		String filedir = null;
		DBSQL dbsql = new DBSQL();		// SQL 전용 관리 객체 생성
		String sql = null;
		int contentnum = 0;
		if(path.equals("content")) {	// 게시글
			sql = "select content_num from content where user_num = " + usernum + " order by content_num desc Limit 1";
			JSONObject selectdata = dbsql.getPhoneSelect(sql,1);
			JSONArray array = (JSONArray) selectdata.get("result");
			contentnum = (int) ((JSONObject)array.get(0)).get("content_num");
			filedirstring = "/leaveout/files/" + usernum + "/" + path + "/" + contentnum;
		}
		
		filedir = request.getServletContext().getRealPath(filedirstring);		// 경로
		File dir = new File(filedir);
		if(!dir.exists()) dir.mkdirs();		// 폴더가 없을경우 만들기
		
		for(int i =0; i < imagearray.size(); i++) {
			decoded = Base64.getDecoder().decode((String)imagearray.get(i));
			try {
				BufferedImage image = ImageIO.read(new ByteArrayInputStream(decoded));
				ImageIO.write(image, "jpg", new File(filedir, (i+1)+".jpg"));
			} catch (IOException e) { e.printStackTrace();}
		}
	}

}
