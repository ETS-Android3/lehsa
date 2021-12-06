<?php
	require_once __DIR__.'/../utils/database/db_sql_util.php';
	require_once __DIR__.'/../utils/log/logger.php';

	function login(array $request) {
		$user_name = $request['user_name'];
        $password = $request['password'];
		
		// log_message("Input: '".$request);
		log_message("The user '".$user_name."' trying to login...");
		$sql = "SELECT * FROM student_details WHERE user_name = ? AND password = ?";
		$all = prepared_select($sql, [$user_name, $password])->fetch_all(MYSQLI_ASSOC);

		$result = json_encode($all);
		log_message("Result: '".$result);
		echo $result;
		close_db();
	}
?>