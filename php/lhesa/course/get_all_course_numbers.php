<?php
	require_once __DIR__.'/../utils/database/db_sql_util.php';
	require_once __DIR__.'/../utils/log/logger.php';
	
	function get_all_cource_numbers(array $request)
    {
		log_message("Getting all course numbers");
		$sql = "SELECT course_number FROM course_list";
		$all = prepared_select($sql)->fetch_all(MYSQLI_ASSOC);

		$result = json_encode($all);
		log_message("Result: '".$result);
		echo $result;
		close_db();
	}
?>