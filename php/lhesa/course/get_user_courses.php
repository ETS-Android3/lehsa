<?php
	require_once __DIR__.'/../utils/database/db_sql_util.php';
	require_once __DIR__.'/../utils/log/logger.php';

	function get_user_courses(array $request)
    {
		$user_name = $request['user_name'];

		log_message("Getting all course numbers for the user '".$user_name."'");
		$sql = "SELECT * FROM student_course WHERE user_name=?";
		$all = prepared_select($sql, [$user_name])->fetch_all(MYSQLI_ASSOC);
		$result = json_encode($all);
		log_message("Result: '".$result);
		echo $result;
		close_db();
	}
?>