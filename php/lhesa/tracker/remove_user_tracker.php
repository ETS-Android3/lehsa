<?php
	require_once __DIR__.'/../utils/database/db_sql_util.php';
	require_once __DIR__.'/../utils/log/logger.php';

	function remove_user_tracker(array $request)
    {	
		$user_name = $request['user_name'];
		$course_number = $request['course_number'];
		$assignment = $request['assignment'];
		$due_date = $request['due_date'];
		$due_time = $request['due_time'];

		log_message("Deleting a tracker for the user '".$user_name."' - '".$course_number."' - '".$assignment."' - '".$due_date."' - '".$due_time."'");
		$sql = "DELETE FROM assignment_tracker WHERE user_name = ? AND course_number = ? AND assignment = ? AND due_date = ? AND due_time = ?";
		$res = prepared_query($sql, [$user_name, $course_number, $assignment, $due_date, $due_time]);
	
		if ($res) {
			// success
			$response["length"] = 1;
		} else {
			/* No rows matched -- do something else */
			$response["length"] = 0;
			log_message("The failed to remove 'assignment_tracker' with the user '".$course_number."': ".$assignment);
		} 
		$result = json_encode([$response]);
		log_message("Result: '".$result);
		echo $result;
		close_db();
	}
?>