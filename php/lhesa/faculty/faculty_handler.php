<?php
    require_once __DIR__.'/../utils/log/logger.php';
    require_once __DIR__.'/request_all_ids.php';
    require_once __DIR__.'/request_faculty_record.php';
    require_once __DIR__.'/request_all_faculty_names.php';
    require_once __DIR__.'/request_faculty_record_by_name.php';
    
    function faculty_handler(array $request)
    {  
        $sub_cmd = $request['sub_command'];
        switch ($sub_cmd) {
            case "request_all_ids": request_all_ids($request); break;
            case "request_all_faculty_names": request_all_faculty_names($request); break;
            case "request_faculty_record": request_faculty_record($request); break;
            case "request_faculty_record_by_name": request_faculty_record_by_name($request); break;
            default: log_message("The sub_command '" . $sub_cmd  . "' is unsupported.");
        }
    }
?>