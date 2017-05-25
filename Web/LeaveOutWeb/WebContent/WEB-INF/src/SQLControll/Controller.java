package SQLControll;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import java.sql.*;
import javax.sql.*;

import org.apache.jasper.compiler.ServletWriter;

import javax.naming.*;

public class Controller extends HttpServlet {

	DataSource ds;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html; charset=euc-kr");
		PrintWriter out = response.getWriter();
		Connection conn = null;
		try {
			Context init = new InitialContext();
			ds = (DataSource) init.lookup("java:comp/env/jdbc/mysql");
			conn = ds.getConnection();
			out.println("성공");

		} catch (Exception e) {
			e.printStackTrace();
			out.println("실패");

		}
		out.close();
		
	}

}
