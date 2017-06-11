package FileSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FileDownLoad {
	
	private JSONObject resJSON;
	private HttpServletRequest request;
	private String filedirstring;	// 서버 경로
	private String filedir;			// 실제 서버 절대 경로
	String realpath;				// 서버 root 까지 실제 경로
	String webpath;					// http 서버 주소

	
	public FileDownLoad(JSONObject resJSON, HttpServletRequest request) {
		this.resJSON = resJSON;
		this.request = request;
		realpath = request.getServletContext().getRealPath("/");								// 서버 root 까지 실제 경로
		webpath = request.getRequestURL().toString().replace(request.getRequestURI(),"/");		// http 서버 주소
	}
	
	@SuppressWarnings("unchecked")
	public void filesDownLoad(JSONObject jsondownload) {
		if(resJSON.get("result") instanceof JSONArray) {
			JSONArray array = (JSONArray) resJSON.get("result");
			
			for(int i = 0; i < array.size(); i++) {
				JSONObject data = (JSONObject) array.get(i);						// 결과 데이터 한개씩 가져오기
				filedirstring = (String) data.get(jsondownload.get("context"));		// 데이터베이스에서 만든 경로 가져오기
				
				if(filedirstring == null) return;			// 경로가 없으면 리턴
				data.remove(jsondownload.get("context"));	// 제이슨에서 만든 경로 데이터 삭제
				
				filedir = request.getServletContext().getRealPath(filedirstring);		// 실제 경로 + 폴더
				File dir = new File(filedir);				// 디렉토리 폴더 불러오기
				File[] filelist = dir.listFiles();			// 데렉토리 안에 파일객체로 불러오기
				
				JSONArray imagearray = new JSONArray();
				for(File file : filelist) {					// 파일 한개씩(loop)
					if(file.isFile()) {						// 파일 객체가 파일 확인
						if(file.getName().contains(".txt")) {		//파일 이름이 .txt가 있으면
							StringBuilder text = textRead(file);	// 파일에 내용 가져오기
							data.put("text", text.toString());		// 제이슨 데이터에 넣기
						}
						if(file.getName().contains(".jpg")) {		// 파일 이름이 jpg가 있으면
							StringBuilder imagepath = imageRead(file,realpath,webpath);		// 이미지 path로 넣기
							imagearray.add(imagepath.toString());
						}
					}	// if -- END
				}	// sub for -- END
				data.put("image", imagearray);
				
			}	// for -- END
		}
	}
	
	// text 파일 불러오기
	private StringBuilder textRead(File file) {
		StringBuilder text = new StringBuilder();
		BufferedReader in = null;

		try {
			in = new BufferedReader(new FileReader(file));		// 파일 불러오기
			String s;
			while ((s = in.readLine()) != null) text.append(s+"\n");			// 한 문장씩 가져오기
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {if(in != null) try { in.close();} catch (IOException e) {}}
		return text;
	}
	
	private StringBuilder imageRead(File file, String realpath, String webpath) {
		StringBuilder imagepath = new StringBuilder();
		String filepath = file.getPath().replace(realpath, "").replace("\\", "/");
		imagepath.append(webpath);
		imagepath.append(filepath);
		return imagepath;
	}
	


}
