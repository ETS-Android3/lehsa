<?php
    require_once __DIR__.'/../utils/log/logger.php';
    // require_once __DIR__.'/login.php';

    function faq_handler(array $request)
    {  
        $sub_cmd = $request['sub_command'];
        switch ($sub_cmd) {
            case "faq": break;
            default: log_message("The sub_command '" . $sub_cmd  . "' is unsupported.");
        }
    }
?>