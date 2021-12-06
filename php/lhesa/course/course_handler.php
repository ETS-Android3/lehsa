<?php
    require_once __DIR__.'/../utils/log/logger.php';
    require_once __DIR__.'/get_course_info.php';
    require_once __DIR__.'/get_all_course_numbers.php';
    require_once __DIR__.'/get_user_courses.php';
    require_once __DIR__.'/update_user_courses.php';

    function course_handler(array $request)
    {  
        $sub_cmd = $request['sub_command'];
        switch ($sub_cmd) {
            case "get_course_info": get_course_info($request); break;
            case "get_all_course_numbers": get_all_cource_numbers($request); break;
            case "get_user_courses": get_user_courses($request); break;
            case "update_user_courses": update_user_courses($request); break;
            default: log_message("The sub_command '" . $sub_cmd  . "' is unsupported.");
        }
    }
?>