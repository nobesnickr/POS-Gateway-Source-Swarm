<!--
  Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.

 This software is the confidential and proprietary information of
 Sonrisa Informatikai Kft. ("Confidential Information").
 You shall not disclose such Confidential Information and shall use it only in
 accordance with the terms of the license agreement you entered into
 with Sonrisa.

 SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
-->
<%@page contentType="text/html" pageEncoding="UTF-8" isELIgnored="false"%>
<!DOCTYPE html>
<html>
<head>
<title>Kounta Point-of-Sale Installation | Swarm-Mobile</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="../css/bootstrap.min.css" rel="stylesheet">
<style type="text/css">
div.main-block {
	max-width: 900px;
	margin: 15px auto;
	padding: 30px;
	background-color: #f2f2ff;
}
div#button_container {
	max-width: 350px;
	margin: 40px auto;
}

div#button_container button.btn {
	font-size: 25px;
}

</style>
</head>
<body>
	<div class="main-block">
		<h1>
			Kounta Point-of-Sale Integration
		</h1>

		<h4>
			by <a href="http://swarm-mobile.com">Swarm-Mobile</a>
		</h4>
		
		<div class="col-sm-11">
		
			<p>Connect your Kounta sales data to your Swarm account. By
				installing the add-on you'll be able to track the
				transactions and revenue of your store.</p>
				
		</div>

		<div class="col-sm-11">
			<div class="well" id="button_container">
				<form method="get" action="${authorizeUrl}">
						<input type="hidden" name="client_id" value="${clientId}" />
						<input type="hidden" name="response_type" value="code"/>
						<input type="hidden" name="redirect_uri" value="${redirectUri}" />
						<input type="hidden" name="scope" value="" />
						<input type="hidden" name="state" value="1" />
						<button type="submit" class="btn btn-primary btn-block">Install</button>
				</form>
			</div>
		</div>
		
		<p class="clearfix"></p>
		
		<div class="col-sm-11">
			You have to be logged in at your Kounta account before clicking Install. 
			<a href="https://my.kounta.com/" target="_default">You can do this here.</a>
		</div>

		<p class="clearfix"></p>
	</div>
</body>
</html>
