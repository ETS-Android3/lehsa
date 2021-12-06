<?php
	require_once __DIR__.'/../utils/database/db_sql_util.php';
	require_once __DIR__.'/../utils/log/logger.php';

	function request_all_faculty_names(array $request) {
		log_message("Getting all Faculty names from 'faculty' table");

		$sql = "SELECT fac_name FROM faculty";
		$all = prepared_select($sql)->fetch_all(MYSQLI_ASSOC);
		$result = json_encode($all);
		log_message("Result: '".$result);
		echo $result;
		close_db();
	}
?>