<?php
/*
 *   Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.
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
 */

$usage = "Usage: php clear_store.php --store {store_id} --database {database_name} [--from] {date} [--all]";

// (S)tore is required, D(atabase) is required, F(rom) is optional, A(ll) is boolean
$options = getopt("s:d:f:a", array('store:', 'database:', 'all', 'from'));

/**
 * Read command line argument's value or exit if missing
 */
function read_option($short, $long){
	global $usage, $options;
	
	if(isset($options[$short])){
		return $options[$short];
	} else if(isset($options[$long])){
		return $options[$long];
	} else {
		print $usage . "\n";
		die("No -$short/--$long specified!\n");
	}
}

// Which store to use?
$store_id = 0 + read_option('s', 'store');
print "Executing query for store_id: $store_id\n";

// For deleting only invoices its optional to choose a "since"
// before which invoices are ignored
$since = 0;
$all = isset($options['a']) || isset($options['all']);

// Since only has meaning if $all == false
if(!$all && (isset($options['f']) || isset($options['from']))){
	$since = strtotime(read_option('f', 'from'));
	if(!$since){
		$since = 0;
	}
	print "Using time filter: " . date('Y-m-d G:i:s', $since) . "\n";
}

$known_hosts = array(
	'localhost' =>  array('host' => 'localhost', 'username' => 'swarm', 'password' =>'swarm', 'database'=> 'swarm' ),
	'sonrisa_dev' =>  array('host' => 'swarmposdata.cdmer9ay9s4r.us-west-1.rds.amazonaws.com', 'username' => 'posadmin', 'password' =>'dUdEph94aR5fr6', 'database'=> 'sonrisa_dev' ),
	'pos_production' =>  array('host' => 'swarmposdata.cdmer9ay9s4r.us-west-1.rds.amazonaws.com', 'username' => 'posadmin', 'password' =>'dUdEph94aR5fr6', 'database'=> 'pos_production' ),
);

// Which database to use ?
$database_key = read_option('d', 'database');
if(isset($known_hosts[$database_key])){
	$mysql_credentials = $known_hosts[$database_key];
} else {
	die("Allowed values for database are: " . print_r(array_keys($known_hosts), true) . "\n");
}

$db = new mysqli($mysql_credentials['host'], $mysql_credentials['username'], $mysql_credentials['password'], $mysql_credentials['database']);

/**
 * Execute query or exit with error message upon MySQL error
 */
function myquery($query){
	global $db;
	$result = $db -> query($query);
	if(!$result){
		print $query."\n";
		die($db->error."\n");
	}
	return $result;
}

print "Connection to $mysql_credentials[host] established\n";

