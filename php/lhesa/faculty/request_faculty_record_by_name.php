<?php
	require_once __DIR__.'/../utils/database/db_sql_util.php';
	require_once __DIR__.'/../utils/log/logger.php';

	function request_faculty_record_by_name(array $request) {
		$fac_name = $request['fac_name'];

		log_message("Getting the faculty record for the faculty name=".$fac_name);
		$sql = "SELECT * FROM faculty WHERE fac_name=?";
		$all = prepared_select($sql, [$fac_name])->fetch_all(MYSQLI_ASSOC);
		$result = json_encode($all);
		// log_message("Result: '".$result);
		echo $result;
		close_db();
	}
?>
