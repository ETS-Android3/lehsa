<?php
	require_once __DIR__.'/../utils/database/db_sql_util.php';
	require_once __DIR__.'/../utils/log/logger.php';

	function update_user_courses(array $request)
    {	
		$user_name = $request['user_name'];
		$add_course_numbers = $request['add_course_numbers_string']; // it's string of courses to regiter multiple courses at a time
		$remove_course_numbers = $request['remove_course_numbers_string']; // it's string of courses to unregiter multiple courses at a time
		
		# Add check
		$add_array = explode(',', $add_course_numbers); 
		foreach($add_array as $course_number) {
			if (!(empty(trim($course_number)))) {
				log_message("Registering new course for the user '".$user_name."': '".$course_number."'");
				$sql = "INSERT INTO student_course (user_name, course_number) VALUES(?, ?)";
				$res = prepared_query($sql, [$user_name, $course_number]);	
				if (!$res) {
					log_message("The 'student_course' table already contains the record for the user'".$user_name."': ".$course_number);
				}
			}
		}

		# Remove check
		$remove_array = explode(',', $remove_course_numbers); 
		foreach($remove_array as $course_number) {
			if (!(empty(trim($course_number)))) {
				log_message("Unregistering the course for the user '".$user_name."': '".$course_number."'");
				$sql = "DELETE FROM student_course WHERE user_name = ? AND course_number = ?";
				$res = prepared_query($sql, [$user_name, $course_number]);
				if (!$res) {
					/* No rows matched -- do something else */
					log_message("The failed to delete 'student_course' with the user'".$user_name."': ".$course_number);
				}

				// Remove all trackers corresponding to the course
				log_message("Deleting a tracker for the user '".$user_name."' - '".$course_number."'");
				$sql1 = "DELETE FROM assignment_tracker WHERE user_name = ? AND course_number = ?";
				$res1 = prepared_query($sql1, [$user_name, $course_number]);
			
				if (!$res1) {
					/* No rows matched -- do something else */
					log_message("The failed to remove 'assignment_tracker' with the user '".$user_name."' - '".$course_number."'");
				} 
			}
		}
		
		// Always success
		$response["length"] = 1;
		$result = json_encode([$response]);
		log_message("Result: '".$result);
		echo $result;
		close_db();
	}
?>