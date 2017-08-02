package login;

import java.sql.*;
import java.util.*;

public class MemberMgr {

    private DBConnectionMgr pool = null;

    public MemberMgr() {
        try{
            pool = DBConnectionMgr.getInstance();
        }catch(Exception e){
            System.out.println("Error : 커넥션 가져오기 실패!!");
        }
    }

    public boolean checkId(String id) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean checkCon = false;
        try {
            con = pool.getConnection();
            String strQuery = "select id from member where id = ?";
            pstmt = con.prepareStatement(strQuery);
            pstmt.setString(1,id);
            rs = pstmt.executeQuery();
            checkCon = rs.next();

        }catch(Exception ex) {
            System.out.println("Exception" + ex);
        }finally{
            pool.freeConnection(con,pstmt,rs);
        }
        return checkCon;
    }

    public Vector zipcodeRead(String area3)  {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector vecList = new Vector();
        try {
            con = pool.getConnection();
            String strQuery = "select * from zipcode where area3 like '"+area3+"%'";
            pstmt = con.prepareStatement(strQuery);
            rs = pstmt.executeQuery();
            while(rs.next()){
                ZipcodeBean tempZipcode = new ZipcodeBean();
                tempZipcode.setZipcode(rs.getString("zipcode"));
                tempZipcode.setArea1(rs.getString("area1"));
                tempZipcode.setArea2(rs.getString("area2"));
                tempZipcode.setArea3(rs.getString("area3"));
                tempZipcode.setArea4(rs.getString("area4"));
                vecList.addElement(tempZipcode);
            }

        }catch(Exception ex) {
            System.out.println("Exception" + ex);
        }finally{
             pool.freeConnection(con,pstmt,rs);
        }
        return vecList;
    }

    public boolean memberInsert(RegisterBean regBean) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean flag = false;
        try {
            con = pool.getConnection();
            String strQuery = "insert into member values(?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(strQuery);
            pstmt.setString(1,regBean.getMem_id());
            pstmt.setString(2,regBean.getMem_passwd());
            pstmt.setString(3,regBean.getMem_name());
            pstmt.setString(4,regBean.getMem_num1());
            pstmt.setString(5,regBean.getMem_num2());
            pstmt.setString(6,regBean.getMem_email());
            pstmt.setString(7,regBean.getMem_phone());
            pstmt.setString(8,regBean.getMem_zipcode());
            pstmt.setString(9,regBean.getMem_address());
            pstmt.setString(10,regBean.getMem_job());
            int count = pstmt.executeUpdate();

            if(count>0){
                flag = true;
            }
        }catch(Exception ex) {
            System.out.println("Exception" + ex);
        }finally{
             pool.freeConnection(con,pstmt,rs);
        }
        return flag;
    }

    public boolean loginCheck(String id, String passwd)  {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean loginCon = false;
        try {
            con = pool.getConnection();
            String strQuery = "select id, passwd from member where id = ? and passwd = ?";
            pstmt = con.prepareStatement(strQuery);
            pstmt.setString(1,id);
            pstmt.setString(2,passwd);
            rs = pstmt.executeQuery();
            loginCon = rs.next();
        }catch(Exception ex) {
            System.out.println("Exception" + ex);
        }finally{
             pool.freeConnection(con,pstmt,rs);
        }
        return loginCon;
    }

}
