<?php
	require_once __DIR__.'/../utils/database/db_sql_util.php';
	require_once __DIR__.'/../utils/log/logger.php';

	function add_user_tracker(array $request)
    {	
		$user_name = $request['user_name'];
		$course_number = $request['course_number'];
		$assignment = $request['assignment'];
		$due_date = trim($request['due_date']);
		$due_time = trim($request['due_time']);

		log_message("Adding a tracker for the user '".$user_name."' - '".$course_number."' - '".$assignment."' - '".$due_date."' - '".$due_time."'");
		$sql = "INSERT INTO assignment_tracker (user_name, course_number, assignment, due_date, due_time) VALUES(?, ?, ?, ?, ?)";
		$res = prepared_query($sql, [$user_name, $course_number, $assignment, $due_date, $due_time]);
				
		if ($res) {
			// success
			$response["length"] = 1;
		} else {
			/* No rows matched -- do something else */
			$response["length"] = 0;
			log_message("The failed to add 'assignment_tracker' with the user '".$course_number."': ".$assignment);
		} 
		$result = json_encode([$response]);
		log_message("Result: '".$result);
		echo $result;
		close_db();
	}
?>