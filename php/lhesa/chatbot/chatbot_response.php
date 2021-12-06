<?php
	require_once __DIR__.'/../utils/database/db_sql_util.php';
	require_once __DIR__.'/../utils/log/logger.php';
	require_once __DIR__.'/wit_ai/wit_ai.php';

	function chatbot_response(array $request) {
		$utterance = $request['utterance'];

		log_message("Chatbot: Utterance '".$utterance."'");
		$server_output = process_request_post($utterance);
		log_message("Chatbot: WitAI response '".$server_output."'");

		$server_decoded_rsp = json_decode($server_output)->entities->{"issues:issues"};
		$response = array();
		for ($i = 0; $i < count($server_decoded_rsp); $i++){
			$keyword = $server_decoded_rsp[$i]->value;
			$sql = "SELECT answer FROM chatbot WHERE keyword = ?";
			$result = prepared_select($sql, [$keyword]);
			$num_rows = mysqli_num_rows($result);
			if ($num_rows > 0) {
				$row = mysqli_fetch_array($result);
				$answer = $row[0];
				array_push($response, json_encode($answer));
			} 
			// If no rows mean key not found in the database. 
			// So don't return anything, and let the frontend handle everything.
		}
		close_db();
		// sleep(10); // To test the delayed response so that the frontend can verify the progress message in the input area.
		header('Content-type: application/json');
		echo json_encode($response);
	}
?>
