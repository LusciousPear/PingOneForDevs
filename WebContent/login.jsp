<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<html lang="en">
  <head>
  	<%@ include file="templates/standard_head.jsp" %>
    <title>My Little Pony Login</title>
  </head>
<%@ include file="templates/standard_body_start.jsp"%>
	<%
		String error = (String)session.getAttribute("login_error");
		if(error != null && error.length() > 0) {
			session.removeAttribute("login_error");
	%>
	<div class="alert alert-warning">
  		<strong>Login Failure: </strong> Either the user name or password is incorrect	
	</div>
	
	<%} %>
      <form class="form-signin" action="Login" method="post">
        <h2 class="form-signin-heading">Please sign in</h2>
        <input class="form-control" name="username" placeholder="Email address" required="" autofocus="" type="text">
        <input class="form-control" name="password" placeholder="Password" required="" type="password">
        <label class="checkbox">
          <input value="remember-me" type="checkbox"> Remember me
        </label>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
      </form> 
      <p/>
      <p/>
	<div class="alert alert-info">
	<center><strong>Try one of these</strong></center>
	<strong>Admin:</strong> joe@customer.com, 2Federate<br/>
	<strong>User:</strong> sue@customer.com, 2Federate<br/>
	</div>
<%@ include file="templates/standard_body_end.jsp" %>
</html>