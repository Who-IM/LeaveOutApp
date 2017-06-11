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

public class FileUpload {

	private JSONObject jsonupload;
	private HttpServletRequest request;

	private DBSQL dbsql = new DBSQL();		// SQL 전용 관리 객체 생성
	private String sql;
	private String filedirstring;	// 서버 경로
	private String filedir;			// 실제 서버 절대 경로

	private int usernum;		// 유저 번호
	private String path;		// 저장 할 실제 이름(어디서 저장됬는지 확인)
	private int pathnum;		// 실제 저장 할곳의 데이터베이스 번호

	public FileUpload(JSONObject jsonupload, HttpServletRequest request) {
		this.jsonupload = jsonupload;
		this.request = request;
		init();
	}
 
	// 초기화
	public void init() {
		usernum = ((Long)jsonupload.get("usernum")).intValue();
		path = (String)jsonupload.get("path");

		if(path.equals("content")) {	// 게시글
			sql = "select content_num from content where user_num = " + usernum + " order by content_num desc Limit 1";
			JSONObject selectdata = dbsql.getPhoneSelect(sql,1);
			JSONArray array = (JSONArray) selectdata.get("result");
			pathnum = (int) ((JSONObject)array.get(0)).get("content_num");
			filedirstring = "/leaveout/files/" + usernum + "/" + path + "/" + pathnum;
		}
		filedir = request.getServletContext().getRealPath(filedirstring);		// 실제 경로
		File dir = new File(filedir);
		if(!dir.exists()) dir.mkdirs();		// 폴더가 없을경우 만들기
	}

	// text 파일 업로드
	@SuppressWarnings("unchecked")
	public JSONObject fileTextUpload() {
		JSONObject resJSON = null; // 응답용 데이터

		String text = (String)jsonupload.get("text");
		if(text != null) {
			String realfiledir = filedir + "\\text.txt";		// 파일 만들기

			PrintWriter writer = null;					// 파일 업로드
			try {
				writer = new PrintWriter(realfiledir);
				writer.print(text);
				
				if(writer != null) {	// 게시글
					if(updateFilesPath() == null) return null;
				}
				
				resJSON = new JSONObject(); 	// 응답용 데이터 객체 생성
				resJSON.put("result", 1);	// 결과값 
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			finally {if(writer!=null) writer.close();}
		}

		return resJSON;
	}

	// image 파일 업로드
	@SuppressWarnings("unchecked")
	public JSONObject fileImageUpload() {
		JSONObject resJSON = null; // 응답용 데이터
		
		int imagecount = ((Long)jsonupload.get("imagecount")).intValue(); 
		JSONArray imagearray = (JSONArray) jsonupload.get("array");
		byte[] decoded = null;		// 디코딩

		if(imagearray != null) {
			for(int i =0; i < imagearray.size(); i++) {
				decoded = Base64.getDecoder().decode((String)imagearray.get(i));
				try {
					BufferedImage image = ImageIO.read(new ByteArrayInputStream(decoded));
					ImageIO.write(image, "jpg", new File(filedir, imagecount+".jpg"));
					
					if(updateFilesPath() == null) return null;
					
					resJSON = new JSONObject(); 	// 응답용 데이터 객체 생성
					resJSON.put("result", 1);	// 결과값 
				} 
				catch (IOException e) { e.printStackTrace();}
			}
		}
		return resJSON;
	}
	
	// 데이터 베이스에 파일 경로 넣기
	public JSONObject updateFilesPath() {
		JSONObject jsonObject = null;
		if(path.equals("content")) {	// 게시글
			sql = "update content set files = \"" + filedirstring + "\" where content_num = " + pathnum + "&& files is null";
			jsonObject = dbsql.getPhoneUpdate(sql);
		}
		return jsonObject;
	}

}
