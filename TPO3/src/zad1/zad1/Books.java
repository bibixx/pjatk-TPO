package zad1;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import javax.naming.*;
import java.sql.*;
import javax.sql.*;

public class Books extends HttpServlet {
	private static final long serialVersionUID = -736950681058681531L;
	DataSource dataSource;

	public void init() throws ServletException {
		try {
			Context init = new InitialContext();
			Context contx = (Context) init.lookup("java:comp/env");
			dataSource = (DataSource) contx.lookup("jdbc/booksdb");
		} catch (NamingException exc) {
			throw new ServletException("Cannot connect to the DB", exc);
		}
	}

	public void serviceRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html; charset=utf-8");

		PrintWriter responseOut = res.getWriter();
		responseOut.println("<h1>Lista dostępnych książek</h1>");
		responseOut.println("<form>");
		responseOut.println("  <label>");
		responseOut.println("    <p>Nazwa:</p>");
		responseOut.println("    <input type=\"text\" name=\"searchedBook\" />");
		responseOut.println("  </label>");
		responseOut.println("  <br />");
		responseOut.println("  <input type=\"submit\" value=\"Szukaj\" />");
		responseOut.println("</form>");

		String searchedBook = req.getParameter("searchedBook");

		Connection connection = null;

		try {
			synchronized(dataSource) {
				connection = dataSource.getConnection();
			}
			Statement statement = connection.createStatement();

			String query = "select * from books";

			if (searchedBook != null) {
				query = "select id, title, author from books WHERE title LIKE \"%" + searchedBook + "%\"";
			}

			ResultSet results = statement.executeQuery(query);
			responseOut.println("<ol>");

			while (results.next()) {
				int id = results.getInt("id");
				String title = results.getString("title");
				String author = results.getString("author");
				responseOut.println("<li><a href=\"Book?id=" + id + "\">" + title + ", " + author + "</li>");
			}

			responseOut.println("</ol>");

			results.close();
			statement.close();
		} catch (Exception exc) {
			responseOut.println(exc.getMessage());
		} finally {
			try {
				connection.close();
			} catch (Exception exc) {}
		}

		responseOut.close();
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		serviceRequest(request, response);
	}
}
