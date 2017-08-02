package board;

import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;

public class BoardMgr {

	private DBConnectionMgr pool = null;

    public BoardMgr() {
	   try{
			pool = DBConnectionMgr.getInstance();
	   }catch(Exception e){
			System.out.println("Error : 커넥션 가져오기 실패!!");
		}
    }

	public Vector getBoardList(String keyField, String keyWord) {
          Connection con = null;
		  Statement stmt = null;
		  ResultSet rs = null;
		  String strQuery = null;
		  Vector boardList = new Vector();
   		  
		  try {	 
			 con = pool.getConnection();
			 stmt = con.createStatement();
			 if(keyWord.equals("null") || keyWord.equals("")){
			   strQuery = "select * from board order by pos asc";
			 }else{
			   strQuery = "select * from board where "
			   +keyField+" like '%" + keyWord +"%' order by pos asc";
			 }
			rs = stmt.executeQuery(strQuery);
			while (rs.next()) {
			BoardBean tempBean = new BoardBean();

			tempBean.setNum(rs.getInt("num"));
			tempBean.setName(rs.getString("name"));
			tempBean.setEmail(rs.getString("email"));
			tempBean.setHomepage(rs.getString("homepage"));
			tempBean.setSubject(rs.getString("subject")); 
			tempBean.setContent(rs.getString("content"));
			tempBean.setPos(rs.getInt("pos"));
			tempBean.setDepth(rs.getInt("depth"));
			tempBean.setRegdate(rs.getString("regdate"));
			tempBean.setPass(rs.getString("pass"));
			tempBean.setCount(rs.getInt("count"));
			boardList.addElement(tempBean);
			
			}
		  }catch(Exception ex){
			 System.out.println("Exception" + ex);
		  }finally{
	        pool.freeConnection(con,stmt,rs);	  
		  }
		return boardList;
	}

	public void insertBoard(BoardBean boardBean){
		  Connection con = null;
		  PreparedStatement pstmt = null;
		  upPos();
		  
		  try {
			con = pool.getConnection();
			String strQuery = "insert into board" 
					+ "(name,email,homepage,subject,content,pos,depth,regdate,pass,count,ip)" 
					+ " values(?,?,?,?,?,?,?,NOW(),?,?,?)"; 
																			 
					pstmt = con.prepareStatement(strQuery);
					pstmt.setString(1,boardBean.getName());
					pstmt.setString(2,boardBean.getEmail());
					pstmt.setString(3,boardBean.getHomepage());
					pstmt.setString(4,boardBean.getSubject());
					pstmt.setString(5,boardBean.getContent());
					pstmt.setInt(6,boardBean.getPos());
					pstmt.setInt(7,boardBean.getDepth());
					pstmt.setString(8,boardBean.getPass());
					pstmt.setInt(9,boardBean.getCount());
					pstmt.setString(10,boardBean.getIp());
					pstmt.executeUpdate();

		  }catch(Exception ex) {
				System.out.println("Exception" + ex);
		  }finally{
	         pool.freeConnection(con,pstmt);		  
		  }
	}

	public void upPos(){ 
		  Connection con = null;
		  Statement stmt = null;
		  
		  try{
			con = pool.getConnection();
			stmt=con.createStatement();
			String strQuery="update board set pos=pos+1";
			stmt.executeUpdate(strQuery);
		  }catch(Exception ex) {
				System.out.println("Exception" + ex);
	      }finally{
	        pool.freeConnection(con,stmt);		  
		  }
	}

	public BoardBean getBoard(int num) { 
		  Connection con = null;
		  PreparedStatement pstmt = null;
		  ResultSet rs = null;
		  upCount(num);
		  BoardBean tempBean = new BoardBean();
		
		  try {
			con = pool.getConnection();
			String strQuery = "select * from board where num = ?";
			pstmt = con.prepareStatement(strQuery);
			pstmt.setInt(1,num);
			rs = pstmt.executeQuery();
		    while (rs.next()) {
			
				tempBean.setNum(rs.getInt("num"));
				tempBean.setName(rs.getString("name"));
				tempBean.setEmail(rs.getString("email"));
				tempBean.setHomepage(rs.getString("homepage"));
				tempBean.setSubject(rs.getString("subject")); 
				tempBean.setContent(rs.getString("content"));
				tempBean.setPos(rs.getInt("pos"));
				tempBean.setDepth(rs.getInt("depth"));
				tempBean.setRegdate(rs.getString("regdate"));
				tempBean.setPass(rs.getString("pass")); 
				tempBean.setCount(rs.getInt("count"));
				tempBean.setIp(rs.getString("ip"));
			}
		  }catch(Exception ex) {
			  System.out.println("Exception" + ex);
		  }finally{
	         pool.freeConnection(con,pstmt,rs);	  
		  }
		return tempBean;
	}

