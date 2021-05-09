package zad1;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import javax.naming.*;
import java.sql.*;
import javax.sql.*;

public class Book extends HttpServlet {
	private static final long serialVersionUID = 4868398701057478220L;
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

		String bookId = req.getParameter("id");

		Connection connnection = null;
		try {
			synchronized(dataSource) {
				connnection = dataSource.getConnection();
			}

			Statement statement = connnection.createStatement();

			String query = "select title, author, overview from books WHERE id=" + bookId + " LIMIT 1;";
			ResultSet results = statement.executeQuery(query);

			boolean hasResult = results.next();

			if (hasResult) {
				String title = results.getString("title");
				String author = results.getString("author");
				String overview = results.getString("overview");
				responseOut.println("<h1>" + title + "</h1>");
				responseOut.println("<h2>" + author + "</h2>");

				if (overview == null) {
					responseOut.println("<div><i>No overview</i></div>");
				} else {
					responseOut.println("<div>" + overview + "</div>");
				}
			} else {
				responseOut.println("<div>Book not found</div>");
			}

			results.close();
			statement.close();
		} catch (Exception exc) {
			responseOut.println(exc.getMessage());
		} finally {
			try {
				connnection.close();
			} catch (Exception exc) {}
		}

		responseOut.close();
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		serviceRequest(request, response);
	}
}
