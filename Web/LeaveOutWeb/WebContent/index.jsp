<%@ page language="java" contentType="text/html; charset=EUC-KR"%>
<html>
<head>
    <title>LeaveOut</title>
	
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	
	<!-- Bootstrap Core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">
	
	<!-- Theme Fonts -->
    <link href="css/logintheme.css" rel="stylesheet" type="text/css">
	
	
    <script src="https://use.typekit.net/ayg4pcz.js"></script>
    <script>try{Typekit.load({ async: true });}catch(e){}</script>

</head>

<body>
  <script language="JavaScript" src="script.js"> </script>
    <div class="container">
    <h1 class="welcome text-center">LeaveOut에 로그인 해주세요.</h1>
        <div class="card card-container">
        <h2 class='login_title text-center'>로그인</h2>
        <hr>

            <form name="loginForm" class="form-signin" action="loginProcess.jsp" method="post">
                <span id="reauth-email" class="reauth-email"></span>
                <p class="input_title">아이디</p>
                <input type="text" name="inputId" id="inputId" class="login_box" placeholder="" required autofocus>
                <p class="input_title">비밀번호</p>
                <input type="password" name="inputPassword" id="inputPassword" class="login_box" placeholder="" required>
                <div id="remember" class="checkbox">
                    <label></label>
                </div>
                <button class="btn btn-lg btn-primary" type="submit">Login</button>
            </form><!-- /form -->
        </div><!-- /card-container -->
    </div><!-- /container -->
	
</body>
</html>