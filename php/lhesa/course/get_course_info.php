<?php
	require_once __DIR__.'/../utils/database/db_sql_util.php';
	require_once __DIR__.'/../utils/log/logger.php';

	function get_course_info(array $request)
    {
		$course_number = $request['course_number'];

		log_message("Getting course details for the course number - '".$course_number."'");
		$sql = "SELECT * FROM course_list WHERE course_number=?";
		$all = prepared_select($sql, [$course_number])->fetch_all(MYSQLI_ASSOC);

		$result = json_encode($all);
		log_message("Result: '".$result);
		echo $result;
		close_db();
	}
?>