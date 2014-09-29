
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
<title>Gateway Admin Demo | Swarm-Mobile</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="../../css/bootstrap.min.css" rel="stylesheet">
<link href="../../css/pos-status.css" rel="stylesheet">
<script src="../../js/pos-status.js"></script>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script>

	/**
	 * Set global value for checboxes of a certain css class
	 */
	function setValueForAll(selected, cssClass){
		if(selected){
			$('.' + cssClass).prop('checked', true);
	 	} else {
	 		$('.' + cssClass).removeAttr('checked');
	 	} 
		refresh();
	}

	// Refresh dynamically loaded page parts
	function refresh() {

		// Prepare URL for normal stores
		var apis = getSelectedValues('pull-api');
		var statusValues = getSelectedValues('status');
		var activeValues = getSelectedValues('active');
		
		// If either checkbox list is empty, then return set is going to be empty
		if(apis.length == 0 || statusValues.length == 0 || activeValues.length == 0){
			showEmpty();
			return;
		}

		var url = '?api='
				+ apis.join()
				+ "&status="
				+ statusValues.join()
				+ ((activeValues.length == 1) ? ("&active=" + activeValues[0])
						: "");

		// Run AJAX queries, this function is async
		refreshAjaxContent('/swarm/api/admin/stores', url, {
			"store_id" : "Store ID",
			"name" : "Store name",
			"api" : "API",
			"created" : "Created At",
			"active" : "Active",
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
				<a href="retailpro/status">← Go to the Retail Pro Status Page</a>
			</nav>
		
			<div class="pull-right">
				<div class="info">
					<label for="version">Version:</label> <span id="version"></span>
				</div>
				<div class="info">
					<label for="time">Time:</label> <span id="time">${date}</span>
				</div>
			</div>

			<h1>Swarm Gateway Status Page</h1>
			<p>
				This page is a demo user interface for the status service API of the
				gateway. It demonstrates the basic features of the <code>stores</code> API.
			</p>
			<p>
				Please note the status services use extensive caching to enhance
				their performance, but this may also result in <i>almost all</i>
				stores appearing to have ERROR status for brief interval when the
				gateway is restarting.
			</p>
			<h3>Filter results</h3>
			<div class="row">
				<div class="col-md-4">
					<h5>API</h5>
					<c:forEach var="row" items="${pullApis}">
						<input type="checkbox" class="pull-api" value="${row.apiName}"
							checked="checked" id="api_${row.apiName}" onchange="refresh()" />
						<label for="api_${row.apiName}">${row.apiName}</label>
						<br />
					</c:forEach>
					<a href="#" onclick="setValueForAll(true, 'pull-api');return false;">Select All</a> |
					<a href="#" onclick="setValueForAll(false, 'pull-api');return false;">Deselect All</a>
				</div>

				<div class="col-md-4">
					<h5>Active</h5>

					<input type="checkbox" class="active" value="true"
						checked="checked" id="api_true" onchange="refresh()" /> <label
						for="api_true">Active</label> <br /> <input type="checkbox"
						class="active" value="false" checked="checked" id="api_false"
						onchange="refresh()" /> <label for="api_false">Inactive</label> <br />
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
