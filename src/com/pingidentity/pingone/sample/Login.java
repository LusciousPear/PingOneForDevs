package com.pingidentity.pingone.sample;


import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pingidentity.pingone.sample.app.FormAuth;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String path = getServletContext().getRealPath("/")+"/";
        FormAuth auth = FormAuth.getInstance();
        if(auth.Login(request, username, password)== true){
    		RequestDispatcher disp = getServletContext(  ).getRequestDispatcher("/index.jsp");
    		disp.forward(request, response);       	
        } else{
			RequestDispatcher disp = getServletContext(  ).getRequestDispatcher("/login.jsp");
			disp.forward(request, response);        	
        }
	}
}
