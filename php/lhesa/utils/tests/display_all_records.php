<?php
	require_once __DIR__ . '/../database/db_sql_util.php';
	require_once __DIR__ . '/../util.php';

	// Test script to print all records from the response table.
	$sql = "SELECT * FROM faculty";
	my_pretty_print("Response", fetch_all($sql));
	close_db();
 
?>