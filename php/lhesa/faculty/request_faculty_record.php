<?php
	require_once __DIR__.'/../utils/database/db_sql_util.php';
	require_once __DIR__.'/../utils/log/logger.php';

	function request_faculty_record(array $request) {
		$input_id = $request['id'];

		log_message("Getting the faculty information the id=".$input_id);
		$sql = "SELECT * FROM faculty WHERE id=?";
		$all = prepared_select($sql, [$input_id])->fetch_all(MYSQLI_ASSOC);
		$result = json_encode($all);
		log_message("Result: '".$result);
		echo $result;
		close_db();
	}
?>
