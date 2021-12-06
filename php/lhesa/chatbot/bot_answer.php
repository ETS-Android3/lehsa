<?php
	require_once __DIR__.'/../utils/database/db_sql_util.php';
	require_once __DIR__.'/../utils/log/logger.php';
	require_once __DIR__.'/wit_ai/wit_ai.php';

	function bot_answer(array $request) {
		$issues = $request['issues'];

		// log_message("Chatbot: Utterance '".$utterance."'");
		// $server_output = process_request_post($utterance);
		// log_message("Chatbot: WitAI response '".$server_output."'");

		log_message("WitAI Response, input '".$issues."'");
		// $server_decoded_rsp = json_decode($issues)->entities->{"issues:issues"};
		$server_decoded_rsp = json_decode($issues);
		log_message("server_decoded_rsp '".$server_decoded_rsp."'");
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
		log_message(json_encode(array(array("response" => $response))));
		echo json_encode(array(array("response" => $response)));
	}
?>
