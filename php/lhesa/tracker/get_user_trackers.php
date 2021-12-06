<?php
	require_once __DIR__.'/../utils/database/db_sql_util.php';
	require_once __DIR__.'/../utils/log/logger.php';

	function get_user_trackers(array $request)
    {
		$user_name = $request['user_name'];

		log_message("Getting all tracker details for the user '".$user_name."'");
		$sql = "SELECT * FROM assignment_tracker WHERE user_name=?";
		$all = prepared_select($sql, [$user_name])->fetch_all(MYSQLI_ASSOC);
		$result = json_encode($all);
		log_message("Result: '".$result);
		echo $result;
		close_db();
	}
?>