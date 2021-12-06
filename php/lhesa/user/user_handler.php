<?php
    require_once __DIR__.'/../utils/log/logger.php';
    require_once __DIR__.'/get_user_info.php';
    
    function user_handler(array $request)
    {  
        $sub_cmd = $request['sub_command'];
        switch ($sub_cmd) {
            case "get_user_info": get_user_info($request); break;
            default: log_message("The sub_command '" . $sub_cmd  . "' is unsupported.");
        }
    }
?>