	public void upCount(int num) { 
		  Connection con = null;
		  PreparedStatement pstmt = null;
		
		  try{
			con = pool.getConnection();
			String strQuery="update board set count=count+1 where num= ?";
			pstmt = con.prepareStatement(strQuery);
			pstmt.setInt(1,num);
			pstmt.executeUpdate();
		  }catch(Exception ex) {
			System.out.println("Exception" + ex);
	      }finally{
	        pool.freeConnection(con,pstmt);		  
		  }
	}

	public void deleteBoard(int num) {  
		  Connection con = null;
		  PreparedStatement pstmt = null;
		
		  try{
			con = pool.getConnection();
			String strQuery = "delete from board where num = ?";
			pstmt = con.prepareStatement(strQuery);
			pstmt.setInt(1,num);
			pstmt.executeUpdate();
		  }catch(Exception ex) {
		      System.out.println("Exception" + ex);
		  }finally{
	        pool.freeConnection(con,pstmt);		  
		  }
	}

	public void updateBoard(BoardBean boardBean){
		  Connection con = null;
		  PreparedStatement pstmt = null;
		
	      try{
			con = pool.getConnection();
			String strQuery = "update board set "
							+ "name=?,email=?,homepage=?,subject=?,content=?" 
							+ "where num=?";
					
			pstmt = con.prepareStatement(strQuery);
			pstmt.setString(1,boardBean.getName());
			pstmt.setString(2,boardBean.getEmail());
			pstmt.setString(3,boardBean.getHomepage());
			pstmt.setString(4,boardBean.getSubject());
			pstmt.setString(5,boardBean.getContent());
			pstmt.setInt(6,boardBean.getNum());
			pstmt.executeUpdate();
			}catch(Exception ex) {
			System.out.println("Exception" + ex);
			}finally{
	           pool.freeConnection(con,pstmt);		  
		  }
	}

	public void replyupMyBoard(BoardBean reBoardBean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try{
			con = pool.getConnection();
			int pos = reBoardBean.getPos();
			String strQuery = "update board set pos = pos + 1 where pos > ?";
			pstmt = con.prepareStatement(strQuery);
			pstmt.setInt(1,pos);
			pstmt.executeUpdate();
		}catch(Exception ex) {
			System.out.println("Exception" + ex);
		}finally{
	        pool.freeConnection(con,pstmt);		  
		}
	}

	public void replyMyBoard(BoardBean reBoardBean){
		  Connection con = null;
		  PreparedStatement pstmt = null;
		
		  try{
			con = pool.getConnection();
			int depth = reBoardBean.getDepth() + 1;
			int pos = reBoardBean.getPos()+1;
			String strQuery = "insert into board" 
				   + "(name,email,homepage,subject,content,pos,depth,regdate,pass,count,ip)" 
				   + " values (?,?,?,?,?,?,?,NOW(),?,?,?)";
																  
					pstmt = con.prepareStatement(strQuery);
					pstmt.setString(1,reBoardBean.getName());
					pstmt.setString(2,reBoardBean.getEmail());
					pstmt.setString(3,reBoardBean.getHomepage());
					pstmt.setString(4,reBoardBean.getSubject());
					pstmt.setString(5,reBoardBean.getContent());
					pstmt.setInt(6,pos);
					pstmt.setInt(7,depth);
					pstmt.setString(8,reBoardBean.getPass());
					pstmt.setInt(9,reBoardBean.getCount());
					pstmt.setString(10,reBoardBean.getIp());
					pstmt.executeUpdate();
							
		  }catch(Exception ex) {
			 System.out.println("Exception" + ex);
		  }finally{
	         pool.freeConnection(con,pstmt);		  		  
		  }
	  }
}