// Show store data to user
$store_result = myquery("SELECT swarm_id, stores.store_id as store_ids, stores.api_id, apis.name as api_name, stores.name, ls_store_no, ls_sbs_no 
					     FROM stores 
						 LEFT JOIN stores_rp ON stores.store_id = stores_rp.store_id 
						 LEFT JOIN apis ON stores.api_id = apis.api_id
						 WHERE stores.store_id = $store_id");
						 
$store_row = $store_result->fetch_assoc();
print_r($store_row);
print "\n";

$allowed_apis = array('revel', 'shopify', 'retailpro8', 'retailpro9', 'merchantos_gw', 'erply', 'lightspeed_pro');
if(!in_array($store_row['api_name'], $allowed_apis)){
	print "Clear store script in not allowed for api: $store_row[api_name]\n";
	exit;
}

// Staging tables don't have store_id, this is needed
$swarm_id = $store_row['swarm_id'];
$ls_store_no = $store_row['ls_store_no'];
$ls_sbs_no = $store_row['ls_sbs_no'];

// Same as above but can be quoted to be instertable to SQL queries
$swarm_id_quo = $db->real_escape_string($store_row['swarm_id']);
$ls_store_no_quo = $db->real_escape_string($store_row['ls_store_no']);
$ls_sbs_no_quo = $db->real_escape_string($store_row['ls_sbs_no']);

// Show number of invoices to user
$result = myquery("SELECT COUNT(*) AS row_count FROM invoices WHERE store_id = $store_id AND ts > FROM_UNIXTIME($since)");
if($row = $result->fetch_assoc()){
	print "Found $row[row_count] invoices which will be deleted\n";
}

// Ask user to verify
if($all){
	print "WARNING: Script will delete all data for the store WARNING!\n";
}

echo "Are you sure you want to do this?  Type 'yes' to continue: ";
$handle = fopen ("php://stdin","r");
$line = strtolower(trim(fgets($handle)));
if($line != 'yes' && $line != 'y'){
    echo "ABORTING!\n";
    exit;
}

// Delete EVERYTHING
if($all){

	// Which staging entities are associated with this store?
	$staging_criteria = "store_id = $store_id";
	if($swarm_id){
		$staging_criteria .= " OR (swarm_id = '$swarm_id_quo' AND ls_sbs_no = '$ls_sbs_no_quo' AND ls_store_no = '$ls_store_no_quo')";
	}
	
	$file_id = "{$database_key}_{$store_id}_" . date('Ymd_His');

	// Which tables to delete?
	$tables = array('invoice_lines', 'invoices', 'products', 'customers', 'manufacturers', 'categories');
	$staging_tables = array('staging_invoice_lines', 'staging_invoices', 'staging_products', 'staging_customers');
	
	// Save content of legacy tables
	$legacy_tables = implode(' ',$tables);
	$filename = "~/sonrisa-sandbox/backup_{$file_id}_legacy.sql";
	$cmd = "mysqldump -h$mysql_credentials[host] -u$mysql_credentials[username] -p$mysql_credentials[password] $mysql_credentials[database] $legacy_tables --where \"store_id=$store_id\" > $filename";
	print "Executing:\n$cmd\n";
	system($cmd);	
	system("gzip $filename");
	
	// Save content of staging tables
	$staging_tables_str = implode(' ',$staging_tables);
	$filename = "~/sonrisa-sandbox/backup_{$file_id}_stage.sql";
	$cmd = "mysqldump -h$mysql_credentials[host] -u$mysql_credentials[username] -p$mysql_credentials[password] $mysql_credentials[database] $staging_tables_str --where \"$staging_criteria\" > $filename";
	print "Executing:\n$cmd\n";
	system($cmd);	
	system("gzip $filename");
	
	// Delete everything
	foreach($staging_tables as $table){
		myquery("DELETE FROM $table WHERE $staging_criteria");
	}
	
	foreach($tables as $table){
		myquery("DELETE FROM $table WHERE store_id = $store_id");
	}
	
	// For Retail Pro stores delete
	if($swarm_id){
		myquery("DELETE FROM stores_rp WHERE (store_id = $store_id AND (swarm_id = '$swarm_id_quo' AND ls_sbs_no = '$ls_sbs_no_quo' AND ls_store_no = '$ls_store_no_quo'))");
		myquery("DELETE FROM stores WHERE store_id = $store_id");
		
	// For Non-Retail Pro stores only set to inactive
	} else {
		myquery("UPDATE stores SET active = NULL where store_id = $store_id");
	}	
} else {
	// Delete invoices and invoice lines only
	$invoice_query = "SELECT invoice_id FROM invoices WHERE store_id = $store_id AND ts > FROM_UNIXTIME($since)";

	print "Deleting invoice_lines...\n";
	myquery("DELETE FROM staging_invoice_lines WHERE store_id = $store_id");
	myquery("DELETE FROM invoice_lines WHERE store_id = $store_id AND invoice_id IN ($invoice_query)");

	print "Deleting invoices...\n";
	myquery("DELETE FROM staging_invoices WHERE store_id = $store_id");
	myquery("DELETE FROM invoices WHERE store_id = $store_id AND ts > FROM_UNIXTIME($since)");
}

print "Finished...\n";

?>

