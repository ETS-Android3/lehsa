<?php
    $wit_ait_config = require __DIR__.'/wit_ai_config.php';

    function process_request_post($utterance)
    {
        global $wit_ait_config;

        $witURL = $wit_ait_config['witRoot'] . 
                "v=" . $wit_ait_config['witVersion'] . 
                "&q=" . urlencode($utterance);

        $ch = curl_init();
        $header = array();
        $header[] = 'Authorization: Bearer '. $wit_ait_config['witToken'];

        curl_setopt($ch, CURLOPT_URL, $witURL);

        // Configure to use POST Request (1 = TRUE)
        // curl_setopt($ch, CURLOPT_POST, 1);
        
        // Set the heade value for wit.ai authentication
        curl_setopt($ch, CURLOPT_HTTPHEADER, $header);

        // Configure to inhibit the immediate display of the returned data
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

        // Execute the curl command to fetch the Keywords for a user utterance
        $server_output = curl_exec($ch);

        // close the active connection
        curl_close($ch);

        return $server_output;
    }
?>