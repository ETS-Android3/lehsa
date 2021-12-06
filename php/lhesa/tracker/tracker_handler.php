<?php
    require_once __DIR__.'/../utils/log/logger.php';
    require_once __DIR__.'/add_user_tracker.php';
    require_once __DIR__.'/remove_user_tracker.php';
    require_once __DIR__.'/get_user_trackers.php';
    
    function tracker_handler(array $request)
    {  
        $sub_cmd = $request['sub_command'];
        switch ($sub_cmd) {
            case "get_user_trackers": get_user_trackers($request); break;
            case "add_user_tracker": add_user_tracker($request); break;
            case "remove_user_tracker": remove_user_tracker($request); break;
            default: log_message("The sub_command '" . $sub_cmd  . "' is unsupported.");
        }
    }
?>