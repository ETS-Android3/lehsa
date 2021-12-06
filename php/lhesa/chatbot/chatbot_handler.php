<?php
    require_once __DIR__.'/../utils/log/logger.php';
    require_once __DIR__.'/chatbot_response.php';
    require_once __DIR__.'/bot_answer.php';
    
    function chatbot_handler(array $request)
    {  
        $sub_cmd = $request['sub_command'];
        switch ($sub_cmd) {
            case "chatbot_response": chatbot_response($request); break;
            case "bot_answer": bot_answer($request); break;
            default: log_message("The sub_command '" . $sub_cmd  . "' is unsupported.");
        }
    }
?>