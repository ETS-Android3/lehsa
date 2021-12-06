<?php
    require_once __DIR__.'/../utils/log/logger.php';
    require_once __DIR__.'/login.php';
    require_once __DIR__.'/register.php';
    require_once __DIR__.'/update_password.php';
    require_once __DIR__.'/delete_account.php';

    function authorization_handler(array $request)
    {  
        $sub_cmd = $request['sub_command'];
        switch ($sub_cmd) {
            case "login": login($request); break;
            case "register": register($request); break;
            case "update_password": update_password($request); break;
            case "delete_account": delete_account($request); break;
            default: log_message("The sub_command '" . $sub_cmd  . "' is unsupported.");
        }
    }
?>