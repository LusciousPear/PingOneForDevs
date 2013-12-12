<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	if(com.pingidentity.pingone.sample.app.FormAuth.getInstance().isLoggedIn(request) == false){
		pageContext.forward("/login.jsp");	   
	}
%>    
    