<?php
	require_once __DIR__.'/../utils/database/db_sql_util.php';
	require_once __DIR__.'/../utils/log/logger.php';

	function register(array $request) {
		$first_name = $request['first_name'];
		$last_name = $request['last_name'];
		$user_name = $request['user_name'];
		$password = $request['password'];

		log_message("Registering '".$first_name."', '".$last_name."'");
		$sql = "INSERT INTO student_details (first_name, last_name, user_name, password) VALUES(?, ?, ?, ?)";
		$res = prepared_query($sql, [$first_name, $last_name, $user_name, $password]);
		if ($res) {
			// success
			$response["length"] = 1;
		} 
		else {
			/* No rows matched -- do something else */
			$response["length"] = 0;
		}   
		
		$result = json_encode([$response]);
		log_message("Result: '".$result);
		echo $result;
		close_db();
	}
?>