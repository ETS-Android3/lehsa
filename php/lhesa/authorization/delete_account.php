<?php
	require_once __DIR__.'/../utils/database/db_sql_util.php';
	require_once __DIR__.'/../utils/log/logger.php';
	
	function delete_account(array $request) {
		$user_name = $request['user_name'];

		log_message("Deleting the user '".$user_name."'");
		$sql = "DELETE FROM student_details WHERE user_name = ?";
		$res = prepared_query($sql, [$user_name]);
		if (!$res) {
			/* No rows matched -- do something else */
			$response["length"] = 0;
		}

		# student_course
		log_message("Deleting all courses from 'student_course' table corrresponding to the user '".$user_name."'");
		$sql1 = "DELETE FROM student_course WHERE user_name = ?";
		$res1 = prepared_query($sql1, [$user_name]);
		if (!$res1) {
			/* No rows matched -- do something else */
			log_message("Failed to all courses from 'student_course' table corrresponding to the user '".$user_name."'");
		} 

		# assignment_tracker
		log_message("Deleting all trackers from 'assignment_tracker' table corrresponding to the user '".$user_name."'");
		$sql2 = "DELETE FROM assignment_tracker WHERE user_name = ?";
		$res2 = prepared_query($sql2, [$user_name]);
		if (!$res2) {
			/* No rows matched -- do something else */
			log_message("Failed to all trackers from 'assignment_tracker' table corrresponding to the user '".$user_name."'");
		} 

		$response["length"] = 0;
		$result = json_encode([$response]);
		log_message("Result: '".$result);
		echo $result;
		close_db();
	}
?>