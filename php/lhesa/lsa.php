<?php
    require_once __DIR__.'/utils/log/logger.php';
    require_once __DIR__ . '/user/user_handler.php';
    require_once __DIR__ . '/authorization/authorization_handler.php';
    require_once __DIR__ . '/course/course_handler.php';
    require_once __DIR__ . '/faculty/faculty_handler.php';
    require_once __DIR__ . '/faq/faq_handler.php';
    require_once __DIR__ . '/tracker/tracker_handler.php';
    require_once __DIR__ . '/user/user_handler.php';
    require_once __DIR__ . '/chatbot/chatbot_handler.php';
    
    $cmd = $_POST['command'];
    log_message("Received command '" . $cmd . "'");
    switch ($cmd) {
        case "authorization": authorization_handler($_POST); break;
        case "course": course_handler($_POST); break;
        case "faculty": faculty_handler($_POST); break;
        case "faq": faq_handler($_POST); break;
        case "tracker": tracker_handler($_POST); break;
        case "user": user_handler($_POST); break;
        case "chatbot": chatbot_handler($_POST); break;
        default: log_message("The command '" . $cmd . "' is unsupported.");
    }
?>