<?php
	require_once __DIR__.'/../utils/database/db_sql_util.php';
	require_once __DIR__.'/../utils/log/logger.php';
	
	function update_password(array $request) {
		$user_name = $request['user_name'];
		$password = $request['password'];

		log_message("Resetting the password for '".$user_name."'");
		$sql = "UPDATE student_details SET password=? WHERE user_name=?";
		$affected_rows = prepared_query($sql, [$password, $user_name])->affected_rows;
		$record[] = array("affetced_rows"=>$affected_rows);

		$result = json_encode($record);
		log_message("Result: '".$result);
		echo $result;
		close_db();
	}
?>