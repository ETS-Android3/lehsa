<?php
    date_default_timezone_set('EST');
    function log_message($msg) {
        error_log('['.date("F j, Y, g:i a e O").'] LSA: '.$msg."\n", 3, "./lsa.log");
    }
?>