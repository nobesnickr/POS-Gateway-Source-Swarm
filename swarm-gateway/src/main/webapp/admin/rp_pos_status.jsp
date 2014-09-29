
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<title>Retail Pro Admin Demo | Swarm-Mobile</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="../../../css/bootstrap.min.css" rel="stylesheet">
<link href="../../../css/pos-status.css" rel="stylesheet">
<script src="../../../js/pos-status.js"></script>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script>

	// Refresh dynamically loaded page parts
	function refresh() {

		// REST query for Retail Pro stores
		var rpApis = getSelectedValues('retailpro-api');
		var statusValues = getSelectedValues('status');
		
		if(rpApis.length == 0 || statusValues.length == null){
			showEmpty();
			return;
		}

		var rpUrl = '?api=' + rpApis.join() + "&status=" + statusValues.join();

		// Run AJAX queries, this function is async
		refreshAjaxContent("/swarm/api/admin/retailpro/stores", rpUrl, {
				"store_id" : "Store ID",
				"name" : "Store name",
				"api" : "API",
				"created" : "Created At",
				"swarm_id" : "Swarm ID",
				"timezone" : "Timezone",
				"status" : "Status"
		});
	}

	$(function() {
		refresh();
	});
</script>
<body>
	<div class="wrapper">
		<header>
			<nav class="header">
				<a href="../status">← Go to the Status Page</a>
			</nav>
		
			<div class="pull-right">
				<div class="info">
					<label for="version">Version:</label> <span id="version"></span>
				</div>
				<div class="info">
					<label for="time">Time:</label> <span id="time">${date}</span>
				</div>
			</div>

			<h1>Swarm Gateway Retail Pro Status Page</h1>
			<p>
				This page is a demo user interface for the status service API of the
				gateway. It demonstrates the basic features of the
				<code>retailpro/stores</code>
				API.
			</p>
			<h3>Filter results</h3>
			<div class="row">
				<div class="col-md-4">
					<h5>Retail Pro version</h5>
					<c:forEach var="row" items="${retailproApis}">
						<input type="checkbox" class="retailpro-api"
							value="${row.apiName}" checked="checked" id="api_${row.apiName}"
							onchange="refresh()" />
						<label for="api_${row.apiName}">${row.apiName}</label>
						<br />
					</c:forEach>
				</div>
				<div class="col-md-4">
					<h5>Status</h5>
					<c:forEach var="row" items="${statusValues}">
						<input type="checkbox" class="status" value="${row.value}"
							checked="checked" id="status_${row.value}" onchange="refresh()" />
						<label for="status_${row.value}">${row.value}</label>
						<br />
					</c:forEach>
				</div>
			</div>
		</header>
		<section class="main-block">
			<h3>Stores</h3>
			<div id="stores"></div>
			<p class="clearfix"></p>
		</section>
	</div>
</body>
</html>
