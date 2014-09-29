/**
 *  Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of
 *  Sonrisa Informatikai Kft. ("Confidential Information").
 *  You shall not disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Sonrisa.
 * 
 *  SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 *  THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *  TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *  PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 *  ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 *  DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 *  Javascript functions for the POS status demo
 * 
 */

/**
 * Shows empty table
 */
function showEmpty(){
	$('#stores').html('No stores match the criteria');
}

/**
 * Creates table from Javascript Array
 * @param rows Content to be displayed
 * @param columns Columns displayed from the rows of the first argument
 * @returns String of HTML
 */
function createTable(rows, columns) {
	
	// If no rows
	if(rows.length == 0){
		showEmpty();
		return;
	}
	
	var table = $('<table>');
	table.addClass('status-table');

	// Prepare header row
	var header = $('<tr>');
	for (column in columns) {
		var cell = $('<th>');
		cell.html(columns[column]);

		header.append(cell);
	}
	header.append($('<th>').html('Reason'));
	table.append(header);

	// For each store
	for (i in rows) {
		var row = $('<tr>');
		row.addClass('status-' + rows[i].status.toLowerCase());

		// Add column
		for (column in columns) {
			var cell = $('<td>');
			cell.addClass('cell-' + column);

			var value = rows[i][column];
			if (column == 'active') {
				value = ((value == 'true') ? 'Yes' : 'No');
			}
			
			// Trim values longer then 100 chars
			var maxLength = 100;
			if(value !== undefined && value.length > maxLength){
				value = "<div class='trimmed-value' onclick='$(this).html(" + JSON.stringify(value) + ");'>" + 
						value.substr(0, maxLength) + "...</div>";
			}

			cell.html(value);
			row.append(cell);
		}

		if (rows[i].reason !== undefined) {
			var reason = $('<ul>');

			for (j in rows[i].reason) {
				var value = rows[i].reason[j];
				
				var withSlashes = value.replace(/\\/g, '\\\\').
								        replace(/\t/g, '\\t').
								        replace(/\n/g, '\\n').
								        replace(/\r/g, '\\r').
								        replace(/'/g, '\\\'').
								        replace(/"/g, '\\"');

				var maxLength = 200;
				if (value !== undefined && value.length > maxLength) {
					value = "<div class='trimmed-value' onclick='$(this).html("+  withSlashes + ");'>" 
							+ value.substr(0, maxLength) + "...</div>";
				}

				var item = $('<li>');
				item.html(value);
				reason.append(item);
			}

			row.append($('<td>').append(reason));
		} else {
			row.append($('<td>'));
		}

		table.append(row);
	}

	return table;
}

/**
 * Get selected values for a checkboxs with the same class
 * @param cssClass CSS classes for the checkbox
 * @returns Array
 */
function getSelectedValues(cssClass) {
	var values = [];
	$('input.' + cssClass + ":checked").each(function() {
		values[values.length] = $(this).val();
	});

	return values;
}

/**
 * Refreshes the DOM element with the ID version with the current interface ]
 */
function refreshVersion(){

	// REST query for not-RetailPro stores
	$.ajax({
		url : '/swarm/api/version',
		type : "GET",
		success : function(data) {
			$('#version').html(data.interfaceVersion);
		},
		error : function(request) {
			var data = JSON.parse(request.responseText);
			$('#result_body').html(JSON.stringify(data, null, 4));
		}
	});
}


/**
 * Execute various requests to update page content
 * @param url Query for stores, e.g. api=shopify&order_by=created_at
 * @param rpUrl Query for the Retail Pro stores, e.g. swarm_id=nashua
 * @returns void
 */
function refreshAjaxContent(baseUrl, url, tableMeta) {

	var loading_html = '<p style="text-align: center"><img src="/swarm/loading.gif" alt="Loading..." style="margin: auto; text-align: center;" /></p>';

	// Refresh version box
	refreshVersion();

	// Replace with "Loading" GIF
	$('#stores').html(loading_html);

	// REST query
	$.ajax({
		url : baseUrl + url,
		type : "GET",
		success : function(data) {
			var table = createTable(data.stores, tableMeta);
			$('#result_body').html('');
			$('#stores').html(table);
		},
		error : function(request) {
			var data = JSON.parse(request.responseText);
			$('#result_body').html(JSON.stringify(data, null, 4));
		}
	});
}